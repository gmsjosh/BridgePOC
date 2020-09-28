package gms.cims.bridge;

import com.google.gson.JsonObject;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sql.rowset.serial.SerialException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StreamJoiner {

    public void Start() {

        Topology topology = buildTopology();
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "hello_world5");
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

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        final String outputTopic = "claim-topic";

        KStream<Object, Object> topic2 = builder.stream("CIMSTEST.Financial.ClaimStatusClaimLink");
        KStream<Object, Object> topic1 = builder.stream("CIMSTEST.Financial.ClaimStatus");

        KStream<Object, Object> output = topic1.outerJoin(
            topic2,
            (leftValue, rightValue) -> {
                if (leftValue==null) { return ("{left:{}" + ",right:" + rightValue + "}"); }
                else if (rightValue==null) { return ("{left:" + leftValue + ",right:{}}"); }
                else { return ("{left:" + leftValue + ",right:" + rightValue + "}"); }
            },
            JoinWindows.of(Duration.ofMinutes(5)));

        output.to(outputTopic);

        return builder.build();
    }
}