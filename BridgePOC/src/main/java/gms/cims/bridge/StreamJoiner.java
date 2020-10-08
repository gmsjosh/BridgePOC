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
        StreamsBuilder builder = new StreamsBuilder();

        KStream<GenericRecord, ClaimStatus> topic1 = builder.stream("CIMSTEST.Financial.ClaimStatus");
        KTable<Integer, ClaimStatus> moddedTopic1 = topic1.map((key, value) -> KeyValue.pair(value.getCSClaimStatusID(), value)).toTable();

        KStream<GenericRecord, ClaimStatusClaimLink> topic2 = builder.stream("CIMSTEST.Financial.ClaimStatusClaimLink");
        KTable<Integer, ClaimStatusClaimLink> moddedTopic2 = topic2.map((key, value) -> KeyValue.pair(value.getCSClaimStatusID(), value)).toTable();


        return builder.build();
    }
}