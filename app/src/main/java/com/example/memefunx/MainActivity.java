package com.example.memefunx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView memeImage;
    Button buttonShare, buttonNext;
    ProgressBar progressBar;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memeImage = findViewById(R.id.memeImage);
        buttonNext = findViewById(R.id.buttonNext);
        buttonShare = findViewById(R.id.buttonShare);
        progressBar = findViewById(R.id.progressBar);
        memeCall();
        buttonNext.setOnClickListener(v -> memeCall());
        buttonShare.setOnClickListener(v -> {
            try {
                shareImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void memeCall() {
        url = "https://meme-api.herokuapp.com/gimme";
        progressBar.setVisibility(View.INVISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        url = response.getString("url");
                        Glide.with(MainActivity.this).load(url).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(memeImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // TODO: Handle error
                    Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

public void shareImage() throws IOException {
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());
    BitmapDrawable drawable = (BitmapDrawable)memeImage.getDrawable();
    Bitmap bitmap = drawable.getBitmap();
    File f =new File(getExternalCacheDir()+"/"+"MemeX"+".png");
    Intent shareImage = new Intent(Intent.ACTION_SEND);
    FileOutputStream outputStream = new FileOutputStream(f);
    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
    outputStream.flush();
    outputStream.close();
    shareImage.setType("image/*");
    shareImage.putExtra(Intent.EXTRA_TEXT,"Hey check out this cool Meme ");
    shareImage.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
    shareImage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(shareImage);
}
}