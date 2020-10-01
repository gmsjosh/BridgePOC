package gms.cims.bridge;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

public class StreamJoiner {

    public void Start() {

        Topology topology = buildTopology();
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "claim-creator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);

        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private Topology buildTopology() {
        //topic2.print(Printed.toSysOut());
        //[ClaimStatus: {"CS_ClaimStatusID": 12288}, {"CS_ClaimStatusID": 12288, "CS_Description": "Claim contains invalid drugs                                                                        ", "__deleted": "false"}
        //ClaimStatusClaimLink: {"CL_ClaimID": 12229719, "CS_ClaimStatusID": 26711}, {"CL_ClaimID": 12229719, "CS_ClaimStatusID": 26711, "__deleted": "false"}
        StreamsBuilder builder = new StreamsBuilder();
        KStream<GenericRecord, GenericRecord> topic1 = builder.stream("CIMSTEST.Financial.ClaimStatus");
        KStream<GenericRecord, GenericRecord> topic2 = builder.stream("CIMSTEST.Financial.ClaimStatusClaimLink");

        KTable<Integer, GenericRecord> moddedTopic1 = topic1.map((key, value) -> KeyValue.pair(((Integer) (key.get("CS_ClaimStatusID"))), value)).toTable();
        KTable<Integer, GenericRecord> moddedTopic2 = topic2.map((key, value) -> KeyValue.pair(((Integer) (key.get("CS_ClaimStatusID"))), value)).toTable();

        KTable<Integer, Claim> joined = moddedTopic2.join(moddedTopic1,
                (left,right) -> {
                    JSONObject leftJSON = new JSONObject(left.toString());
                    JSONObject rightJSON = new JSONObject(right.toString());
                    ObjectMapper objectMapper = new ObjectMapper();
                    Claim claim = new Claim();

                    leftJSON.keys().forEachRemaining(k -> {
                        if (!rightJSON.has(k)) {
                            rightJSON.put(k, leftJSON.get(k));
                        }
                    });

                    try {
                        claim = objectMapper.readValue(rightJSON.toString(), Claim.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return claim;
                }
        );

        joined.toStream().to(Arguments.outputTopic);


        /*ArrayList<KStream> streams = new ArrayList<>();
        ArrayList<KStream> splitStreams = new ArrayList<>();
        Arguments.Topics.forEach(topic -> streams.add(builder.stream(topic)));

        streams.forEach(stream -> {
            splitStreams.add(stream.flatMap(
                    (key, value) -> {
                        List<KeyValue<Object, Object>> result = new LinkedList<>();
                        new JSONObject(key.toString()).keys().forEachRemaining(k -> {
                            result.add(KeyValue.pair(((Object) new JSONObject("{" + k + ": " + new JSONObject(key.toString()).get(k) + "}").toString()), value));
                        });
                        return result;
                    })
            );
        });

        KStream joined = splitStreams.get(0).join(splitStreams.get(1),
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue,
                JoinWindows.of(Duration.ofMinutes(5))
        );*/

        //joined.to("output-topic");

        return builder.build();
    }
}