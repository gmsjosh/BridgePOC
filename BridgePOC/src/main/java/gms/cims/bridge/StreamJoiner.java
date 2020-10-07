package gms.cims.bridge;

import CIMSTEST.Financial.ClaimCostPlus.ClaimCostPlus;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import javax.annotation.processing.Processor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class StreamJoiner {

    public void Start() {

        Topology topology = buildTopology();
        Properties props = buildProperties();

        StoreBuilder processingStore = Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("processingStore"),
                Serdes.String(),
                Serdes.String()
        );

        StoreBuilder outputStore = Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("outputStore"),
                Serdes.String(),
                Serdes.String()
        );

        topology.addSource(
                "Source",
                "CIMSTEST.Financial.ClaimStatus",
                "CIMSTEST.Financial.ClaimStatusClaimLink",
                "CIMSTEST.Financial.ClaimCostPlus",
                "CIMSTEST.Customer.ClaimBlackList")
                .addProcessor("Process", () -> new MergedMessageProcessor(), "Source")
                .addStateStore(processingStore, "Process")
                .addStateStore(outputStore, "Process")
                .addSink("Sink", "sink-topic", "Process");

        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.cleanUp();
        streams.start();
        System.out.println(streams.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private Properties buildProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "claim-creator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);
        return props;
    }

    private Topology buildTopology() {
        //topic2.print(Printed.toSysOut());
        //[ClaimStatus: {"CS_ClaimStatusID": 12288}, {"CS_ClaimStatusID": 12288, "CS_Description": "Claim contains invalid drugs                                                                        ", "__deleted": "false"}
        //ClaimStatusClaimLink: {"CL_ClaimID": 12229719, "CS_ClaimStatusID": 26711}, {"CL_ClaimID": 12229719, "CS_ClaimStatusID": 26711, "__deleted": "false"}

        StreamsBuilder builder = new StreamsBuilder();

        //keySetStreams.get(0).print(Printed.toSysOut());
        //keySetStreams.get(1).print(Printed.toSysOut());

        //KStream<GenericRecord, ClaimStatus> topic1 = builder.stream("CIMSTEST.Financial.ClaimStatus");
        //KTable<Integer, ClaimStatus> moddedTopic1 = topic1.map((key, value) -> KeyValue.pair(value.getCSClaimStatusID(), value)).toTable();

        //KStream<GenericRecord, ClaimStatusClaimLink> topic2 = builder.stream("CIMSTEST.Financial.ClaimStatusClaimLink");
        //KTable<Integer, ClaimStatusClaimLink> moddedTopic2 = topic2.map((key, value) -> KeyValue.pair(value.getCSClaimStatusID(), value)).toTable();

        /*KStream<GenericRecord, ClaimCostPlus> topic3 = builder.stream("CIMSTEST.Financial.ClaimCostPlus");
        KTable<Integer, ClaimCostPlus> moddedtopic3 = topic3.map((key, value) -> KeyValue.pair(value.getCLClaimID(), value)).toTable();*/



        //streamJoiner(moddedTopic1, moddedTopic2).toStream().to(Arguments.outputTopic);

        return builder.build();
    }

    private KTable streamJoiner (KTable leftTopic, KTable rightTopic) {

        KTable<Integer, Claim> joined = leftTopic.join(rightTopic,
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
        return joined;
    }
}