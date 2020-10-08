import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.cli.Arg;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

public class main {
    public static void main(String[] args) {
        /*FORMAT OF ARGUMENTS:
        1. FIRST STREAM
        2. SECOND STREAM
        3. OUTPUT TOPIC
        4. COMMON KEY*/

        Arguments.leftTopicName = "CIMSTEST.Financial.ClaimStatusClaimLink";// args[0];
        Arguments.rightTopicName = "CIMSTEST.Financial.ClaimStatus";// args[1];
        Arguments.outputTopicName = "ClaimStatusJoined";// args[2];
        Arguments.commonKey = "CS_ClaimStatusID";// args[3];
        Arguments.Broker ="localhost:29092";
        Arguments.SchemaRegistry = "http://localhost:8081";
        Arguments.GroupId = "cimstest";
        Arguments.ApplicationID = "stream-merger";
        Arguments.AutoOffsetResetConfig = "earliest";

        Topology topology = buildTopology();
        Properties props = buildProperties();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static Properties buildProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, Arguments.ApplicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, Arguments.AutoOffsetResetConfig);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        return props;
    }

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<GenericRecord, GenericRecord> topic1 = builder.stream(Arguments.leftTopicName);
        KTable<Integer, GenericRecord> keySetTopic1 = topic1.map((key, value) -> KeyValue.pair(((Integer) value.get(Arguments.commonKey)), value)).toTable();

        KStream<GenericRecord, GenericRecord> topic2 = builder.stream(Arguments.rightTopicName);
        KTable<Integer, GenericRecord> keySetTopic2 = topic2.map((key, value) -> KeyValue.pair(((Integer) value.get(Arguments.commonKey)), value)).toTable();

        KTable<Integer, Output> joined = keySetTopic1.join(keySetTopic2,
                (left,right) -> {
                    JSONObject leftJSON = new JSONObject(left.toString());
                    JSONObject rightJSON = new JSONObject(right.toString());
                    ObjectMapper objectMapper = new ObjectMapper();
                    Output output = new Output();

                    leftJSON.keys().forEachRemaining(k -> {
                        if (!rightJSON.has(k)) {
                            rightJSON.put(k, leftJSON.get(k));
                        }
                    });

                    try {
                        output = objectMapper.readValue(rightJSON.toString(), Output.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return output;
                }
        );

        joined.toStream().to(Arguments.outputTopicName);

        return builder.build();
    }
}
