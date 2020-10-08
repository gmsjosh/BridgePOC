/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Claim extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7185824311573873840L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Claim\",\"fields\":[{\"name\":\"CL_ClaimID\",\"type\":[\"int\",\"null\"],\"default\":-1},{\"name\":\"CS_ClaimStatusID\",\"type\":[\"int\",\"null\"],\"default\":-1},{\"name\":\"CS_Description\",\"type\":[\"string\",\"null\"],\"default\":\"NONE\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Claim> ENCODER =
      new BinaryMessageEncoder<Claim>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Claim> DECODER =
      new BinaryMessageDecoder<Claim>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<Claim> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<Claim> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Claim>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this Claim to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a Claim from a ByteBuffer. */
  public static Claim fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.Integer CL_ClaimID;
  @Deprecated public java.lang.Integer CS_ClaimStatusID;
  @Deprecated public java.lang.CharSequence CS_Description;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Claim() {}

  /**
   * All-args constructor.
   * @param CL_ClaimID The new value for CL_ClaimID
   * @param CS_ClaimStatusID The new value for CS_ClaimStatusID
   * @param CS_Description The new value for CS_Description
   */
  public Claim(java.lang.Integer CL_ClaimID, java.lang.Integer CS_ClaimStatusID, java.lang.CharSequence CS_Description) {
    this.CL_ClaimID = CL_ClaimID;
    this.CS_ClaimStatusID = CS_ClaimStatusID;
    this.CS_Description = CS_Description;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return CL_ClaimID;
    case 1: return CS_ClaimStatusID;
    case 2: return CS_Description;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: CL_ClaimID = (java.lang.Integer)value$; break;
    case 1: CS_ClaimStatusID = (java.lang.Integer)value$; break;
    case 2: CS_Description = (java.lang.CharSequence)value$; break;
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
   * Gets the value of the 'CS_Description' field.
   * @return The value of the 'CS_Description' field.
   */
  public java.lang.CharSequence getCSDescription() {
    return CS_Description;
  }

  /**
   * Sets the value of the 'CS_Description' field.
   * @param value the value to set.
   */
  public void setCSDescription(java.lang.CharSequence value) {
    this.CS_Description = value;
  }

  /**
   * Creates a new Claim RecordBuilder.
   * @return A new Claim RecordBuilder
   */
  public static Claim.Builder newBuilder() {
    return new Claim.Builder();
  }

  /**
   * Creates a new Claim RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Claim RecordBuilder
   */
  public static Claim.Builder newBuilder(Claim.Builder other) {
    return new Claim.Builder(other);
  }

  /**
   * Creates a new Claim RecordBuilder by copying an existing Claim instance.
   * @param other The existing instance to copy.
   * @return A new Claim RecordBuilder
   */
  public static Claim.Builder newBuilder(Claim other) {
    return new Claim.Builder(other);
  }

  /**
   * RecordBuilder for Claim instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Claim>
    implements org.apache.avro.data.RecordBuilder<Claim> {

    private java.lang.Integer CL_ClaimID;
    private java.lang.Integer CS_ClaimStatusID;
    private java.lang.CharSequence CS_Description;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(Claim.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.CL_ClaimID)) {
        this.CL_ClaimID = data().deepCopy(fields()[0].schema(), other.CL_ClaimID);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.CS_ClaimStatusID)) {
        this.CS_ClaimStatusID = data().deepCopy(fields()[1].schema(), other.CS_ClaimStatusID);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.CS_Description)) {
        this.CS_Description = data().deepCopy(fields()[2].schema(), other.CS_Description);
        fieldSetFlags()[2] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Claim instance
     * @param other The existing instance to copy.
     */
    private Builder(Claim other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.CL_ClaimID)) {
        this.CL_ClaimID = data().deepCopy(fields()[0].schema(), other.CL_ClaimID);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.CS_ClaimStatusID)) {
        this.CS_ClaimStatusID = data().deepCopy(fields()[1].schema(), other.CS_ClaimStatusID);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.CS_Description)) {
        this.CS_Description = data().deepCopy(fields()[2].schema(), other.CS_Description);
        fieldSetFlags()[2] = true;
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
    public Claim.Builder setCLClaimID(java.lang.Integer value) {
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
    public Claim.Builder clearCLClaimID() {
      CL_ClaimID = null;
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
    public Claim.Builder setCSClaimStatusID(java.lang.Integer value) {
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
    public Claim.Builder clearCSClaimStatusID() {
      CS_ClaimStatusID = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'CS_Description' field.
      * @return The value.
      */
    public java.lang.CharSequence getCSDescription() {
      return CS_Description;
    }

    /**
      * Sets the value of the 'CS_Description' field.
      * @param value The value of 'CS_Description'.
      * @return This builder.
      */
    public Claim.Builder setCSDescription(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.CS_Description = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'CS_Description' field has been set.
      * @return True if the 'CS_Description' field has been set, false otherwise.
      */
    public boolean hasCSDescription() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'CS_Description' field.
      * @return This builder.
      */
    public Claim.Builder clearCSDescription() {
      CS_Description = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Claim build() {
      try {
        Claim record = new Claim();
        record.CL_ClaimID = fieldSetFlags()[0] ? this.CL_ClaimID : (java.lang.Integer) defaultValue(fields()[0]);
        record.CS_ClaimStatusID = fieldSetFlags()[1] ? this.CS_ClaimStatusID : (java.lang.Integer) defaultValue(fields()[1]);
        record.CS_Description = fieldSetFlags()[2] ? this.CS_Description : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Claim>
    WRITER$ = (org.apache.avro.io.DatumWriter<Claim>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Claim>
    READER$ = (org.apache.avro.io.DatumReader<Claim>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
