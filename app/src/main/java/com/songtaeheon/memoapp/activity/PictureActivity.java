package com.songtaeheon.memoapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.songtaeheon.memoapp.model.Picture;
import com.songtaeheon.memoapp.adapter.PictureAdapter;
import com.songtaeheon.memoapp.R;

import java.util.ArrayList;

public class PictureActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private int mPosition;

    private ArrayList<Picture> mPictureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mViewPager = findViewById(R.id.full_viewPager);

        Intent intent = getIntent();
        mPictureList = (ArrayList<Picture>)intent.getSerializableExtra(getString(R.string.pictureList));
        mPosition = intent.getIntExtra("position", 0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


        setUpViewPager();
    }

    void setUpViewPager(){
        PictureAdapter mPagerAdapter = new PictureAdapter(this, mPictureList) ;
        mViewPager.setAdapter(mPagerAdapter) ;
        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.position), mPosition);
        setResult(RESULT_OK, intent);
        finish();
    }
}
