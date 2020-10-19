package org.gms;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SetupArguments(args);
        Topology topology = buildTopology();
        Properties props = buildProperties();
        WaitForInputTopics(props);

        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static Properties buildProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, Arguments.ApplicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, Arguments.AutoOffsetResetConfig);
        props.put(Arguments.SCHEMA_REGISTRY, Arguments.SchemaRegistryURL);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        ArrayList<KTable<String, GenericRecord>> tables = KeySharedTableGenerator(builder);
        KTable<String, GenericRecord> joined = InnerJoinKTables(tables);
        joined.toStream().to(Arguments.OutputTopicName);

        return builder.build();
    }

    private static KTable<String, GenericRecord> InnerJoinKTables(ArrayList<KTable<String, GenericRecord>> tables) {
        KTable<String, GenericRecord> joined = tables.get(0);
        for (int i = 1; i < tables.size(); i++) {
            joined = joined.join(tables.get(i), (left,right) -> MergeMessages(left, right) );
        }
        return joined;
    }

    private static GenericRecord MergeMessages(GenericRecord left, GenericRecord right) {
        JSONObject mergedValues = MergeValues(left, right);
        if (Arguments.OutputRecord==null) Arguments.OutputRecord = GenerateGenericRecord(left, right);
        GenericRecord mergedGenericRecord = PopulateGenericRecord(mergedValues);
        mergedGenericRecord.getSchema();
        return mergedGenericRecord;
    }

    private static Schema MergeSchema(GenericRecord left, GenericRecord right) {
        String rightSchemaString = right.getSchema().toString();
        List<Field> rightFields = right.getSchema().getFields();
        List<Field> leftFields = left.getSchema().getFields();
        ArrayList<Field> mergedFields = new ArrayList();
        rightFields.forEach(field -> mergedFields.add(new Field(field.name(), field.schema(), field.doc(), field.defaultVal())));

        for (Field field : leftFields) {
            if (!rightSchemaString.contains(field.name())) {
                Field newField = new Field(field.name(), field.schema(), field.doc(), field.defaultVal());
                mergedFields.add(newField);
            }
        }
        Schema mergedSchema = Schema.createRecord("Claim", "Claim Record", "org.gms", false, mergedFields);
        return mergedSchema;
    }

    private static JSONObject MergeValues(GenericRecord left, GenericRecord right) {
        JSONObject leftJSON = new JSONObject(left.toString());
        JSONObject rightJSON = new JSONObject(right.toString());
        leftJSON.keys().forEachRemaining(k -> {
            if (!rightJSON.has(k)) {
                rightJSON.put(k, leftJSON.get(k));
            }
        });
        return rightJSON;
    }

    private static GenericRecord GenerateGenericRecord(GenericRecord left, GenericRecord right) {
        return new GenericData.Record(MergeSchema(left, right));
    }

    private static GenericRecord PopulateGenericRecord(JSONObject values) {
        GenericRecord record = Arguments.OutputRecord;
        values.keys().forEachRemaining(k -> {
            record.put(k, values.get(k));
        });
        return record;
    }

    private static String SetKey(GenericRecord value, String commonKey) {
        if (value==null) return null;
        else return value.get(commonKey).toString();
    }

    private static void WaitForInputTopics(Properties props) throws ExecutionException, InterruptedException {
        AdminClient admin = AdminClient.create(props);
        Integer sleepTime = 5000;
        for (String topic : Arguments.InputTopicNames) {
            boolean topicExists = admin.listTopics().names().get().stream().anyMatch(topicName -> topicName.equalsIgnoreCase(topic));
            while (!topicExists) {
                System.out.println("Waiting for: " + topic);
                Thread.sleep(sleepTime);
                topicExists = admin.listTopics().names().get().stream().anyMatch(topicName -> topicName.equalsIgnoreCase(topic));
            }
        }
    }

    private static ArrayList<KTable<String, GenericRecord>> KeySharedTableGenerator(StreamsBuilder builder) {
        ArrayList<KTable<String, GenericRecord>> tables = new ArrayList<>();
        for (String topicName : Arguments.InputTopicNames) {
            KStream<String, GenericRecord> topic = builder.stream(topicName);
            KTable<String, GenericRecord> keySetTopic = topic.map((key, value) -> KeyValue.pair(SetKey(value, Arguments.CommonKey), value)).toTable();
            tables.add(keySetTopic);
        }
        return tables;
    }
    private static void SetupArguments(String[] args) {
        for (int i = 0; i < (args.length-Arguments.NonInputTopicArgs); i++) Arguments.InputTopicNames.add(args[i]);
        Arguments.OutputTopicName = args[args.length-Arguments.NonInputTopicArgs];
        Arguments.CommonKey = args[args.length-Arguments.NonInputTopicArgs+1];
        Arguments.Broker = args[args.length-Arguments.NonInputTopicArgs+2];
        Arguments.SchemaRegistryURL = args[args.length-Arguments.NonInputTopicArgs+3];
        Arguments.ApplicationID = args[args.length-Arguments.NonInputTopicArgs+4];
        Arguments.AutoOffsetResetConfig = args[args.length-Arguments.NonInputTopicArgs+5];
    }
}