package edu.sfsu.csc780.pictachio;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.koushikdutta.ion.Ion;

import uk.co.senab.photoview.PhotoView;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = Uri.parse(getIntent().getStringExtra("image"));
        if (uri.toString().endsWith(".mp4")) {
            setContentView(R.layout.activity_detail);
            VideoView videoView = (VideoView) findViewById(R.id.video_view);
            videoView.setVideoURI(uri);
            videoView.setMediaController(new MediaController(this));
            videoView.requestFocus();
            videoView.start();
        } else {
            PhotoView photoView = new PhotoView(this);
            photoView.setMaximumScale(16);
            setContentView(photoView);
            Ion.with(photoView)
                    .fitCenter()
                    .smartSize(true)
                    .deepZoom()
                    .load(uri.getPath());
        }
    }

}
