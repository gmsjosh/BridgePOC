package gms.cims.bridge;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.ExpectedBodyTypeException;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

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

        JSONObject sourceJSON = kafkaBodyMessageObject.getJSONObject("source");
        SchemaRegistryClient registry = new CachedSchemaRegistryClient(Arguments.SchemaRegistry, Integer.MAX_VALUE);
        int registryID = GetIDFromNamespace(sourceJSON, registry);
        Schema schema = registry.getById(registryID);

        GenericRecordBuilder record = SetRecords(data_before, data_after);

        return record.build();
    }

    private int GetIDFromNamespace(JSONObject sourceJSON, SchemaRegistryClient registry) throws IOException, RestClientException {
        String namespace = sourceJSON.get("name")+"."+sourceJSON.get("schema")+"."+sourceJSON.get("table");
        for (int i=1; i <= (registry.getAllSubjects().size()); i++) {
            if (namespace.equals(registry.getById(i).getNamespace())){
                return i+1;
            }
        }
        return 0;
    }

    private Schema ParseSchemaString(String schemaString) {
        return new Schema.Parser().parse(schemaString);
    }

    private GenericRecordBuilder SetRecords(JSONObject before, JSONObject after) {

        GenericRecordBuilder record = new GenericRecordBuilder(schema);

        before.keys().forEachRemaining(key -> {
            record.set(key+"_before", before.get(key).toString());
        });
        after.keys().forEachRemaining(key -> {
            record.set(key+"_after", after.get(key).toString());
        });

        return record;
    }

    private String SchemaBuilder(JSONObject kvp) {
        final String[] schema = {"{"
                + "\"type\" : \"record\","
                + "\"name\" : \"schema\","
                + "\"namespace\" : \"Test\","
                + "\"fields\" : [ {"};
        kvp.keys().forEachRemaining(key -> {
            schema[0] += MessageFormat.format("\"name\" : \"{0}\",", (key+"_before"))
                    +"\"type\" : [\"null\", \"string\"],"
                    +"\"default\" : null"
                    +"}, {"
                    +MessageFormat.format("\"name\" : \"{0}\",", (key+"_after"))
                    +"\"type\" : [\"null\", \"string\"],"
                    +"\"default\" : null"
                    +"}, {";
        });

        schema[0] = schema[0].substring(0, schema[0].length() - 4);
        schema[0] += "}]"+"}";
        return schema[0];
    }
}
