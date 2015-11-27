package edu.sfsu.csc780.pictachio;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.koushikdutta.ion.Ion;

import uk.co.senab.photoview.PhotoView;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhotoView photoView = new PhotoView(this);
        photoView.setMaximumScale(16);
        setContentView(photoView);
        Uri uri = Uri.parse(getIntent().getStringExtra("image"));
        Ion.with(photoView)
                .fitCenter()
                .smartSize(true)
                .deepZoom()
                .load(uri.getPath());
    }

}
