package edu.sfsu.csc780.pictachio;


import android.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraRollFragment extends Fragment {

    ArrayList<String> imageList = new ArrayList<>();

    public CameraRollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageList = loadImages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        // Set padding for Tiles
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return recyclerView;
    }

    /**
     * Adapter to display recycler view
     */
    public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

        public ContentAdapter() {
            // no-op
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView iv;
            public ViewHolder(View itemView) {
                super(itemView);

                iv = (ImageView) itemView.findViewById(R.id.imageTile);
            }

            public ImageView getIv() {
                return iv;
            }

            public void setIv(RelativeLayout.LayoutParams layoutParams) {
                iv.setLayoutParams(layoutParams);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_camera_roll, parent, false);
            return new ViewHolder(itemView);
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

    }

    private ArrayList<String> loadImages() {
        Cursor cursor;
        ArrayList<String> imagePaths = new ArrayList<>();
        Uri queryUri = MediaStore.Files.getContentUri("external");

        cursor = getActivity().getContentResolver().query(queryUri, null, null, null, null);

        int loaded = 0;
        while ((cursor != null && cursor.moveToNext()) && loaded < 10) {
            int mediaType = cursor.getInt(cursor.getColumnIndex(
                    MediaStore.Files.FileColumns.MEDIA_TYPE));
            if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    && mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                continue;
            loaded++;

            String path = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            imagePaths.add(path);
        }
        if (cursor != null)
            cursor.close();

        return imagePaths;
    }
}