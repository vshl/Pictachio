package edu.sfsu.csc780.pictachio;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.io.File;

public class GalleryFragment extends Fragment {
    private MyAdapter mAdapter;

    private class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position > getCount() - 4)
                loadMore();

            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_image_item, null);

            final ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);

            Ion.with(iv)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .load(getItem(position));


            return convertView;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        int cols = getResources().getDisplayMetrics().widthPixels /
                getResources().getDisplayMetrics().densityDpi * 2;

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setNumColumns(cols);
        mAdapter = new MyAdapter(getActivity());
        gridView.setAdapter(mAdapter);

        loadMore();
        return view;
    }

    Cursor mediaCursor;
    public void loadMore() {
        if (mediaCursor == null) {
            mediaCursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, null, null, null);
        }

        int loaded = 0;
        assert mediaCursor != null;
        while (mediaCursor.moveToNext() && loaded < 10) {
            // get the media type. ion can show images for both regular images AND video.
            int mediaType = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    && mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                continue;
            }

            loaded++;

            String uri = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            File file = new File(uri);
            // turn this into a file uri if necessary/possible
            if (file.exists())
                mAdapter.add(file.toURI().toString());
            else
                mAdapter.add(uri);
        }
    }

}
