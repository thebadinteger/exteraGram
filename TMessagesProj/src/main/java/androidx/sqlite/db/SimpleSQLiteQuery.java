package androidx.sqlite.db;
public class SimpleSQLiteQuery implements SupportSQLiteQuery {
    private final String query;
    private final Object[] args;
    public SimpleSQLiteQuery(String query) { this(query, null); }
    public SimpleSQLiteQuery(String query, Object[] args) { this.query = query; this.args = args; }
    public String getSql() { return query; }
}
