package org.gms;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class main {

    public static void main(String[] args) {

        Arguments.LeftTopicName = args[0];
        Arguments.RightTopicName = args[1];
        Arguments.OutputTopicName = args[2];
        Arguments.CommonKey = args[3];
        Arguments.Broker = args[4];
        Arguments.SchemaRegistry = args[5];
        Arguments.GroupId = args[6];
        Arguments.ApplicationID = args[7];
        Arguments.AutoOffsetResetConfig = args[8];

        Topology topology = buildTopology();
        Properties props = buildProperties();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static Properties buildProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, Arguments.ApplicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, Arguments.AutoOffsetResetConfig);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<GenericRecord, GenericRecord> topic1 = builder.stream(Arguments.LeftTopicName);
        KTable<Integer, GenericRecord> keySetTopic1 = topic1.map((key, value) -> KeyValue.pair(((Integer) value.get(Arguments.CommonKey)), value)).toTable();

        KStream<GenericRecord, GenericRecord> topic2 = builder.stream(Arguments.RightTopicName);
        KTable<Integer, GenericRecord> keySetTopic2 = topic2.map((key, value) -> KeyValue.pair(((Integer) value.get(Arguments.CommonKey)), value)).toTable();

        KTable<Integer, GenericRecord> joined = InnerJoinKTables(keySetTopic1, keySetTopic2);

        joined.toStream().to(Arguments.OutputTopicName);

        return builder.build();
    }

    private static KTable<Integer, GenericRecord> InnerJoinKTables(KTable<Integer, GenericRecord> leftTopic, KTable<Integer, GenericRecord> rightTopic) {
        KTable<Integer, GenericRecord> joined = leftTopic.join(rightTopic, (left,right) -> MergeMessages(left, right) );
        return joined;
    }
    private static GenericRecord MergeMessages(GenericRecord left, GenericRecord right) {
        JSONObject mergedValues = MergeValues(left, right);
        Schema mergedSchema;
        if (Arguments.Schema==null) mergedSchema = MergeSchema(left, right);
        else mergedSchema = Arguments.Schema;
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
}