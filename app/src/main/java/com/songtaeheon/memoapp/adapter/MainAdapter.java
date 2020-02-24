package com.songtaeheon.memoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.songtaeheon.memoapp.activity.DetailActivity;
import com.songtaeheon.memoapp.model.Memo;
import com.songtaeheon.memoapp.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final String TAG = "MainAdapter";
    private ArrayList<Memo> mData = null;
    private Context mContext;

    public MainAdapter(Context context, ArrayList<Memo> list ){
        this.mData = list;
        mContext = context;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String titleStr = mData.get(position).getTitle();
        String detailStr = mData.get(position).getDetail();
        String thumbnailUri = mData.get(position).getThumbnailUri();

        holder.titleTextView.setText(titleStr);
        holder.detailTextView.setText(detailStr);
        if(titleStr.equals("")) holder.titleTextView.setHint(mContext.getString(R.string.noTitle));
        if(detailStr.equals("")) holder.detailTextView.setHint(mContext.getString(R.string.noDetail));

        if(thumbnailUri != null){
            holder.thumbnailImageView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .asBitmap()
                    .load(thumbnailUri)
                    .thumbnail(1f)
                    .error(R.drawable.error_image)
                    .into(holder.thumbnailImageView);
        }else{
            holder.thumbnailImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView detailTextView;
        public ImageView thumbnailImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_edittext);
            detailTextView = itemView.findViewById(R.id.detail_edittext);
            thumbnailImageView = itemView.findViewById(R.id.thumbnail_imageview);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(mContext.getString(R.string.memo), mData.get(getAdapterPosition()));
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
