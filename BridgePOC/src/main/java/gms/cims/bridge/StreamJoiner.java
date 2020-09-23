package gms.cims.bridge;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StreamJoiner {

    public Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();

        final String ESIClaimImportDetailTopic = "CIMSTEST.Financial.ClaimStatus";
        final String ESIClaimFileRecordTopic = "CIMSTEST.Financial.ClaimStatusClaimLink";
        final String outputTopic = "CIMSTEST.Test";

        KStream<String, String> ESIClaimImportDetail = builder.stream(ESIClaimImportDetailTopic);
        KStream<String, String> ESIClaimFileRecord = builder.stream(ESIClaimFileRecordTopic);
        KStream<String, String> output = ESIClaimImportDetail.merge(ESIClaimFileRecord);

        output.to(outputTopic);
        return builder.build();
    }

    public void Start() throws Exception {

        Topology topology = buildTopology();
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, Arguments.GroupId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put("schema.registry.url", Arguments.SchemaRegistry);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());


        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}