package com.exteragram.messenger.export.api;

import org.telegram.messenger.Utilities;

public class ApiWrap$StoriesProcess {
    public Utilities.CallbackReturn<ApiWrap$DownloadProgress, Boolean> fileProgress;
    public Runnable finish;
    public Utilities.CallbackReturn<ApiWrap$StoriesSlice, Boolean> handleSlice;
    public ApiWrap$StoriesSlice slice;
    public Utilities.CallbackReturn<Integer, Boolean> start;
    public int processed = 0;
    public int offsetId = 0;
    public boolean lastSlice = false;
    public int fileIndex = 0;
}
