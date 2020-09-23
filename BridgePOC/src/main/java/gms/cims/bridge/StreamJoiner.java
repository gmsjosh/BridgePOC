package gms.cims.bridge;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StreamJoiner {

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String ESIClaimImportDetailTopic = "CIMSTEST.Financial.ClaimStatus";
        final String ESIClaimFileRecordTopic = "CIMSTEST.Financial.ClaimStatusClaimLink";
        final String outputTopic = "outer-join-output-topic";

        KStream<Object, Object> topic1 = builder.stream(ESIClaimImportDetailTopic);
        KStream<Object, Object> topic2 = builder.stream(ESIClaimFileRecordTopic);

        /*KStream<Object, Object> outerJoined = topic1.outerJoin(
                topic2,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue,
                JoinWindows.of(Duration.ofMinutes(5)));*/

        topic1.to(outputTopic);
        //topic2.to(outputTopic);

        return builder.build();
    }

    public void Start() {

        Topology topology = buildTopology();
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "hello_world3");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Arguments.Broker);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, KafkaAvroSerde.class);

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