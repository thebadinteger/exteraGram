package com.yandex.mapkit.location;

public class SubscriptionSettings {
    private UseInBackground useInBackground;
    private Purpose purpose;

    public SubscriptionSettings() {}

    public SubscriptionSettings(UseInBackground useInBackground, Purpose purpose) {
        this.useInBackground = useInBackground;
        this.purpose = purpose;
    }

    public UseInBackground getUseInBackground() {
        return this.useInBackground;
    }

    public Purpose getPurpose() {
        return this.purpose;
    }
}
