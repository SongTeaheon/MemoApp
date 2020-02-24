package com.songtaeheon.memoapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.songtaeheon.memoapp.interfaces.MyBroadcastListener;
import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.R;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private MyBroadcastListener mListener;
    public MyBroadcastReceiver(MyBroadcastListener listener){
        this.mListener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Memo memo = (Memo)intent.getSerializableExtra(context.getString(R.string.memo));
        if(action.equals(context.getString(R.string.actionAdd))){
            mListener.addMemo(memo);
        }else if(action.equals(context.getString(R.string.actionDelete))){
            mListener.deleteMemo(memo.getId());
        }
    }
}
