package com.yagnikfadadu.librarymanagement.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yagnikfadadu.librarymanagement.Adapters.MyBooksAdapter;
import com.yagnikfadadu.librarymanagement.ModalClass.RecordModal;
import com.yagnikfadadu.librarymanagement.R;

import org.bson.Document;

import java.util.ArrayList;


public class MyBooksFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    MyBooksAdapter myBooksAdapter;
    ArrayList<RecordModal> recordModalArrayList;
    ImageView coverPhoto;
    TextView bookName, authorName, issueDate, returnDate;
    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase bookDatabase = mongoClient.getDatabase("books");
    MongoCollection<Document> booksCollection = bookDatabase.getCollection("books");

    MongoDatabase recordDatabase = mongoClient.getDatabase("records");
    MongoCollection<Document> recordCollection = recordDatabase.getCollection("records");

    public MyBooksFragment() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mybooks, container, false);

        recordModalArrayList = new ArrayList<>();

        myBooksAdapter = new MyBooksAdapter(getContext(), recordModalArrayList);

        recyclerView = view.findViewById(R.id.mybooks_recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        recyclerView.setAdapter(myBooksAdapter);

        getMyBooksList();

        return view;

    }
    public void getMyBooksList() {;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared",MODE_PRIVATE);
        String enroll = sharedPreferences.getString("enroll","");

        MongoCursor<Document> cursor = recordCollection.find(Filters.eq("enroll", enroll)).iterator();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            RecordModal recordModal = new RecordModal();
            recordModal.setBookName(document.getString("name"));
            recordModal.setId(document.getString("_id"));
            recordModal.setUrl(document.getString("url"));
            recordModal.setBookAuthor(document.getString("author"));
            recordModal.setIssueDate(document.getString("issueDate"));
            recordModal.setReturnDate(document.getString("returnDate"));
            recordModal.setExpectedReturnDate(document.getString("expectedReturnDate"));
            recordModalArrayList.add(recordModal);
            myBooksAdapter.notifyDataSetChanged();
        }
    }
}