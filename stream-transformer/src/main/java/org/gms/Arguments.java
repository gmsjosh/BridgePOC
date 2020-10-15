package org.gms;

import org.apache.avro.Schema;

public class Arguments {
    public static String Broker;
    public static String SCHEMA_REGISTRY = "schema.registry.url";
    public static String SchemaRegistryURL;
    public static String LeftTopicName;
    public static String RightTopicName;
    public static String OutputTopicName;
    public static String CommonKey;
    public static String ApplicationID;
    public static String AutoOffsetResetConfig;
    public static Schema ValueSchema;
}
