package com.songtaeheon.memoapp.utils;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.R;

public class BroadcastUtil {
    public static void sendMemo(Memo memo, Context ctx, String action){
        Intent intent = new Intent(action);
        intent.putExtra(ctx.getResources().getString(R.string.memo), memo);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }
}
