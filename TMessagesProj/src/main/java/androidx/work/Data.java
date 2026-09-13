package androidx.work;
public final class Data {
    public static final Data EMPTY = new Data();
    public String getString(String key) { return null; }
    public int getInt(String key, int defaultValue) { return defaultValue; }
    public static class Builder {
        public Builder putString(String key, String value) { return this; }
        public Data build() { return new Data(); }
    }
}
