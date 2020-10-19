package org.gms;

import org.apache.avro.Schema;

import java.util.ArrayList;

public class Arguments {
    public static String Broker;
    public static String SCHEMA_REGISTRY = "schema.registry.url";
    public static String SchemaRegistryURL;
    public static ArrayList<String> InputTopicNames = new ArrayList<>();
    public static String OutputTopicName;
    public static String CommonKey;
    public static String ApplicationID;
    public static String AutoOffsetResetConfig;
    public static Schema ValueSchema;
    public static Integer NonInputTopicArgs = 6;
}
