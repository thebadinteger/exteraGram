package com.exteragram.messenger.export.api;

import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.Utilities;

public abstract class ApiWrap$ChatsProcess {
    public Utilities.Callback<ApiWrap$DialogsInfo> done;
    public Utilities.CallbackReturn<Integer, Boolean> progress;
    public ApiWrap$DialogsInfo info = new ApiWrap$DialogsInfo();
    public int processedCount = 0;
    public Map<Long, Integer> indexByPeer = new HashMap();
}
