package com.songtaeheon.memoapp.interfaces;

import com.songtaeheon.memoapp.model.Memo;

public interface MyBroadcastListener {
    void addMemo(Memo memo);
    void deleteMemo(long memoId);
}
