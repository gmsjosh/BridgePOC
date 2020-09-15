package gms.cims.bridge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.reflect.ReflectData;
import org.apache.camel.Exchange;
import org.apache.camel.ExpectedBodyTypeException;
import org.apache.camel.Processor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.Ref;
import java.util.*;
import java.io.*;

public class MessageProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroMessageConsumerProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            JSONObject kafkaBodyMessageObject = new JSONObject(exchange.getIn().getBody(String.class));
            exchange.getIn().setBody(GetRecordFromJsonObject(kafkaBodyMessageObject));
        } catch (ExpectedBodyTypeException e) {
            LOG.error(e.toString() + exchange.getIn().getBody(String.class));
        }
    }

    private Schema LoadSchemaFromFile(String schemaFile) throws IOException {
        return new Schema.Parser().parse(getClass().getClassLoader().getResourceAsStream(schemaFile));
    }

    private GenericData.Record GetRecordFromJsonObject(JSONObject kafkaBodyMessageObject) throws IOException {

        JSONObject data_before = kafkaBodyMessageObject.getJSONObject("before");
        JSONObject data_after = kafkaBodyMessageObject.getJSONObject("after");
        Schema schema = LoadSchemaFromFile("schema.avsc");
        //Schema schema = ReflectData.get().getSchema(kafkaBodyMessageObject.get("before"));
        //System.out.println(schema);
        GenericRecordBuilder record = new GenericRecordBuilder(schema);

        //record.set("UserID", data_before.getString("UserID"));
        //record.set("CO_ContractID", data_before.getInt("CO_ContractID"));
        // TO DO: SERIALIZE BYTES TYPE
        // record.set("RowVersion", data_before.get("RowVersion"));
        //record.set("LastUpdate_before", data_before.get("LastUpdate"));
        //record.set("IsIndividual_before", data_before.get("IsIndividual"));
        //record.set("LastUpdate_after", data_after.get("LastUpdate"));
        //record.set("IsIndividual_after", data_after.get("IsIndividual"));

        return record.build();
    }
}
