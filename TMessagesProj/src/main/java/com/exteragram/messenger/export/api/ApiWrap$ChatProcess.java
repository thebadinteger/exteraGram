package com.exteragram.messenger.export.api;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;

public class ApiWrap$ChatProcess {
    public Runnable done;
    public Utilities.CallbackReturn<ApiWrap$DownloadProgress, Boolean> fileProgress;
    public Utilities.CallbackReturn<ApiWrap$MessagesSlice, Boolean> handleSlice;
    public ApiWrap$DialogInfo info;
    public Utilities.Callback<TLRPC.messages_Messages> requestDone;
    public ApiWrap$MessagesSlice slice;
    public Utilities.CallbackReturn<ApiWrap$DialogInfo, Boolean> start;
    public int localSplitIndex = 0;
    public int largestIdPlusOne = 1;
    public ApiWrap$ParseMediaContext context = new ApiWrap$ParseMediaContext();
    public boolean lastSlice = false;
    public int fileIndex = 0;
}
