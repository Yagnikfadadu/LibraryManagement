package com.yagnikfadadu.librarymanagement.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yagnikfadadu.librarymanagement.ModalClass.WishlistModal;
import com.yagnikfadadu.librarymanagement.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    Context context;
    ArrayList<WishlistModal> wishlistModalList;
    public WishlistAdapter(Context context, ArrayList<WishlistModal> wishlistModalList){
        this.context = context;
        this.wishlistModalList = wishlistModalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishlistModal wishlistModal= wishlistModalList.get(position);
        String name = wishlistModal.getWishListBookName();
        holder.title.setText(name);
        if (name.length()<=36){
            String[] sub = name.split(" ");
            String edit = "";
            for (int i=0;i<=sub.length-2;i++){
                edit+= sub[i]+" ";
            }
            edit += "\n"+sub[sub.length-1];
            holder.title.setText(edit);
        }
        holder.author.setText(wishlistModal.getWishListAuthorName());
        Bitmap bmp = null;
        try {
            URL url = new URL(wishlistModal.getWishListurl());
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.bookImage.setImageBitmap(bmp);
        if(wishlistModal.getAvailable()<=0)
        {
            holder.available.setText("This book is currently unavailable");
            holder.available.setTextColor(Color.parseColor("#d80b0b"));
        }
        else {
            holder.available.setText("This book is Available Now");
            holder.available.setTextColor(Color.parseColor("#69b00b"));
        }

    }

    @Override
    public int getItemCount() {
        return wishlistModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView bookImage;
        TextView author;
        TextView title;
        TextView available;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.wishlist_book_image);
            author = itemView.findViewById(R.id.wishlist_book_author);
            title = itemView.findViewById(R.id.wishlist_book_title);
            available = itemView.findViewById(R.id.wishlist_availability);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}