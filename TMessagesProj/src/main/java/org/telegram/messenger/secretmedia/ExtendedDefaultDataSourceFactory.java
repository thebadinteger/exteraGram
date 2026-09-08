public ExtendedDefaultDataSourceFactory(Context context, String userAgent) {
        this(context, userAgent, null);
    }

    /**
     * @param context A context.
     * @param userAgent The User-Agent string that should be used.
     * @param listener An optional listener.
     */
    public ExtendedDefaultDataSourceFactory(Context context, String userAgent,
                                    TransferListener listener) {
        this(context, listener, new DefaultHttpDataSourceFactory(userAgent, listener));
    }

    /**
     * @param context A context.
     * @param listener An optional listener.
     * @param baseDataSourceFactory A {@link DataSource.Factory} to be used to create a base {@link DataSource}
     *     for {@link DefaultDataSource}.
     * @see DefaultDataSource#DefaultDataSource(Context, TransferListener, DataSource)
     */
    public ExtendedDefaultDataSourceFactory(Context context, TransferListener listener,
                                    DataSource.Factory baseDataSourceFactory) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.baseDataSourceFactory = baseDataSourceFactory;
    }

    private final LongSparseArray<Uri> mtprotoUris = new LongSparseArray<>();

    @Override
    public ExtendedDefaultDataSource createDataSource() {
        return new ExtendedDefaultDataSource(context, listener, baseDataSourceFactory.createDataSource(), mtprotoUris);
    }

    public void putDocumentUri(long docId, Uri uri) {
        mtprotoUris.put(docId, uri);
    }

}