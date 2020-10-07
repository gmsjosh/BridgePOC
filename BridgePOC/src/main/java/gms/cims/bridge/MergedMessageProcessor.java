package gms.cims.bridge;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.json.JSONObject;

import java.time.Duration;
import java.util.Iterator;

public class MergedMessageProcessor implements Processor<GenericRecord, GenericRecord> {

    private ProcessorContext context;
    private KeyValueStore<String, String> outputStore;
    private KeyValueStore<String, String> processingStore;

    @Override
    public void init(ProcessorContext processorContext) {
        // keep the processor context locally because we need it in punctuate() and commit()
        this.context = processorContext;
        // retrieve the key-value store named "inmemory-store"
        this.processingStore = (KeyValueStore) this.context.getStateStore("processingStore");
        this.outputStore = (KeyValueStore) this.context.getStateStore("outputStore");

        // schedule a punctuate() method every second based on event-time
        /*this.context.schedule(Duration.ofSeconds(1), PunctuationType.WALL_CLOCK_TIME, (timestamp) -> {
            KeyValueIterator<String, String> iter = this.outputStore.all();
            while (iter.hasNext()) {
                KeyValue<String, String> entry = iter.next();
                context.forward(entry.key, entry.value.toString());
            }
            iter.close();

            // commit the current processing progress
            context.commit();
        });*/
    }

    @Override
    public void process(GenericRecord gk, GenericRecord gv) {

        //If empty, add the input key and value to the kvStore
        if (!this.processingStore.all().hasNext()) {
            this.processingStore.put(gk.toString(), gv.toString());
        }
        //If kvStore has items, check to see if the input value has a matching value in the kvStore
        else {
            boolean broken = false;
            //Set the input value to JSONObject for easy looping
            JSONObject inputValue = new JSONObject(gv.toString());
            //Loop through each kvp in the kvStore
            KeyValueIterator<String, String> kvStoreIterator = this.processingStore.all();
            while (kvStoreIterator.hasNext()) {
                //Grab the kvStore Value and convert to JSONObject
                KeyValue<String, String> storeKVP = kvStoreIterator.next();
                JSONObject storeValue = new JSONObject(storeKVP.value);
                // Check if the values are the same
                if (AreTheSame(inputValue, storeValue)) {
                    broken = true;
                    break;
                }
                //loop though each kvp in the input value
                Iterator<String> inputValueIterator = inputValue.keys();
                while (inputValueIterator.hasNext()) {
                    //Set the key variable
                    String inputValueKey = inputValueIterator.next();
                    //When a value from the storeValue matches with the value of the input based on the same key, go ahead and combine them
                    if (HasSameValue(inputValue, storeValue, inputValueKey)){
                        //get the combined json
                        inputValue = CombineJSONObjects(inputValue, storeValue);
                        //delete the old record
                        this.processingStore.delete(storeKVP.key);
                        //add new record
                        this.processingStore.put(gk.toString(), inputValue.toString());
                        //Output to Sink Topic
                        context.forward(gk.toString(), inputValue.toString());
                        //set matched to true and break out of loops.
                        broken = true;
                        break;
                    }
                }
                if (broken) {break;}
            }
            if (!broken) {
                this.processingStore.put(gk.toString(), gv.toString());
            }
        }

    }

    @Override
    public void close() {

    }

    private boolean HasSameValue(JSONObject left, JSONObject right, String key) {
        //If both the left and right have the same key
        if (left.has(key) && right.has(key)) {
            //Check if the values for the key are the same
            boolean firstCondition = (left.get(key).toString().equals(right.get(key).toString()));
            return firstCondition;
        }
        else return false;
    }

    private boolean AreTheSame(JSONObject left, JSONObject right) {
        return (left.toString().equals(right.toString()));
    }

    private JSONObject CombineJSONObjects(JSONObject left, JSONObject right) {
        left.keys().forEachRemaining(k -> {
            //Add missing values
            if (!right.has(k)) {
                right.put(k, left.get(k));
            }
            //Update old values
            else {
                right.remove(k);
                right.put(k, left.get(k));
            }
        });
        return right;
    }

}
