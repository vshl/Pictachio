package edu.sfsu.csc780.pictachio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Uri uri = Uri.parse(getIntent().getStringExtra("image"));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        Toast.makeText(this.getBaseContext(), uri.getPath(), Toast.LENGTH_SHORT).show();
        ImageView iv = (ImageView) findViewById(R.id.imageDetail);
        int w = (int) (bitmap.getWidth() * 0.5);
        int h = (int) (bitmap.getHeight() * 0.5);
        Ion.with(iv)
                .resize(w, h)
                .fitXY()
                .centerCrop()
                .load(uri.getPath());
    }

}
