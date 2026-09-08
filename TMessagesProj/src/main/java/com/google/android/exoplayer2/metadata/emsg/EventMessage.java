EventMessage(Parcel in) {
    schemeIdUri = castNonNull(in.readString());
    value = castNonNull(in.readString());
    durationMs = in.readLong();
    id = in.readLong();
    messageData = castNonNull(in.createByteArray());
  }

  @Override
  @Nullable
  public Format getWrappedMetadataFormat() {
    switch (schemeIdUri) {
      case ID3_SCHEME_ID_AOM:
      case ID3_SCHEME_ID_APPLE:
        return ID3_FORMAT;
      case SCTE35_SCHEME_ID:
        return SCTE35_FORMAT;
      default:
        return null;
    }
  }

  @Override
  @Nullable
  public byte[] getWrappedMetadataBytes() {
    return getWrappedMetadataFormat() != null ? messageData : null;
  }

  @Override
  public int hashCode() {
    if (hashCode == 0) {
      int result = 17;
      result = 31 * result + (schemeIdUri != null ? schemeIdUri.hashCode() : 0);
      result = 31 * result + (value != null ? value.hashCode() : 0);
      result = 31 * result + (int) (durationMs ^ (durationMs >>> 32));
      result = 31 * result + (int) (id ^ (id >>> 32));
      result = 31 * result + Arrays.hashCode(messageData);
      hashCode = result;
    }
    return hashCode;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    EventMessage other = (EventMessage) obj;
    return durationMs == other.durationMs
        && id == other.id
        && Util.areEqual(schemeIdUri, other.schemeIdUri)
        && Util.areEqual(value, other.value)
        && Arrays.equals(messageData, other.messageData);
  }

  @Override
  public String toString() {
    return "EMSG: scheme="
        + schemeIdUri
        + ", id="
        + id
        + ", durationMs="
        + durationMs
        + ", value="
        + value;
  }

  // Parcelable implementation.

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(schemeIdUri);
    dest.writeString(value);
    dest.writeLong(durationMs);
    dest.writeLong(id);
    dest.writeByteArray(messageData);
  }

  public static final Parcelable.Creator<EventMessage> CREATOR =
      new Parcelable.Creator<EventMessage>() {

        @Override
        public EventMessage createFromParcel(Parcel in) {
          return new EventMessage(in);
        }

        @Override
        public EventMessage[] newArray(int size) {
          return new EventMessage[size];
        }
      };
}
