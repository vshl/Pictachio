package edu.sfsu.csc780.pictachio.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.sfsu.csc780.pictachio.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // Provide a direct reference to each of the views within a data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        // Constructor that accepts an entire row item
        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
