package gms.cims.bridge;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.ExpectedBodyTypeException;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;

public class MessageProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroMessageConsumerProcessor.class);

    @Override
    public void process(Exchange exchange) {
        try {
            JSONObject kafkaBodyMessageObject = new JSONObject(exchange.getIn().getBody(String.class));
            exchange.getIn().setBody(GetRecordFromJsonObject(kafkaBodyMessageObject));
        } catch (ExpectedBodyTypeException e) {
            LOG.error(e.toString() + exchange.getIn().getBody(String.class));
        }
    }

    private GenericData.Record GetRecordFromJsonObject(JSONObject kafkaBodyMessageObject) {
        JSONObject data_before = kafkaBodyMessageObject.getJSONObject("before");
        JSONObject data_after = kafkaBodyMessageObject.getJSONObject("after");
        GenericRecordBuilder record = SetRecords(data_before, data_after);
        return record.build();
    }

    private Schema ParseSchemaString(String schemaString) {
        return new Schema.Parser().parse(schemaString);
    }

    private GenericRecordBuilder SetRecords(JSONObject before, JSONObject after) {

        Schema schema = ParseSchemaString(SchemaBuilder(before));
        System.out.println(schema);
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
