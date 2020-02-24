package com.songtaeheon.memoapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.songtaeheon.memoapp.adapter.MainAdapter;
import com.songtaeheon.memoapp.db.DBHandler;
import com.songtaeheon.memoapp.interfaces.MyBroadcastListener;
import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.R;
import com.songtaeheon.memoapp.utils.MyBroadcastReceiver;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyBroadcastListener {

    public static final int REQUEST_NEW_MEMO = 1001;
    public static final int REQUEST_SEE_MEMO = 1002;

    private TextView mNoticeTextView;
    private FloatingActionButton mNewMemoFab;

    private RecyclerView mRecyclerView;
    private MainAdapter mMainAdapter;

    private ArrayList<Memo> mMemoList;
    private DBHandler dbHandler = null;
    private MyBroadcastReceiver mMessageReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.memo_recyclerview);
        mNewMemoFab = findViewById(R.id.new_memo_fab);
        mNoticeTextView = findViewById(R.id.notice_textview);

        dbHandler = new DBHandler(this);
        mMessageReceiver = new MyBroadcastReceiver(this);

        //local broadcast register to manage modifying memo
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.actionAdd));
        filter.addAction(getString(R.string.actionDelete));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);


        mNewMemoFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModificationActivity.class);
                startActivity(intent);
            }
        });

        getAllMemos();
        setUpRecyclerView();
    }

    private void getAllMemos(){
        mMemoList = dbHandler.getAllMemo();
        if(mMemoList.size() == 0){
            mNoticeTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMainAdapter = new MainAdapter(this, mMemoList);//listener전달하기.
        mRecyclerView.setAdapter(mMainAdapter);
    }

    @Override
    public void addMemo(Memo memo) {
        mMemoList.add(0, memo);
        mMainAdapter.notifyItemInserted(0);
        if(mNoticeTextView.getVisibility() == View.VISIBLE)
             mNoticeTextView.setVisibility(View.GONE);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void deleteMemo(long memoId) {
        for(int i = 0; i < mMemoList.size(); i++){
            if(mMemoList.get(i).getId() == memoId) {
                mMemoList.remove(i);
                mMainAdapter.notifyItemRemoved(i);
                break;
            }
        }

        if(mMemoList.size() == 0)
            mNoticeTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
