package me.vshl.apps.pictachio.fragments;


import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;

import me.vshl.apps.pictachio.R;
import me.vshl.apps.pictachio.activities.DetailActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraRollFragment extends Fragment {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final ContentAdapter adapter = new ContentAdapter();
    private ArrayList<String> imageList = new ArrayList<>();
    private View mLayout;

    public CameraRollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = getActivity().findViewById(R.id.drawer_layout);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalStoragePermission();
        }
        updateAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    @Override
    public void onDestroy() {
        imageList = null;
        super.onDestroy();
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

    /**
     * Method to update the RecyclerView adapter
     */
    @SuppressWarnings("unchecked")
    public void updateAdapter() {
        new LoadImages().execute(imageList);
    }

    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_read_external_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, R.string.permission_available_read_external_storage,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mLayout, R.string.permission_not_granted, Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                    .inflate(R.layout.fragment_recycler_grid, parent, false);
            return new ViewHolder(context, itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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
            private final ImageView iv;
            private final Context context;

            public ViewHolder(Context context, View itemView) {
                super(itemView);

                this.iv = (ImageView) itemView.findViewById(R.id.imageTile);
                this.context = context;
                itemView.setOnClickListener(this);
            }

            public ImageView getIv() {
                return iv;
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

    /**
     * Method to create a background task for the image loading
     */
    private class LoadImages extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @SafeVarargs
        @Override
        protected final ArrayList<String> doInBackground(ArrayList<String>... params) {
            Cursor cursor;
            ArrayList<String> imagePaths = new ArrayList<>();
            Uri queryUri = MediaStore.Files.getContentUri("external");

            cursor = getActivity().getContentResolver().query(queryUri,
                    null,
                    MediaStore.Images.Media.DATA + " like ? ",
                    new String[]{"%DCIM/Camera%"},
                    null);

            while ((cursor != null && cursor.moveToNext())) {
                int mediaType = cursor.getInt(cursor.getColumnIndex(
                        MediaStore.Files.FileColumns.MEDIA_TYPE));
                if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        && mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                    continue;
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

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            imageList = strings;
            adapter.notifyDataSetChanged();
        }
    }
}
