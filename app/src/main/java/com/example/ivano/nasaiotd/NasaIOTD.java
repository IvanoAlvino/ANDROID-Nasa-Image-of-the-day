package com.example.ivano.nasaiotd;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;


public class NasaIOTD extends AppCompatActivity {

    private Handler threadHandler;
    private IotdHandler handler;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Strict mode for internet
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_nasa_iotd);

        threadHandler = new Handler();

        refreshContent();
    }

    private void resetDisplay( String title, String date, Bitmap image, String description ) {

        TextView titleView = (TextView) findViewById(R.id.imageTitle);
        titleView.setText(title);

        TextView dateView = (TextView) findViewById(R.id.imageDate);
        dateView.setText(date);

        this.image = image;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(this.image);

        TextView descriptionView = (TextView) findViewById(R.id.imageDescription);
        descriptionView.setText(description);

        setTitle("");
    }

    public void onRefresh(View view) {
        refreshContent();
    }

    private void refreshContent() {
        // create progress dialog
        final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Loading image of the day");

        // starting new thread to heavy process, leaving UI operations lightweight
        Thread th = new Thread() {
            @Override
            public void run() {
                if ( handler == null ) {
                    handler = new IotdHandler();
                }
                handler.processFeed();

                // Use handler for UI operations (only UI thread can make UI changes)
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetDisplay(handler.getTitle(), handler.getDate(), handler.getImage(), handler.getDescription().toString());
                        dialog.dismiss();
                    }
                });
            }
        };

        th.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nasa_iotd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSetWallpaper(View view) throws IOException {

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    WallpaperManager wallManager = WallpaperManager.getInstance(NasaIOTD.this);
                    wallManager.setBitmap(image);
                    // Wallpaper set toast
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NasaIOTD.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    // error setting wallpaper toast
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NasaIOTD.this, "Error setting wallpaper", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        th.start();

    }
}
