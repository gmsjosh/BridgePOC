package gms.cims.bridge;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.apache.avro.data.Json;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.ExpectedBodyTypeException;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class MessageProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroMessageConsumerProcessor.class);

    @Override
    public void process(Exchange exchange) {
        try {
            JSONObject kafkaBodyMessageObject = new JSONObject(exchange.getIn().getBody(String.class));
            exchange.getIn().setBody(GetRecordFromJsonObject(kafkaBodyMessageObject));
        } catch (ExpectedBodyTypeException e) {
            LOG.error(e.toString() + exchange.getIn().getBody(String.class));
        } catch (RestClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GenericData.Record GetRecordFromJsonObject(JSONObject kafkaBodyMessageObject) throws IOException, RestClientException {
        JSONObject data_before = kafkaBodyMessageObject.getJSONObject("before");
        JSONObject data_after = kafkaBodyMessageObject.getJSONObject("after");

        GenericRecordBuilder record = SetRecords(data_before, data_after, GetSchema(kafkaBodyMessageObject));

        return record.build();
    }

    private Schema GetSchema(JSONObject kafkaBodyMessageObject) throws IOException, RestClientException {
        JSONObject sourceJSON = kafkaBodyMessageObject.getJSONObject("source");
        SchemaRegistryClient registry = new CachedSchemaRegistryClient(Arguments.SchemaRegistry, Integer.MAX_VALUE);
        int registryID = GetIDFromNamespace(sourceJSON, registry);
        return registry.getById(registryID);
    }

    private int GetIDFromNamespace(JSONObject sourceJSON, SchemaRegistryClient registry) throws IOException, RestClientException {
        String namespace = sourceJSON.get("name")+"."+sourceJSON.get("schema")+"."+sourceJSON.get("table");
        String name = "Envelope";
        for (int i=1; i <= (registry.getAllSubjects().size()); i++) {
            if (namespace.equals(registry.getById(i).getNamespace()) && name.equals(registry.getById(i).getName())){
                return i;
            }
        }
        return 0;
    }

    private GenericRecordBuilder SetRecords(JSONObject beforeJSON, JSONObject afterJSON, Schema schema) {
        GenericRecordBuilder envelope = new GenericRecordBuilder(schema);

        String beforeString = schema.getField("before").schema().toString();
        beforeString = beforeString.substring(8,beforeString.length()-1);
        Schema.Parser parser = new Schema.Parser();
        Schema beforeSchema = parser.parse(beforeString);

        GenericRecordBuilder before = new GenericRecordBuilder(beforeSchema);

        String afterString = schema.getField("after").schema().toString();
        afterString = afterString.substring(8,afterString.length()-1);
        Schema afterSchema = parser.parse(beforeString);

        GenericRecordBuilder after = new GenericRecordBuilder(afterSchema);

        beforeJSON.keys().forEachRemaining(key -> {
            before.set(key, beforeJSON.get(key));
        });
        afterJSON.keys().forEachRemaining(key -> {
            after.set(key, afterJSON.get(key));
        });

        envelope.set("before", before);
        envelope.set("after", after);
        return envelope;
    }
}
