package com.songtaeheon.memoapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.songtaeheon.memoapp.activity.PictureActivity;
import com.songtaeheon.memoapp.model.Picture;
import com.songtaeheon.memoapp.interfaces.OnPicDeleteListener;
import com.songtaeheon.memoapp.R;

import java.util.ArrayList;

public class DetailAdapter extends PagerAdapter {

    private static final String TAG = "DetailAdapter";

    public static final int REQUEST_FOR_PICTURE_ACTIVITY = 1021;
    private Context mContext = null ;
    private ArrayList<Picture> mPictureList;
    private OnPicDeleteListener listener;
    private boolean isModifying;

    public DetailAdapter(Context mContext, ArrayList<Picture> mPictureList, boolean isModifying, OnPicDeleteListener listener) {
        this.mContext = mContext;
        this.mPictureList = mPictureList;
        this.isModifying = isModifying;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewpager_small_item, container, false);

        ImageView imageView = view.findViewById(R.id.viewpager_imageview) ;
        TextView textView = view.findViewById(R.id.count_textview);

        String str = (position+1) + "/" + mPictureList.size();
        textView.setText(str);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PictureActivity.class);
                intent.putExtra(mContext.getString(R.string.pictureList), mPictureList);
                intent.putExtra(mContext.getString(R.string.position), position);

                ((Activity)mContext).startActivityForResult(intent, REQUEST_FOR_PICTURE_ACTIVITY);
            }
        });

        //수정 중!
        if(isModifying){
            ImageView deletePicButton = view.findViewById(R.id.x_imageview);
            deletePicButton.setVisibility(View.VISIBLE);
            deletePicButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.deleteOnePicture(mPictureList.get(position));
                    mPictureList.remove(position);
                    if(mPictureList.size() == 0) container.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }
            });
        }

        Glide.with(mContext)
                .asBitmap()
                .load(mPictureList.get(position).getUri())
                .fitCenter()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.error_image)
                .into(imageView);


        // 뷰페이저에 추가.
        container.addView(view) ;

        return view ;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mPictureList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }

    @Override
    public float getPageWidth(int position) {
        return (1f);
    }

    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
