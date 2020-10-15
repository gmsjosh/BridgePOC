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

        Arguments.LeftTopicName = args[0];
        Arguments.RightTopicName = args[1];
        Arguments.OutputTopicName = args[2];
        Arguments.CommonKey = args[3];
        Arguments.Broker = args[4];
        Arguments.SchemaRegistryURL = args[5];
        Arguments.ApplicationID = args[6];
        Arguments.AutoOffsetResetConfig = args[7];

        /*Arguments.LeftTopicName = "CIMSTEST.Financial.ClaimStatusClaimLink";
        Arguments.RightTopicName = "CIMSTEST.Financial.ClaimStatus";
        Arguments.OutputTopicName = "ClaimStatusClaimLink_ClaimStatus";
        Arguments.CommonKey = "CS_ClaimStatusID";
        Arguments.Broker = "localhost:9092";
        Arguments.SchemaRegistryURL = "http://localhost:8081";
        Arguments.ApplicationID = "first-join";
        Arguments.AutoOffsetResetConfig = "earliest";*/

        Topology topology = buildTopology();
        Properties props = buildProperties();
        WaitForInputTopics(props);

        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.cleanUp();
        streams.start();
        //System.out.println(streams.toString());
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

        KStream<String, GenericRecord> topic1 = builder.stream(Arguments.LeftTopicName);
        KTable<String, GenericRecord> keySetTopic1 = topic1.map((key, value) -> KeyValue.pair(SetKey(value, Arguments.CommonKey), value)).toTable();

        KStream<String, GenericRecord> topic2 = builder.stream(Arguments.RightTopicName);
        KTable<String, GenericRecord> keySetTopic2 = topic2.map((key, value) -> KeyValue.pair(SetKey(value, Arguments.CommonKey), value)).toTable();

        KTable<String, GenericRecord> joined = InnerJoinKTables(keySetTopic1, keySetTopic2);

        joined.toStream().to(Arguments.OutputTopicName);

        return builder.build();
    }

    private static KTable<String, GenericRecord> InnerJoinKTables(KTable<String, GenericRecord> leftTopic, KTable<String, GenericRecord> rightTopic) {
        return leftTopic.join(rightTopic, (left,right) -> MergeMessages(left, right) );
    }

    private static GenericRecord MergeMessages(GenericRecord left, GenericRecord right) {
        JSONObject mergedValues = MergeValues(left, right);
        Schema mergedSchema;
        if (Arguments.ValueSchema==null) mergedSchema = MergeSchema(left, right);
        else mergedSchema = Arguments.ValueSchema;
        GenericRecord mergedGenericRecord = CreateGenericRecord(mergedValues, mergedSchema);
        return mergedGenericRecord;
    }

    private static Schema MergeSchema(GenericRecord left, GenericRecord right) {
        Schema rightSchema = right.getSchema();
        List<Field> rightFields = right.getSchema().getFields();
        List<Field> leftFields = left.getSchema().getFields();
        ArrayList<Field> mergedFields = new ArrayList();
        rightFields.forEach(field -> mergedFields.add(new Field(field.name(), field.schema(), field.doc(), field.defaultVal())));

        for (Field field : leftFields) {
            if (rightSchema.getField(field.name())==null) {
                Field newField = new Field(field.name(), field.schema(), field.doc(), field.defaultVal());
                mergedFields.add(newField);
            }
        }
        Schema mergedSchema = Schema.createRecord("Value", "info", "org.gms", false, mergedFields);
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

    private static GenericRecord CreateGenericRecord(JSONObject values, Schema schema) {
        final GenericData.Record record = new GenericData.Record(schema);
        values.keys().forEachRemaining(k -> {
            record.put(k, values.get(k));
        });
        return record;
    }

    private static String SetKey(GenericRecord value, String commonKey) {
        return value.get(commonKey).toString();
    }

    private static void WaitForInputTopics(Properties props) throws ExecutionException, InterruptedException {
        AdminClient admin = AdminClient.create(props);
        Integer sleepTime = 10000;
        while (!admin.listTopics().names().get().stream().anyMatch(topicName -> topicName.equalsIgnoreCase(Arguments.LeftTopicName))) {System.out.println("WAITING FOR INPUT TOPIC: " + Arguments.LeftTopicName); Thread.sleep(sleepTime);}
        while (!admin.listTopics().names().get().stream().anyMatch(topicName -> topicName.equalsIgnoreCase(Arguments.RightTopicName))) {System.out.println("WAITING FOR INPUT TOPIC: " + Arguments.RightTopicName); Thread.sleep(sleepTime);}
    }
}