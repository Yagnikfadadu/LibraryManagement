package com.yagnikfadadu.librarymanagement.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.yagnikfadadu.librarymanagement.Adapters.BookSearchAdapter;
import com.yagnikfadadu.librarymanagement.ModalClass.BookModal;
import com.yagnikfadadu.librarymanagement.R;

import org.bson.Document;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    BookSearchAdapter bookSearchAdapter;
    ArrayList<BookModal> bookModalArrayList;
    TextInputEditText searchEditText;
    LinearProgressIndicator linearProgressIndicator;
    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase database = mongoClient.getDatabase("books");
    MongoCollection<Document> collection = database.getCollection("books");

    public SearchFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d("books",""+ System.currentTimeMillis());
        bookModalArrayList = new ArrayList<>();

        bookSearchAdapter = new BookSearchAdapter(getContext(),bookModalArrayList);


        recyclerView = view.findViewById(R.id.search_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        linearProgressIndicator = view.findViewById(R.id.linear_progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookSearchAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d("myDebug",searchEditText.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initializeBookList();
            }
        }.start();

        return view;
    }

    public void initializeBookList(){
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {

            Document document = cursor.next();
            BookModal bookModal = new BookModal();

            bookModal.setAuthor(document.getString("author"));
            bookModal.setCoverPhoto(document.getString("url"));
            bookModal.setName(document.getString("name"));
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

    private class MyTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            initializeBookList();
            return null;
        }
    }


}