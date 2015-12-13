package edu.sfsu.csc780.pictachio.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;

import edu.sfsu.csc780.pictachio.R;
import edu.sfsu.csc780.pictachio.activities.DetailActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraRollFragment extends Fragment {

    ArrayList<String> imageList = new ArrayList<>();
    ContentAdapter adapter = new ContentAdapter();

    public CameraRollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageList = loadImages();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        // Set padding for Tiles
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return recyclerView;
    }

    private ArrayList<String> loadImages() {
        Cursor cursor;
        ArrayList<String> imagePaths = new ArrayList<>();
        Uri queryUri = MediaStore.Files.getContentUri("external");

        cursor = getActivity().getContentResolver().query(queryUri,
                null,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[]{"%DCIM/Camera%"},
                null);

        int loaded = 0;
        while ((cursor != null && cursor.moveToNext()) && loaded < 10) {
            int mediaType = cursor.getInt(cursor.getColumnIndex(
                    MediaStore.Files.FileColumns.MEDIA_TYPE));
            if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    && mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                continue;
            loaded++;
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                String path = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                imagePaths.add(path);
            }
        }
        if (cursor != null)
            cursor.close();

        return imagePaths;
    }

    /**
     * Method to update the RecyclerView adapter
     */
    public void updateAdapter() {
        imageList = loadImages();
        adapter.notifyDataSetChanged();
    }

    /**
     * Adapter to display recycler view
     */
    public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

        public ContentAdapter() {
            // no-op
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater
                    .from(context)
                    .inflate(R.layout.fragment_camera_roll, parent, false);
            return new ViewHolder(context, itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            File imageFile = new File(imageList.get(position));
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    bitmap.getWidth(), bitmap.getHeight());
            holder.setIv(layoutParams);

            Ion.with(holder.getIv())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .load(imageList.get(position));
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView iv;
            private Context context;

            public ViewHolder(Context context, View itemView) {
                super(itemView);

                this.iv = (ImageView) itemView.findViewById(R.id.imageTile);
                this.context = context;
                itemView.setOnClickListener(this);
            }

            public ImageView getIv() {
                return iv;
            }

            public void setIv(RelativeLayout.LayoutParams layoutParams) {
                iv.setLayoutParams(layoutParams);
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                File imageFile = new File(imageList.get(position));
                Uri uri = Uri.fromFile(imageFile);

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("image", uri.toString());
                context.startActivity(intent);
            }
        }


    }
}
