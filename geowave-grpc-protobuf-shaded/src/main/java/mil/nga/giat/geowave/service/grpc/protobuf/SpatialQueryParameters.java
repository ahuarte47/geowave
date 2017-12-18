// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GeoWaveVectorQuery.proto

package mil.nga.giat.geowave.service.grpc.protobuf;

/**
 * Protobuf type {@code SpatialQueryParameters}
 */
public  final class SpatialQueryParameters extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:SpatialQueryParameters)
    SpatialQueryParametersOrBuilder {
  // Use SpatialQueryParameters.newBuilder() to construct.
  private SpatialQueryParameters(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SpatialQueryParameters() {
    geometry_ = "";
    coordinates_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private SpatialQueryParameters(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.Builder subBuilder = null;
            if (baseParams_ != null) {
              subBuilder = baseParams_.toBuilder();
            }
            baseParams_ = input.readMessage(mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(baseParams_);
              baseParams_ = subBuilder.buildPartial();
            }

            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            geometry_ = s;
            break;
          }
          case 26: {
            if (!((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
              coordinates_ = new java.util.ArrayList<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate>();
              mutable_bitField0_ |= 0x00000004;
            }
            coordinates_.add(
                input.readMessage(mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.parser(), extensionRegistry));
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
        coordinates_ = java.util.Collections.unmodifiableList(coordinates_);
      }
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return mil.nga.giat.geowave.service.grpc.protobuf.VectorQueryService.internal_static_SpatialQueryParameters_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return mil.nga.giat.geowave.service.grpc.protobuf.VectorQueryService.internal_static_SpatialQueryParameters_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.class, mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.Builder.class);
  }

  private int bitField0_;
  public static final int BASEPARAMS_FIELD_NUMBER = 1;
  private mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters baseParams_;
  /**
   * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
   */
  public boolean hasBaseParams() {
    return baseParams_ != null;
  }
  /**
   * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
   */
  public mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters getBaseParams() {
    return baseParams_ == null ? mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.getDefaultInstance() : baseParams_;
  }
  /**
   * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
   */
  public mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamtersOrBuilder getBaseParamsOrBuilder() {
    return getBaseParams();
  }

  public static final int GEOMETRY_FIELD_NUMBER = 2;
  private volatile java.lang.Object geometry_;
  /**
   * <pre>
   *ISO 19107 Geometry text definition
   * </pre>
   *
   * <code>optional string geometry = 2;</code>
   */
  public java.lang.String getGeometry() {
    java.lang.Object ref = geometry_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      geometry_ = s;
      return s;
    }
  }
  /**
   * <pre>
   *ISO 19107 Geometry text definition
   * </pre>
   *
   * <code>optional string geometry = 2;</code>
   */
  public com.google.protobuf.ByteString
      getGeometryBytes() {
    java.lang.Object ref = geometry_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      geometry_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int COORDINATES_FIELD_NUMBER = 3;
  private java.util.List<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate> coordinates_;
  /**
   * <code>repeated .Coordinate coordinates = 3;</code>
   */
  public java.util.List<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate> getCoordinatesList() {
    return coordinates_;
  }
  /**
   * <code>repeated .Coordinate coordinates = 3;</code>
   */
  public java.util.List<? extends mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder> 
      getCoordinatesOrBuilderList() {
    return coordinates_;
  }
  /**
   * <code>repeated .Coordinate coordinates = 3;</code>
   */
  public int getCoordinatesCount() {
    return coordinates_.size();
  }
  /**
   * <code>repeated .Coordinate coordinates = 3;</code>
   */
  public mil.nga.giat.geowave.service.grpc.protobuf.Coordinate getCoordinates(int index) {
    return coordinates_.get(index);
  }
  /**
   * <code>repeated .Coordinate coordinates = 3;</code>
   */
  public mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder getCoordinatesOrBuilder(
      int index) {
    return coordinates_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (baseParams_ != null) {
      output.writeMessage(1, getBaseParams());
    }
    if (!getGeometryBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, geometry_);
    }
    for (int i = 0; i < coordinates_.size(); i++) {
      output.writeMessage(3, coordinates_.get(i));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (baseParams_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getBaseParams());
    }
    if (!getGeometryBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, geometry_);
    }
    for (int i = 0; i < coordinates_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, coordinates_.get(i));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters)) {
      return super.equals(obj);
    }
    mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters other = (mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters) obj;

    boolean result = true;
    result = result && (hasBaseParams() == other.hasBaseParams());
    if (hasBaseParams()) {
      result = result && getBaseParams()
          .equals(other.getBaseParams());
    }
    result = result && getGeometry()
        .equals(other.getGeometry());
    result = result && getCoordinatesList()
        .equals(other.getCoordinatesList());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    if (hasBaseParams()) {
      hash = (37 * hash) + BASEPARAMS_FIELD_NUMBER;
      hash = (53 * hash) + getBaseParams().hashCode();
    }
    hash = (37 * hash) + GEOMETRY_FIELD_NUMBER;
    hash = (53 * hash) + getGeometry().hashCode();
    if (getCoordinatesCount() > 0) {
      hash = (37 * hash) + COORDINATES_FIELD_NUMBER;
      hash = (53 * hash) + getCoordinatesList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code SpatialQueryParameters}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:SpatialQueryParameters)
      mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParametersOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return mil.nga.giat.geowave.service.grpc.protobuf.VectorQueryService.internal_static_SpatialQueryParameters_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return mil.nga.giat.geowave.service.grpc.protobuf.VectorQueryService.internal_static_SpatialQueryParameters_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.class, mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.Builder.class);
    }

    // Construct using mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
        getCoordinatesFieldBuilder();
      }
    }
    public Builder clear() {
      super.clear();
      if (baseParamsBuilder_ == null) {
        baseParams_ = null;
      } else {
        baseParams_ = null;
        baseParamsBuilder_ = null;
      }
      geometry_ = "";

      if (coordinatesBuilder_ == null) {
        coordinates_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000004);
      } else {
        coordinatesBuilder_.clear();
      }
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return mil.nga.giat.geowave.service.grpc.protobuf.VectorQueryService.internal_static_SpatialQueryParameters_descriptor;
    }

    public mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters getDefaultInstanceForType() {
      return mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.getDefaultInstance();
    }

    public mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters build() {
      mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters buildPartial() {
      mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters result = new mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (baseParamsBuilder_ == null) {
        result.baseParams_ = baseParams_;
      } else {
        result.baseParams_ = baseParamsBuilder_.build();
      }
      result.geometry_ = geometry_;
      if (coordinatesBuilder_ == null) {
        if (((bitField0_ & 0x00000004) == 0x00000004)) {
          coordinates_ = java.util.Collections.unmodifiableList(coordinates_);
          bitField0_ = (bitField0_ & ~0x00000004);
        }
        result.coordinates_ = coordinates_;
      } else {
        result.coordinates_ = coordinatesBuilder_.build();
      }
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters) {
        return mergeFrom((mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters other) {
      if (other == mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters.getDefaultInstance()) return this;
      if (other.hasBaseParams()) {
        mergeBaseParams(other.getBaseParams());
      }
      if (!other.getGeometry().isEmpty()) {
        geometry_ = other.geometry_;
        onChanged();
      }
      if (coordinatesBuilder_ == null) {
        if (!other.coordinates_.isEmpty()) {
          if (coordinates_.isEmpty()) {
            coordinates_ = other.coordinates_;
            bitField0_ = (bitField0_ & ~0x00000004);
          } else {
            ensureCoordinatesIsMutable();
            coordinates_.addAll(other.coordinates_);
          }
          onChanged();
        }
      } else {
        if (!other.coordinates_.isEmpty()) {
          if (coordinatesBuilder_.isEmpty()) {
            coordinatesBuilder_.dispose();
            coordinatesBuilder_ = null;
            coordinates_ = other.coordinates_;
            bitField0_ = (bitField0_ & ~0x00000004);
            coordinatesBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getCoordinatesFieldBuilder() : null;
          } else {
            coordinatesBuilder_.addAllMessages(other.coordinates_);
          }
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters baseParams_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters, mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.Builder, mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamtersOrBuilder> baseParamsBuilder_;
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public boolean hasBaseParams() {
      return baseParamsBuilder_ != null || baseParams_ != null;
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters getBaseParams() {
      if (baseParamsBuilder_ == null) {
        return baseParams_ == null ? mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.getDefaultInstance() : baseParams_;
      } else {
        return baseParamsBuilder_.getMessage();
      }
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public Builder setBaseParams(mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters value) {
      if (baseParamsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        baseParams_ = value;
        onChanged();
      } else {
        baseParamsBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public Builder setBaseParams(
        mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.Builder builderForValue) {
      if (baseParamsBuilder_ == null) {
        baseParams_ = builderForValue.build();
        onChanged();
      } else {
        baseParamsBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public Builder mergeBaseParams(mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters value) {
      if (baseParamsBuilder_ == null) {
        if (baseParams_ != null) {
          baseParams_ =
            mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.newBuilder(baseParams_).mergeFrom(value).buildPartial();
        } else {
          baseParams_ = value;
        }
        onChanged();
      } else {
        baseParamsBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public Builder clearBaseParams() {
      if (baseParamsBuilder_ == null) {
        baseParams_ = null;
        onChanged();
      } else {
        baseParams_ = null;
        baseParamsBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.Builder getBaseParamsBuilder() {
      
      onChanged();
      return getBaseParamsFieldBuilder().getBuilder();
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamtersOrBuilder getBaseParamsOrBuilder() {
      if (baseParamsBuilder_ != null) {
        return baseParamsBuilder_.getMessageOrBuilder();
      } else {
        return baseParams_ == null ?
            mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.getDefaultInstance() : baseParams_;
      }
    }
    /**
     * <code>optional .BaseGeoWaveQueryParamters baseParams = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters, mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.Builder, mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamtersOrBuilder> 
        getBaseParamsFieldBuilder() {
      if (baseParamsBuilder_ == null) {
        baseParamsBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters, mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamters.Builder, mil.nga.giat.geowave.service.grpc.protobuf.BaseGeoWaveQueryParamtersOrBuilder>(
                getBaseParams(),
                getParentForChildren(),
                isClean());
        baseParams_ = null;
      }
      return baseParamsBuilder_;
    }

    private java.lang.Object geometry_ = "";
    /**
     * <pre>
     *ISO 19107 Geometry text definition
     * </pre>
     *
     * <code>optional string geometry = 2;</code>
     */
    public java.lang.String getGeometry() {
      java.lang.Object ref = geometry_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        geometry_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     *ISO 19107 Geometry text definition
     * </pre>
     *
     * <code>optional string geometry = 2;</code>
     */
    public com.google.protobuf.ByteString
        getGeometryBytes() {
      java.lang.Object ref = geometry_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        geometry_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     *ISO 19107 Geometry text definition
     * </pre>
     *
     * <code>optional string geometry = 2;</code>
     */
    public Builder setGeometry(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      geometry_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *ISO 19107 Geometry text definition
     * </pre>
     *
     * <code>optional string geometry = 2;</code>
     */
    public Builder clearGeometry() {
      
      geometry_ = getDefaultInstance().getGeometry();
      onChanged();
      return this;
    }
    /**
     * <pre>
     *ISO 19107 Geometry text definition
     * </pre>
     *
     * <code>optional string geometry = 2;</code>
     */
    public Builder setGeometryBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      geometry_ = value;
      onChanged();
      return this;
    }

    private java.util.List<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate> coordinates_ =
      java.util.Collections.emptyList();
    private void ensureCoordinatesIsMutable() {
      if (!((bitField0_ & 0x00000004) == 0x00000004)) {
        coordinates_ = new java.util.ArrayList<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate>(coordinates_);
        bitField0_ |= 0x00000004;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        mil.nga.giat.geowave.service.grpc.protobuf.Coordinate, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder, mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder> coordinatesBuilder_;

    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public java.util.List<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate> getCoordinatesList() {
      if (coordinatesBuilder_ == null) {
        return java.util.Collections.unmodifiableList(coordinates_);
      } else {
        return coordinatesBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public int getCoordinatesCount() {
      if (coordinatesBuilder_ == null) {
        return coordinates_.size();
      } else {
        return coordinatesBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.Coordinate getCoordinates(int index) {
      if (coordinatesBuilder_ == null) {
        return coordinates_.get(index);
      } else {
        return coordinatesBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder setCoordinates(
        int index, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate value) {
      if (coordinatesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureCoordinatesIsMutable();
        coordinates_.set(index, value);
        onChanged();
      } else {
        coordinatesBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder setCoordinates(
        int index, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder builderForValue) {
      if (coordinatesBuilder_ == null) {
        ensureCoordinatesIsMutable();
        coordinates_.set(index, builderForValue.build());
        onChanged();
      } else {
        coordinatesBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder addCoordinates(mil.nga.giat.geowave.service.grpc.protobuf.Coordinate value) {
      if (coordinatesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureCoordinatesIsMutable();
        coordinates_.add(value);
        onChanged();
      } else {
        coordinatesBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder addCoordinates(
        int index, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate value) {
      if (coordinatesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureCoordinatesIsMutable();
        coordinates_.add(index, value);
        onChanged();
      } else {
        coordinatesBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder addCoordinates(
        mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder builderForValue) {
      if (coordinatesBuilder_ == null) {
        ensureCoordinatesIsMutable();
        coordinates_.add(builderForValue.build());
        onChanged();
      } else {
        coordinatesBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder addCoordinates(
        int index, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder builderForValue) {
      if (coordinatesBuilder_ == null) {
        ensureCoordinatesIsMutable();
        coordinates_.add(index, builderForValue.build());
        onChanged();
      } else {
        coordinatesBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder addAllCoordinates(
        java.lang.Iterable<? extends mil.nga.giat.geowave.service.grpc.protobuf.Coordinate> values) {
      if (coordinatesBuilder_ == null) {
        ensureCoordinatesIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, coordinates_);
        onChanged();
      } else {
        coordinatesBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder clearCoordinates() {
      if (coordinatesBuilder_ == null) {
        coordinates_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000004);
        onChanged();
      } else {
        coordinatesBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public Builder removeCoordinates(int index) {
      if (coordinatesBuilder_ == null) {
        ensureCoordinatesIsMutable();
        coordinates_.remove(index);
        onChanged();
      } else {
        coordinatesBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder getCoordinatesBuilder(
        int index) {
      return getCoordinatesFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder getCoordinatesOrBuilder(
        int index) {
      if (coordinatesBuilder_ == null) {
        return coordinates_.get(index);  } else {
        return coordinatesBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public java.util.List<? extends mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder> 
         getCoordinatesOrBuilderList() {
      if (coordinatesBuilder_ != null) {
        return coordinatesBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(coordinates_);
      }
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder addCoordinatesBuilder() {
      return getCoordinatesFieldBuilder().addBuilder(
          mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.getDefaultInstance());
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder addCoordinatesBuilder(
        int index) {
      return getCoordinatesFieldBuilder().addBuilder(
          index, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.getDefaultInstance());
    }
    /**
     * <code>repeated .Coordinate coordinates = 3;</code>
     */
    public java.util.List<mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder> 
         getCoordinatesBuilderList() {
      return getCoordinatesFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        mil.nga.giat.geowave.service.grpc.protobuf.Coordinate, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder, mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder> 
        getCoordinatesFieldBuilder() {
      if (coordinatesBuilder_ == null) {
        coordinatesBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            mil.nga.giat.geowave.service.grpc.protobuf.Coordinate, mil.nga.giat.geowave.service.grpc.protobuf.Coordinate.Builder, mil.nga.giat.geowave.service.grpc.protobuf.CoordinateOrBuilder>(
                coordinates_,
                ((bitField0_ & 0x00000004) == 0x00000004),
                getParentForChildren(),
                isClean());
        coordinates_ = null;
      }
      return coordinatesBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:SpatialQueryParameters)
  }

  // @@protoc_insertion_point(class_scope:SpatialQueryParameters)
  private static final mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters();
  }

  public static mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SpatialQueryParameters>
      PARSER = new com.google.protobuf.AbstractParser<SpatialQueryParameters>() {
    public SpatialQueryParameters parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new SpatialQueryParameters(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SpatialQueryParameters> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SpatialQueryParameters> getParserForType() {
    return PARSER;
  }

  public mil.nga.giat.geowave.service.grpc.protobuf.SpatialQueryParameters getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

