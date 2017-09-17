package com.example.administrator.simple_gallery;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Single_Image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__image);

        ImageView v2 = (ImageView)findViewById(R.id.imageView2);
        Intent i = getIntent();
        if (i !=null){
            String f = i.getStringExtra("img");
            v2.setImageURI(Uri.parse(f));
        }
    }
}
