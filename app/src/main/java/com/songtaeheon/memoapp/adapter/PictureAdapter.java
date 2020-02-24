package com.songtaeheon.memoapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.songtaeheon.memoapp.model.Picture;
import com.songtaeheon.memoapp.R;

import java.util.ArrayList;

public class PictureAdapter extends PagerAdapter {

    private static final String TAG = "PictureAdapter";
    private ArrayList<Picture> mPictureList;
    private Context mContext=null;

    public PictureAdapter(Context context, ArrayList<Picture> mPictureList) {
        this.mContext = context;
        this.mPictureList = mPictureList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null ;
        if (mContext != null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.viewpager_big_item, container, false);

            ImageView imageView = view.findViewById(R.id.full_imageview) ;

            Glide.with(mContext)
                    .asBitmap()
                    .load(mPictureList.get(position).getUri())
                    .fitCenter()
                    .placeholder(R.drawable.loading_spinner)
                    .error(R.drawable.error_image)
                    .into(imageView);
        }else{
            Log.e(TAG, "context is null");
        }

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
}
