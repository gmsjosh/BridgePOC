package gms.cims.bridge;

import com.google.gson.JsonObject;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

public class MergedMessageProcessor implements Processor<GenericRecord, GenericRecord> {

    private ProcessorContext context;
    private KeyValueStore processingStore;

    @Override
    public void init(ProcessorContext processorContext) {
        // keep the processor context locally because we need it in punctuate() and commit()
        this.context = processorContext;
        // retrieve the key-value store named "processingStore"
        this.processingStore = (KeyValueStore) this.context.getStateStore("processingStore");
    }

    @Override
    public void process(GenericRecord gk, GenericRecord gv) {

        //If empty, add the input key and value to the kvStore
        if (!this.processingStore.all().hasNext()) {
            this.processingStore.put(gk.toString(), gv.toString());
        }
        //If kvStore has items, check to see if the input value has a matching value in the kvStore
        else {
            //Set the input value to JSONObject for easy looping
            String inputKey = gk.toString();
            String inputValue = gv.toString();
            //Get the value from the kvStore
            KeyValue<String, String> storeKVP = FindInKvStore(this.processingStore, inputKey);
            if (storeKVP!=null) {
                // combine the input and store keys and values.
                String combinedKey = CombineJSONStrings(inputKey, storeKVP.key);
                String combinedValue = CombineJSONStrings(inputValue, storeKVP.value);
                //delete the old record
                this.processingStore.delete(storeKVP.key);
                //add new record
                this.processingStore.put(combinedKey, combinedValue);
                //Output to Sink Topic
                context.forward(combinedKey, combinedValue);
            }
            else {
                this.processingStore.put(gk.toString(), gv.toString());
                String temp = this.processingStore.all().toString();
                System.out.println();
            }
        }

    }

    @Override
    public void close() {

    }

    private KeyValue<String, String> FindInKvStore(KeyValueStore<String, String> kvStore, String inputKey) {
        // create an iterator of all the values from the kvStore
        KeyValueIterator<String, String> kvStoreIterator = kvStore.all();
        while (kvStoreIterator.hasNext()) {
            // set variables
            KeyValue<String, String> storeKVP = kvStoreIterator.next();
            String storeKey = storeKVP.key;
            // Check that the keys are not the same and that the keys share some of the same values
            if (AreNotTheSameAndShareValues(storeKey, inputKey)) return storeKVP;
        }
        return null;
    }

    private boolean AreNotTheSameAndShareValues(String left, String right) {
        // if the strings are equal return false
        if (left.equals(right)) return false;
        right = right.replace(" ", "");
        // split the strings by commas, and remove the braces
        String[] kvps = left.replaceAll("[{}]", "").split(",");
        // loop through each kvp and if the right string matches one of the kvps return true
        for (String kvp : kvps) {
            if (right.contains(kvp)) return true;
        }
        return false;
    }

    private String CombineJSONStrings(String left, String right) {
        JSONObject leftJSON = new JSONObject(left);
        JSONObject rightJSON = new JSONObject(right);
        leftJSON.keys().forEachRemaining(k -> {
            // remove old values
            if (rightJSON.has(k)) {
                rightJSON.remove(k);
            }
            // add new values
            rightJSON.put(k, leftJSON.get(k));
        });
        return rightJSON.toString();
    }
}
