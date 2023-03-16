package com.yagnikfadadu.librarymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class ReturnBookActivity extends AppCompatActivity {

    int rat=-1;
    ImageView coverPhoto;
    ImageView rating;

    SeekBar ratingSeekbar;

    TextView bookName;
    TextView authorReturn;
    TextView publication;
    TextView field;
    TextView issueDate;
    TextView askRating;

    String nameString;
    String authorString;
    String issueDateString;
    String urlString;
    MaterialButton returnBook;
    String recordID;

    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);

    MongoDatabase bookDatabase = mongoClient.getDatabase("books");
    MongoCollection<Document> booksCollection = bookDatabase.getCollection("books");

    MongoDatabase usersDatabase = mongoClient.getDatabase("users");
    MongoCollection<Document> usersCollection = usersDatabase.getCollection("users");

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_book);
        getSupportActionBar().setTitle("My Book");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3867D6")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        coverPhoto = findViewById(R.id.return_book_image);
        bookName = findViewById(R.id.return_book_name);
        authorReturn = findViewById(R.id.book_return_author);
        publication = findViewById(R.id.return_publication);
        field = findViewById(R.id.return_field);
        issueDate = findViewById(R.id.issuedate_return);
        returnBook = findViewById(R.id.return_button);
        ratingSeekbar = findViewById(R.id.rating_seekbar);
        rating = findViewById(R.id.return_image_ratings);

        Intent intent = getIntent();
        nameString = intent.getStringExtra("name");
        authorString = intent.getStringExtra("author");
        issueDateString = intent.getStringExtra("issueDate");
        urlString = intent.getStringExtra("url");
        recordID = intent.getStringExtra("id");
        Document bookDocument = booksCollection.find(Filters.eq("name",nameString)).first();
        String fieldString = bookDocument.getString("field");
        String publicationString = bookDocument.getString("publication");

        field.setText("Field : "+fieldString);
        publication.setText("Publication : "+publicationString);



        Bitmap bmp = null;
        try {
            URL url = new URL(urlString);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        coverPhoto.setImageBitmap(bmp);
        bookName.setText(nameString);
        authorReturn.setText(authorString);
        issueDate.setText("Issued on: "+issueDateString);



        ratingSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                switch (i){
                    case 0:
                        rating.setImageResource(R.drawable.zero_star);
                        rat=0;
                        break;
                    case 1:
                        rating.setImageResource(R.drawable.one_star);
                        rat=1;
                        break;
                    case 2:
                        rating.setImageResource(R.drawable.two_star);
                        rat=2;
                        break;
                    case 3:
                        rating.setImageResource(R.drawable.three_star);
                        rat=3;
                        break;
                    case 4:
                        rating.setImageResource(R.drawable.four_star);
                        rat=4;
                        break;
                    case 5:
                        rating.setImageResource(R.drawable.five_star);
                        rat=5;
                        break;
                    default:
                        rating.setImageResource(R.drawable.zero_star);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        returnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue requestQueue = Volley.newRequestQueue(ReturnBookActivity.this);

            }
        });

        returnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(ReturnBookActivity.this);

                SharedPreferences sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE);
                String enroll = sharedPreferences.getString("enroll", "");

                Document document = usersCollection.find(Filters.eq("_id", enroll)).first();

                String url = "https://LibraryManagementAPI.yagnikpatel.repl.co?email="+document.getString("email")+"&key=my-key&book="+bookName.getText()+"&id="+recordID+"&date="+issueDateString+"&rating="+rat;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("myTag",response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("myTag","Response Received");
                    }
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(jsonObjectRequest);
                Snackbar.make(bookName,"OTP sent to your registered E-Mail",Snackbar.LENGTH_SHORT).show();
            }
        });

    }

}