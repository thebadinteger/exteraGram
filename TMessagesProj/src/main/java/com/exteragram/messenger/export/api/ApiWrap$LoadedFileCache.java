package com.exteragram.messenger.export.api;

import java.util.Deque;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ApiWrap$LoadedFileCache {
    private final int _limit;
    private final HashMap<String, String> _map = new HashMap<>();
    private final Deque<String> _list = new ConcurrentLinkedDeque();

    public ApiWrap$LoadedFileCache(int i) {
        this._limit = i;
    }

    public void save(ApiWrap$FileLocation apiWrap$FileLocation, String str) {
        if (apiWrap$FileLocation == null) {
            return;
        }
        String strComputeLocationKey = DataTypesUtils.ComputeLocationKey(apiWrap$FileLocation);
        this._map.put(strComputeLocationKey, str);
        this._list.add(strComputeLocationKey);
        if (this._list.size() > this._limit) {
            String first = this._list.getFirst();
            this._list.removeFirst();
            this._map.remove(first);
        }
    }

    public String find(ApiWrap$FileLocation apiWrap$FileLocation) {
        if (apiWrap$FileLocation == null) {
            return null;
        }
        return this._map.get(DataTypesUtils.ComputeLocationKey(apiWrap$FileLocation));
    }
}
