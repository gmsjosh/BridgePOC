import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

public class main {
    public static void main(String[] args) {
        /*FORMAT OF ARGUMENTS:
        1. FIRST STREAM
        2. SECOND STREAM
        3. OUTPUT TOPIC
        4. COMMON KEY*/

        String leftTopicName = "CIMSTEST.Financial.ClaimStatusClaimLink";// args[0];
        String rightTopicName = "CIMSTEST.Financial.ClaimStatus";// args[1];
        String outputTopicName = "ClaimStatusJoined";// args[2];
        String commonKey = "CL_ClaimStatusID";// args[3];

        Topology topology = buildTopology(leftTopicName, rightTopicName, outputTopicName, commonKey);
        Properties props = buildProperties();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static Properties buildProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "claim-creator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        return props;
    }

    public static Topology buildTopology(String leftTopicName, String rightTopicName, String outputTopicName, String commonKey) {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<GenericRecord, GenericRecord> topic1 = builder.stream(leftTopicName);
        KTable<Integer, GenericRecord> keySetTopic1 = topic1.map((key, value) -> KeyValue.pair(((Integer) value.get(commonKey)), value)).toTable();

        KStream<GenericRecord, GenericRecord> topic2 = builder.stream(leftTopicName);
        KTable<Integer, GenericRecord> keySetTopic2 = topic2.map((key, value) -> KeyValue.pair(((Integer) value.get(commonKey)), value)).toTable();

        KStream<Integer, GenericRecord> joinedTopics = keySetTopic1.join(keySetTopic2)


        return builder.build();
    }
}
