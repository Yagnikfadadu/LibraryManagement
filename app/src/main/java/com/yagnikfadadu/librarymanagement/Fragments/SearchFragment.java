package com.yagnikfadadu.librarymanagement.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.yagnikfadadu.librarymanagement.Adapters.BookSearchAdapter;
import com.yagnikfadadu.librarymanagement.CustomOrientationScanner;
import com.yagnikfadadu.librarymanagement.ModalClass.BookModal;
import com.yagnikfadadu.librarymanagement.R;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    BookSearchAdapter bookSearchAdapter;
    ImageView scanQR;
    ArrayList<BookModal> bookModalArrayList;
    TextInputEditText searchEditText;
    LinearProgressIndicator linearProgressIndicator;
    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);

    MongoDatabase bookDatabase = mongoClient.getDatabase("books");
    MongoCollection<Document> booksCollection = bookDatabase.getCollection("books");

    MongoDatabase recordDatabase = mongoClient.getDatabase("records");
    MongoCollection<Document> recordCollection = recordDatabase.getCollection("records");

    MongoDatabase database = mongoClient.getDatabase("users");
    MongoCollection<Document> collection = database.getCollection("users");

    public SearchFragment(){
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        bookModalArrayList = new ArrayList<>();

        bookSearchAdapter = new BookSearchAdapter(getContext(),bookModalArrayList);


        recyclerView = view.findViewById(R.id.search_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        linearProgressIndicator = view.findViewById(R.id.linear_progress_bar);
        scanQR = view.findViewById(R.id.scan_image);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookSearchAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!searchEditText.getText().toString().isEmpty()){
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                searchWithParams(searchEditText.getText().toString().toLowerCase());
                            }
                        }.start();
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(SearchFragment.this);
                integrator.setPrompt("Scan Book QR code\n\n");
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(CustomOrientationScanner.class);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.initiateScan();
            }
        });

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initializeBookList();
            }
        }.start();

        return view;
    }

    public void initializeBookList(){
        MongoCursor<Document> cursor = booksCollection.find().iterator();
        while (cursor.hasNext()) {

            Document document = cursor.next();
            BookModal bookModal = new BookModal();

            bookModal.setId(document.getString("_id"));
            bookModal.setAuthor(document.getString("author"));
            bookModal.setCoverPhoto(document.getString("url"));
            bookModal.setName(document.getString("name"));
            bookModal.setTotalRatedUser(document.getInteger("totalrateduser"));
            bookModal.setRating(document.getDouble("rating"));

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearProgressIndicator.setVisibility(View.GONE);
                    bookModalArrayList.add(bookModal);
                    bookSearchAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void searchWithParams(String param){
        MongoCursor<Document> cursor = booksCollection.find(Filters.regex("name",".*"+param+".*","i")).iterator();
        MongoCursor<Document> cursor1 = booksCollection.find(Filters.regex("author",".*"+param+".*","i")).iterator();

        if (!cursor.hasNext() && !cursor1.hasNext()){
            Log.d("myDebug","Param:"+param);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bookModalArrayList.clear();
                    bookSearchAdapter.notifyDataSetChanged();
                }
            });
            return;
        }

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bookModalArrayList.clear();
                bookSearchAdapter.notifyDataSetChanged();
            }
        });

        while (cursor.hasNext()) {

            Document document = cursor.next();
            BookModal bookModal = new BookModal();

            bookModal.setId(document.getString("_id"));
            bookModal.setAuthor(document.getString("author"));
            bookModal.setCoverPhoto(document.getString("url"));
            bookModal.setName(document.getString("name"));
            bookModal.setTotalRatedUser(document.getInteger("totalrateduser"));
            bookModal.setRating(document.getDouble("rating"));

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearProgressIndicator.setVisibility(View.GONE);
                    bookModalArrayList.add(bookModal);
                    bookSearchAdapter.notifyDataSetChanged();
                }
            });
        }

        while (cursor1.hasNext()) {

            Document document = cursor1.next();
            BookModal bookModal = new BookModal();

            bookModal.setId(document.getString("_id"));
            bookModal.setAuthor(document.getString("author"));
            bookModal.setCoverPhoto(document.getString("url"));
            bookModal.setName(document.getString("name"));
            bookModal.setTotalRatedUser(document.getInteger("totalrateduser"));
            bookModal.setRating(document.getDouble("rating"));

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearProgressIndicator.setVisibility(View.GONE);
                    bookModalArrayList.add(bookModal);
                    bookSearchAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(bookSearchAdapter);
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
            } else {
                try {
                    if (generateTransaction(intentResult.getContents())){
                        Toast.makeText(requireContext(), "Book Issued Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Log.d("Exception",""+e);
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean generateTransaction(String bookID){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared",MODE_PRIVATE);
        String enroll = sharedPreferences.getString("enroll","");

        LocalDate currentDate = LocalDate.now();
        LocalDate dateAfter15Days = currentDate.plusDays(15);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String expected = dateAfter15Days.format(formatter);
        String issued = currentDate.format(formatter);

        if (enroll.isEmpty()){
            Toast.makeText(requireContext(), "Please Login Again", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            try {
                Document book = booksCollection.find(Filters.eq("_id", bookID)).first();
                Document user = collection.find(Filters.eq("_id", enroll)).first();

                if (book==null || user==null){
                    Toast.makeText(requireContext(), "Invalid QR", Toast.LENGTH_SHORT).show();
                    return false;
                }
                int bookCount = book.getInteger("available");
                int totalIssues = book.getInteger("totalissues");
                int userCredits = user.getInteger("credit");


                if (userCredits<=0){
                    Toast.makeText(requireContext(), "Insufficient Credits", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (bookCount<=0){
                    Toast.makeText(requireContext(), "Book Unavailable", Toast.LENGTH_SHORT).show();
                    return false;
                }

                String recordID = ""+System.currentTimeMillis();

                Document document = new Document("_id",recordID)
                        .append("bookID",bookID)
                        .append("enroll",enroll)
                        .append("name",book.getString("name"))
                        .append("author",book.getString("author"))
                        .append("url",book.getString("url"))
                        .append("issueDate",issued)
                        .append("returnDate","")
                        .append("expectedReturnDate",expected);

                recordCollection.insertOne(document);

                Bson filter = Filters.eq("_id",bookID);
                Bson update = Updates.set("available",bookCount-1);
                Bson update1 = Updates.set("totalissues",totalIssues+1);
                booksCollection.updateOne(filter, update);
                booksCollection.updateOne(filter,update1);

                Bson filter2 = Filters.eq("_id",enroll);
                Bson update3 = Updates.set("credit",userCredits-1);
                collection.updateOne(filter2,update3);

                String email = user.getString("email");
                String bookName = book.getString("name");

                String url = "https://librarymanagement.yagnikpatel.repl.co/?email="+email+"&record="+recordID+"&key=my-key&book="+bookName+"&issue="+issued+"&due="+expected+"&enroll="+enroll;
                send_message(url);

                return true;
            }catch (Exception e){
                Log.d("myDebug", "generateTransaction: "+e);
                return false;
            }
        }
    }

    public void send_message(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

}