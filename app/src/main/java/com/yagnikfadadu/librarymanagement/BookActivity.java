package com.yagnikfadadu.librarymanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yagnikfadadu.librarymanagement.ModalClass.BookModal;

import org.bson.Document;

import java.io.IOException;
import java.net.URL;

public class BookActivity extends AppCompatActivity {

    ImageView bookImage;
    ImageView rating;

    TextView name;
    TextView field;
    TextView price;
    TextView author;
    TextView available;
    TextView description;
    TextView publication;
    TextView totalRatings;

    String urlString;
    String nameString;
    String authorString;
    String idString;

    MaterialButton wishlistButton;
    MaterialButton issueButton;

    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);

    MongoDatabase bookDatabase = mongoClient.getDatabase("books");
    MongoCollection<Document> booksCollection = bookDatabase.getCollection("books");

    MongoDatabase recordDatabase = mongoClient.getDatabase("records");
    MongoCollection<Document> recordCollection = recordDatabase.getCollection("records");

    MongoDatabase database = mongoClient.getDatabase("users");
    MongoCollection<Document> collection = database.getCollection("users");

    BookModal bookModal = new BookModal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3867D6")));

        bookImage = findViewById(R.id.book_image);
        name = findViewById(R.id.book_name);
        author = findViewById(R.id.book_author);
        rating = findViewById(R.id.book_image_ratings);
        description = findViewById(R.id.text_description);
        totalRatings = findViewById(R.id.text_ratings);
        publication = findViewById(R.id.text_publication);
        field = findViewById(R.id.text_filed);
        price = findViewById(R.id.text_price);
        available = findViewById(R.id.text_available);
        issueButton = findViewById(R.id.book_issue);
        wishlistButton = findViewById(R.id.book_wishlist);


        Intent intent = getIntent();
        urlString = intent.getStringExtra("url");
        nameString = intent.getStringExtra("name");
        authorString = intent.getStringExtra("author");
        idString = intent.getStringExtra("id");

        getSupportActionBar().setTitle(nameString);

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

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getBookDetails(idString);
            }
        }.start();

        issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(BookActivity.this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code\n\n");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setCaptureActivity(CustomOrientationScanner.class);
                intentIntegrator.initiateScan();
                intentIntegrator.setRequestCode(1);
            }
        });

    }

    public void getBookDetails(String id){
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc!=null){

            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    bookModal.setId(doc.getString("_id"));
                    bookModal.setName(doc.getString("name"));
                    bookModal.setAuthor(doc.getString("author"));
                    bookModal.setRating(doc.getDouble("rating"));
                    bookModal.setTotalRatedUser(doc.getInteger("totalrateduser"));
                    bookModal.setPublication(doc.getString("publication"));
                    bookModal.setField(doc.getString("field"));
                    bookModal.setDescription(doc.getString("description"));
                    bookModal.setPrice(doc.getDouble("price"));
                    bookModal.setTotalIssues(doc.getInteger("totalissues"));
                    bookModal.setAvailable(doc.getInteger("available"));
                    bookModal.setQuantity(doc.getInteger("quantity"));

                    publication.setText(bookModal.getPublication());
                    field.setText("Field: "+bookModal.getField());
                    description.setText(bookModal.getDescription());
                    price.setText("Price: "+bookModal.getPrice());
                    int totalInt = bookModal.getQuantity();
                    int availableInt = bookModal.getAvailable();
                    if (availableInt==0) {
                        available.setTextColor(Color.parseColor("#c41e00"));
                    }else if (availableInt<=totalInt*0.35) {
                        available.setTextColor(Color.parseColor("#ffb300"));
                    }

                    available.setText("Available: " + availableInt + "/" + totalInt);

                    if (bookModal.getAvailable()<=0){
                        issueButton.setText("Unavailable");
                        issueButton.setEnabled(false);
                        issueButton.setBackgroundColor(Color.parseColor("#B9B9B9"));
                    }

                    int bookRating;

                    if ((int)bookModal.getTotalRatedUser()>0) {
                        bookRating = (int) (bookModal.getRating() / bookModal.getTotalRatedUser());
                    }else {
                        bookRating = 0;
                    }
                    totalRatings.setText(""+bookRating+"/5 ("+bookModal.getTotalRatedUser()+")");

                    switch (bookRating){
                        case 0:
                            rating.setImageResource(R.drawable.zero_star);
                            break;
                        case 1:
                            rating.setImageResource(R.drawable.one_star);
                            break;
                        case 2:
                            rating.setImageResource(R.drawable.two_star);
                            break;
                        case 3:
                            rating.setImageResource(R.drawable.three_star);
                            break;
                        case 4:
                            rating.setImageResource(R.drawable.four_star);
                            break;
                        default:
                            rating.setImageResource(R.drawable.five_star);
                            break;
                    }

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Toast.makeText(this, ""+intentResult.getContents(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "Please scan valid QR Code", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void generateTransaction(String bookID){
        SharedPreferences sharedPreferences = getSharedPreferences("shared",MODE_PRIVATE);
        String enroll = sharedPreferences.getString("enroll","");
        if (enroll.isEmpty()){
            Toast.makeText(this, "Please Login Again", Toast.LENGTH_SHORT).show();
            return;
        }else {

        }
    }

}