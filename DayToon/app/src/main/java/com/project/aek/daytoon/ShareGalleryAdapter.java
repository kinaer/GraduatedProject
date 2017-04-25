package com.project.aek.daytoon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.aek.daytoon.networking.beans.UploadFile;

import java.util.ArrayList;
import java.util.List;

public class ShareGalleryAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<UploadFile> mDataList;

    public ShareGalleryAdapter(Context context) {
        this.context = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_file, parent, false);
        return new ShareGalleryViewHolder(itemView);
    }

    public final static class ShareGalleryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView fileNameView;

        public ShareGalleryViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.ivImage);
            fileNameView = (TextView)itemView.findViewById(R.id.tvFileName);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UploadFile file = mDataList.get(position);

        ShareGalleryViewHolder h = (ShareGalleryViewHolder)holder;
        h.fileNameView.setText(file.getFilename());
        Glide.with(context).load("http://220.127.231.120:8080/uploads/" + file.getFilename()).fitCenter().into(h.imageView);
    }

    public void addItem(UploadFile file) {
        mDataList.add(file);
    }

    public UploadFile getItem(int position) {
        return mDataList.get(position);
    }

    public void removeAll() {
        mDataList.clear();
    }

    public void removeAtIndex(int position) {
        mDataList.remove(position);
    }
}
