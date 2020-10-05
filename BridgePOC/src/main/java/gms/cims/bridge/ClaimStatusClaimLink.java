/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package gms.cims.bridge;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class ClaimStatusClaimLink extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 1946225342893472440L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ClaimStatusClaimLink\",\"namespace\":\"gms.cims.bridge\",\"fields\":[{\"name\":\"CL_ClaimID\",\"type\":\"int\"},{\"name\":\"CS_ClaimStatusID\",\"type\":\"int\"}],\"connect.name\":\"CIMSTEST.Financial.ClaimStatusClaimLink.Value\"}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ClaimStatusClaimLink> ENCODER =
      new BinaryMessageEncoder<ClaimStatusClaimLink>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ClaimStatusClaimLink> DECODER =
      new BinaryMessageDecoder<ClaimStatusClaimLink>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<ClaimStatusClaimLink> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<ClaimStatusClaimLink> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<ClaimStatusClaimLink>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this ClaimStatusClaimLink to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a ClaimStatusClaimLink from a ByteBuffer. */
  public static ClaimStatusClaimLink fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public int CL_ClaimID;
  @Deprecated public int CS_ClaimStatusID;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ClaimStatusClaimLink() {}

  /**
   * All-args constructor.
   * @param CL_ClaimID The new value for CL_ClaimID
   * @param CS_ClaimStatusID The new value for CS_ClaimStatusID
   */
  public ClaimStatusClaimLink(java.lang.Integer CL_ClaimID, java.lang.Integer CS_ClaimStatusID) {
    this.CL_ClaimID = CL_ClaimID;
    this.CS_ClaimStatusID = CS_ClaimStatusID;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return CL_ClaimID;
    case 1: return CS_ClaimStatusID;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: CL_ClaimID = (java.lang.Integer)value$; break;
    case 1: CS_ClaimStatusID = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'CL_ClaimID' field.
   * @return The value of the 'CL_ClaimID' field.
   */
  public java.lang.Integer getCLClaimID() {
    return CL_ClaimID;
  }

  /**
   * Sets the value of the 'CL_ClaimID' field.
   * @param value the value to set.
   */
  public void setCLClaimID(java.lang.Integer value) {
    this.CL_ClaimID = value;
  }

  /**
   * Gets the value of the 'CS_ClaimStatusID' field.
   * @return The value of the 'CS_ClaimStatusID' field.
   */
  public java.lang.Integer getCSClaimStatusID() {
    return CS_ClaimStatusID;
  }

  /**
   * Sets the value of the 'CS_ClaimStatusID' field.
   * @param value the value to set.
   */
  public void setCSClaimStatusID(java.lang.Integer value) {
    this.CS_ClaimStatusID = value;
  }

  /**
   * Creates a new ClaimStatusClaimLink RecordBuilder.
   * @return A new ClaimStatusClaimLink RecordBuilder
   */
  public static gms.cims.bridge.ClaimStatusClaimLink.Builder newBuilder() {
    return new gms.cims.bridge.ClaimStatusClaimLink.Builder();
  }

  /**
   * Creates a new ClaimStatusClaimLink RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ClaimStatusClaimLink RecordBuilder
   */
  public static gms.cims.bridge.ClaimStatusClaimLink.Builder newBuilder(gms.cims.bridge.ClaimStatusClaimLink.Builder other) {
    return new gms.cims.bridge.ClaimStatusClaimLink.Builder(other);
  }

  /**
   * Creates a new ClaimStatusClaimLink RecordBuilder by copying an existing ClaimStatusClaimLink instance.
   * @param other The existing instance to copy.
   * @return A new ClaimStatusClaimLink RecordBuilder
   */
  public static gms.cims.bridge.ClaimStatusClaimLink.Builder newBuilder(gms.cims.bridge.ClaimStatusClaimLink other) {
    return new gms.cims.bridge.ClaimStatusClaimLink.Builder(other);
  }

  /**
   * RecordBuilder for ClaimStatusClaimLink instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ClaimStatusClaimLink>
    implements org.apache.avro.data.RecordBuilder<ClaimStatusClaimLink> {

    private int CL_ClaimID;
    private int CS_ClaimStatusID;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(gms.cims.bridge.ClaimStatusClaimLink.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.CL_ClaimID)) {
        this.CL_ClaimID = data().deepCopy(fields()[0].schema(), other.CL_ClaimID);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.CS_ClaimStatusID)) {
        this.CS_ClaimStatusID = data().deepCopy(fields()[1].schema(), other.CS_ClaimStatusID);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing ClaimStatusClaimLink instance
     * @param other The existing instance to copy.
     */
    private Builder(gms.cims.bridge.ClaimStatusClaimLink other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.CL_ClaimID)) {
        this.CL_ClaimID = data().deepCopy(fields()[0].schema(), other.CL_ClaimID);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.CS_ClaimStatusID)) {
        this.CS_ClaimStatusID = data().deepCopy(fields()[1].schema(), other.CS_ClaimStatusID);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'CL_ClaimID' field.
      * @return The value.
      */
    public java.lang.Integer getCLClaimID() {
      return CL_ClaimID;
    }

    /**
      * Sets the value of the 'CL_ClaimID' field.
      * @param value The value of 'CL_ClaimID'.
      * @return This builder.
      */
    public gms.cims.bridge.ClaimStatusClaimLink.Builder setCLClaimID(int value) {
      validate(fields()[0], value);
      this.CL_ClaimID = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'CL_ClaimID' field has been set.
      * @return True if the 'CL_ClaimID' field has been set, false otherwise.
      */
    public boolean hasCLClaimID() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'CL_ClaimID' field.
      * @return This builder.
      */
    public gms.cims.bridge.ClaimStatusClaimLink.Builder clearCLClaimID() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'CS_ClaimStatusID' field.
      * @return The value.
      */
    public java.lang.Integer getCSClaimStatusID() {
      return CS_ClaimStatusID;
    }

    /**
      * Sets the value of the 'CS_ClaimStatusID' field.
      * @param value The value of 'CS_ClaimStatusID'.
      * @return This builder.
      */
    public gms.cims.bridge.ClaimStatusClaimLink.Builder setCSClaimStatusID(int value) {
      validate(fields()[1], value);
      this.CS_ClaimStatusID = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'CS_ClaimStatusID' field has been set.
      * @return True if the 'CS_ClaimStatusID' field has been set, false otherwise.
      */
    public boolean hasCSClaimStatusID() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'CS_ClaimStatusID' field.
      * @return This builder.
      */
    public gms.cims.bridge.ClaimStatusClaimLink.Builder clearCSClaimStatusID() {
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClaimStatusClaimLink build() {
      try {
        ClaimStatusClaimLink record = new ClaimStatusClaimLink();
        record.CL_ClaimID = fieldSetFlags()[0] ? this.CL_ClaimID : (java.lang.Integer) defaultValue(fields()[0]);
        record.CS_ClaimStatusID = fieldSetFlags()[1] ? this.CS_ClaimStatusID : (java.lang.Integer) defaultValue(fields()[1]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ClaimStatusClaimLink>
    WRITER$ = (org.apache.avro.io.DatumWriter<ClaimStatusClaimLink>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ClaimStatusClaimLink>
    READER$ = (org.apache.avro.io.DatumReader<ClaimStatusClaimLink>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
