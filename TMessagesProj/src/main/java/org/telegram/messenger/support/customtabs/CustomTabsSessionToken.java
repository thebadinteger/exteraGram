public class CustomTabsSessionToken {
    private static final String TAG = "CustomTabsSessionToken";
    private final ICustomTabsCallback mCallbackBinder;
    private final CustomTabsCallback mCallback;

    /* package */ static class DummyCallback extends ICustomTabsCallback.Stub {
        @Override
        public void onNavigationEvent(int navigationEvent, Bundle extras) {}

        @Override
        public void extraCallback(String callbackName, Bundle args) {}

        @Override
        public void onMessageChannelReady(Bundle extras) {}

        @Override
        public void onPostMessage(String message, Bundle extras) {}

        @Override
        public IBinder asBinder() {
            return this;
        }
    }

    /**
     * Obtain a {@link CustomTabsSessionToken} from an intent. See {@link CustomTabsIntent.Builder}
     * for ways to generate an intent for custom tabs.
     * @param intent The intent to generate the token from. This has to include an extra for
     *               {@link CustomTabsIntent#EXTRA_SESSION}.
     * @return The token that was generated.
     */
    public static CustomTabsSessionToken getSessionTokenFromIntent(Intent intent) {
        Bundle b = intent.getExtras();
        IBinder binder = BundleCompat.getBinder(b, CustomTabsIntent.EXTRA_SESSION);
        if (binder == null) return null;
        return new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(binder));
    }

    /**
     * Provides browsers a way to generate a dummy {@link CustomTabsSessionToken} for testing
     * purposes.
     *
     * @return A dummy token with no functionality.
     */
    public static CustomTabsSessionToken createDummySessionTokenForTesting() {
        return new CustomTabsSessionToken(new DummyCallback());
    }

    CustomTabsSessionToken(ICustomTabsCallback callbackBinder) {
        mCallbackBinder = callbackBinder;
        mCallback = new CustomTabsCallback() {

            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                try {
                    mCallbackBinder.onNavigationEvent(navigationEvent, extras);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException during ICustomTabsCallback transaction");
                }
            }

            @Override
            public void extraCallback(String callbackName, Bundle args) {
                try {
                    mCallbackBinder.extraCallback(callbackName, args);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException during ICustomTabsCallback transaction");
                }
            }

            @Override
            public void onMessageChannelReady(Bundle extras) {
                try {
                    mCallbackBinder.onMessageChannelReady(extras);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException during ICustomTabsCallback transaction");
                }
            }

            @Override
            public void onPostMessage(String message, Bundle extras) {
                try {
                    mCallbackBinder.onPostMessage(message, extras);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException during ICustomTabsCallback transaction");
                }
            }
        };
    }

    IBinder getCallbackBinder() {
        return mCallbackBinder.asBinder();
    }

    @Override
    public int hashCode() {
        return getCallbackBinder().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomTabsSessionToken)) return false;
        CustomTabsSessionToken token = (CustomTabsSessionToken) o;
        return token.getCallbackBinder().equals(mCallbackBinder.asBinder());
    }

    /**
     * @return {@link CustomTabsCallback} corresponding to this session if there was any non-null
     *         callbacks passed by the client.
     */
    public CustomTabsCallback getCallback() {
        return mCallback;
    }

    /**
     * @return Whether this token is associated with the given session.
     */
    public boolean isAssociatedWith(CustomTabsSession session) {
        return session.getBinder().equals(mCallbackBinder);
    }
}