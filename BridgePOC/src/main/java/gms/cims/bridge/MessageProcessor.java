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
        System.out.println(registry.getById(registryID));
        return registry.getById(registryID);
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

    private GenericRecordBuilder SetRecords(JSONObject beforeJSON, JSONObject afterJSON, Schema schema) {
        GenericRecordBuilder envelope = new GenericRecordBuilder(schema);
        GenericRecordBuilder before = new GenericRecordBuilder(schema.getField("before").schema());
        GenericRecordBuilder after = new GenericRecordBuilder(schema.getField("after").schema());
        GenericRecordBuilder value = new GenericRecordBuilder(schema.getField("value").schema());
        
        GenericRecordBuilder record = new GenericRecordBuilder(schema);
        /*before.keys().forEachRemaining(key -> {
            record.set("before."+key, before.get(key));
        });
        after.keys().forEachRemaining(key -> {
            record.set("after."+key, after.get(key));
        });*/

        return record;
    }
}
