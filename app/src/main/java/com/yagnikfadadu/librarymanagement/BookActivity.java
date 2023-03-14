package com.yagnikfadadu.librarymanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;

public class BookActivity extends AppCompatActivity {

    ImageView bookImage;
    TextView name;
    TextView author;
    TextView description;
    ImageView rating;
    String urlString;
    String nameString;
    String authorString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        bookImage = findViewById(R.id.book_image);
        name = findViewById(R.id.book_name);
        author = findViewById(R.id.book_author);

        Intent intent = getIntent();
        urlString = intent.getStringExtra("url");
        nameString = intent.getStringExtra("name");
        authorString = intent.getStringExtra("author");

        Bitmap bmp = null;
        try {
            URL url = new URL(urlString);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bookImage.setImageBitmap(bmp);

        name.setText(nameString);
        author.setText(authorString);


    }
}