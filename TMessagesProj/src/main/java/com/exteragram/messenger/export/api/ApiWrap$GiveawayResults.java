package com.exteragram.messenger.export.api;

import java.util.ArrayList;

public class ApiWrap$GiveawayResults {
    public int additionalPeersCount;
    public String additionalPrize;
    public boolean all;
    public long channel;
    public long credits;
    public int launchId;
    public int months;
    public boolean refunded;
    public int unclaimedCount;
    public int untilDate;
    public ArrayList<Long> winners = new ArrayList<>();
    public int winnersCount;

    public ApiWrap$GiveawayResults(long j, int i, int i2, int i3, int i4, int i5, int i6, long j2, boolean z, boolean z2) {
        this.channel = j;
        this.untilDate = i;
        this.launchId = i2;
        this.additionalPeersCount = i3;
        this.winnersCount = i4;
        this.unclaimedCount = i5;
        this.months = i6;
        this.credits = j2;
        this.refunded = z;
        this.all = z2;
    }
}
