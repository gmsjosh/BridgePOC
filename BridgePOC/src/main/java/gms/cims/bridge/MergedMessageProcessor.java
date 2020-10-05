package gms.cims.bridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.json.JSONObject;

public class MergedMessageProcessor implements Processor<GenericRecord, GenericRecord> {

    private ProcessorContext context;
    private KeyValueStore<String, String> kvStore;


    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
        kvStore = (KeyValueStore) this.context.getStateStore("inmemory-store");
    }

    @Override
    public void process(GenericRecord gk, GenericRecord gv) {

        JSONObject JSONkey = new JSONObject(gk.toString());
        JSONObject JSONvalue = new JSONObject(gv.toString());

        if (kvStore.all()!=null) {
            //loop through each kvp in the genericRecord key
            JSONkey.keys().forEachRemaining(jsonk -> {
                //Go through each record in the kvStore
                kvStore.all().forEachRemaining(obj -> {
                    //If the value for the object in the kvStore contains a key that matches with one of the generic record keys

                    Object kvStoreValue = new JSONObject(obj.value).get(jsonk);
                    Object inputValue = JSONkey.get(jsonk);

                    if (kvStoreValue==inputValue) {
                        new JSONObject(obj.value).keys().forEachRemaining(k -> {
                            if (!JSONvalue.has(k)) {
                                JSONvalue.put(k, new JSONObject(obj.value).get(k));
                            }
                        });
                        kvStore.delete(obj.key);
                        kvStore.put(JSONkey.toString(), JSONvalue.toString());
                    }
                });
            });
        }
        else {kvStore.put(JSONkey.toString(), JSONvalue.toString());}
    }

    @Override
    public void close() {

    }
}
