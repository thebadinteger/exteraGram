package com.exteragram.messenger.export.api;

import com.exteragram.messenger.export.output.html.HtmlWriter;
import java.util.ArrayList;
import org.telegram.messenger.Utilities;

public class ApiWrap$UserpicsProcess {
    public Utilities.CallbackReturn<ApiWrap$DownloadProgress, Boolean> fileProgress;
    public Runnable finish;
    public Utilities.CallbackReturn<ArrayList<HtmlWriter.Photo>, Boolean> handleSlice;
    public ArrayList<HtmlWriter.Photo> slice;
    public Utilities.CallbackReturn<ApiWrap$UserpicsInfo, Boolean> start;
    public int processed = 0;
    public long maxId = 0;
    public boolean lastSlice = false;
    public int fileIndex = 0;
}
