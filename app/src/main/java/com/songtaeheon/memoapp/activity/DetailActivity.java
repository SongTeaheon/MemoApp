package com.songtaeheon.memoapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.songtaeheon.memoapp.adapter.PictureAdapter;
import com.songtaeheon.memoapp.db.DBHandler;
import com.songtaeheon.memoapp.adapter.DetailAdapter;
import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.model.Picture;
import com.songtaeheon.memoapp.R;
import com.songtaeheon.memoapp.utils.BroadcastUtil;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private final String TAG = "DetailActivity";

    private static final int REQUEST_MODIFY = 1011;

    private TextView mTitleTextView;
    private TextView mDetailTextView;
    private ViewPager mViewPager;
    private Memo mMemo;

    private ArrayList<Picture> mPictureList;
    private DBHandler dbHandler = null;
    private DetailAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = findViewById(R.id.title_edittext);
        mDetailTextView = findViewById(R.id.detail_edittext);
        mViewPager = findViewById(R.id.full_viewPager) ;

        Intent intent = getIntent();
        mMemo = (Memo) intent.getSerializableExtra(getString(R.string.memo));
        dbHandler = new DBHandler(this);

        if(mMemo != null) {
            mTitleTextView.setText(mMemo.getTitle());
            mDetailTextView.setText(mMemo.getDetail());
            if(mMemo.getTitle().equals("")) mTitleTextView.setHint(getString(R.string.noTitle));
            if(mMemo.getDetail().equals("")) mDetailTextView.setHint(getString(R.string.noDetail));

            mPictureList = dbHandler.getAllPictures(mMemo.getId());
            if(mPictureList.size() >= 1) mViewPager.setVisibility(View.VISIBLE);

            setUpViewPager();
        }else{
            Log.e(TAG, "memo from intent is null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            dbHandler.deleteAllPicture(mMemo.getId());
            dbHandler.deleteMemo(mMemo.getId());
            BroadcastUtil.sendMemo(mMemo, this, getString(R.string.actionDelete));
            finish();
        }else if (id == R.id.action_modify) {
            Intent intent = new Intent(this, ModificationActivity.class);
            intent.putExtra(getString(R.string.memo), mMemo);
            startActivity(intent);
            finish();
        }
        return true;
    }

    void setUpViewPager(){
        int dpValue = 54;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int) (dpValue * d);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(margin, 0, margin, 0);
        mViewPager.setPageMargin(margin/2);

        pagerAdapter = new DetailAdapter(this, mPictureList, false, null) ;
        mViewPager.setAdapter(pagerAdapter) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == DetailAdapter.REQUEST_FOR_PICTURE_ACTIVITY && resultCode == RESULT_OK){
            int position = data.getIntExtra(getString(R.string.position), 0);
            mViewPager.setCurrentItem(position);
        }
    }
}
