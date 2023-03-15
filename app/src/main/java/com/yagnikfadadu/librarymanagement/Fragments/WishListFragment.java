package com.yagnikfadadu.librarymanagement.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yagnikfadadu.librarymanagement.Adapters.WishlistAdapter;
import com.yagnikfadadu.librarymanagement.ModalClass.WishlistModal;
import com.yagnikfadadu.librarymanagement.R;

import org.bson.Document;

import java.util.ArrayList;


public class WishListFragment extends Fragment {
    RecyclerView recyclerView;
    WishlistAdapter wishlistAdapter;
    ArrayList<WishlistModal> wishListArrayList;

    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase bookDatabase = mongoClient.getDatabase("books");
    MongoCollection<Document> booksCollection = bookDatabase.getCollection("books");

    MongoDatabase wishListDatabase = mongoClient.getDatabase("wishlists");
    MongoCollection<Document> wishListCollection = wishListDatabase.getCollection("wishlists");



    public WishListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        wishListArrayList = new ArrayList<>();

        wishlistAdapter = new WishlistAdapter(getContext(), wishListArrayList);

        recyclerView = view.findViewById(R.id.wishlist_recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               getMyWishlist();
            }
        }.start();

        recyclerView.setAdapter(wishlistAdapter);
        return view;
    }

    public void getMyWishlist() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared", MODE_PRIVATE);
        String enroll = sharedPreferences.getString("enroll", "");

        MongoCursor<Document> cursor = wishListCollection.find(Filters.eq("enrollment", enroll)).iterator();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            WishlistModal wishlistModal = new WishlistModal();
            wishlistModal.setWishListBookName(document.getString("book_name"));
            wishlistModal.setWishListurl(document.getString("url"));
            wishlistModal.setWishListAuthorName(document.getString("author"));
            wishlistModal.setWishListBookID(document.getString("bookID"));
            MongoCursor<Document> cursor1 = booksCollection.find(Filters.eq("_id",wishlistModal.getWishListBookID())).iterator();
            while(cursor1.hasNext()){
                Document document1 = cursor1.next();
                int i = document1.getInteger("available");
                wishlistModal.setAvailable(i);
            }

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wishListArrayList.add(wishlistModal);
                    wishlistAdapter.notifyDataSetChanged();
                }
            });

        }

    }
}