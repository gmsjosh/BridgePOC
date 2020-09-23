package gms.cims.bridge;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Properties;

public class TableJoiner {

    public void startStreamStreamInnerJoin() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-stream-inner-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> leftSource = builder.stream("CIMSTEST.Reference.ESIClaimImportDetailType");
        KStream<String, String> rightSource = builder.stream("CIMSTEST.Reference.ESICoverageCodeAccumLink");

        KStream<String, String> joined = leftSource.join(rightSource,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */
                JoinWindows.of(Duration.ofMinutes(5)),
                Joined.with(
                        Serdes.String(), /* key */
                        Serdes.String(),   /* left value */
                        Serdes.String())  /* right value */
        );

        joined.to("my-kafka-stream-stream-inner-join-out");

        final Topology topology = builder.build();
        KafkaStreams streamsInnerJoin = new KafkaStreams(topology, props);
        streamsInnerJoin.start();

    }
}
