public abstract class CustomTabsServiceConnection implements ServiceConnection {

    @Override
    public final void onServiceConnected(ComponentName name, IBinder service) {
        onCustomTabsServiceConnected(name, new CustomTabsClient(
                ICustomTabsService.Stub.asInterface(service), name) {
        });
    }

    /**
     * Called when a connection to the {@link CustomTabsService} has been established.
     * @param name   The concrete component name of the service that has been connected.
     * @param client {@link CustomTabsClient} that contains the {@link IBinder} with which the
     *               connection have been established. All further communication should be initiated
     *               using this client.
     */
    public abstract void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client);
}
