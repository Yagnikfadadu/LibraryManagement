package com.yagnikfadadu.librarymanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yagnikfadadu.librarymanagement.BookActivity;
import com.yagnikfadadu.librarymanagement.ModalClass.BookModal;
import com.yagnikfadadu.librarymanagement.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookSearchAdapter extends RecyclerView.Adapter<BookSearchAdapter.ViewHolder> {
    Context context;
    ArrayList<BookModal> bookModalList;
    public BookSearchAdapter(Context context,ArrayList<BookModal> bookModalList){
        this.context = context;
        this.bookModalList = bookModalList;
    }

    @NonNull
    @Override
    public BookSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookSearchAdapter.ViewHolder holder, int position) {
        BookModal bookModal = bookModalList.get(position);

        String name = bookModal.getName();
        holder.title.setText(name);
        if (name.length()<=36){
            String[] sub = name.split(" ");
            String edit = "";
            for (int i=0;i<=sub.length-2;i++){
                edit+= sub[i]+" ";
            }
            edit += "\n"+sub[sub.length-1];
            Log.d("books",edit);
            holder.title.setText(edit);
        }
        holder.author.setText(bookModal.getAuthor());
        Bitmap bmp = null;
        try {
            URL url = new URL(bookModal.getCoverPhoto());
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.bookImage.setImageBitmap(bmp);

        int bookRating = (int) bookModal.getRating();
        Log.d("books",""+bookRating);
        switch (bookRating){
            case 0:
                holder.ratingImage.setImageResource(R.drawable.zero_star);
                break;
            case 1:
                holder.ratingImage.setImageResource(R.drawable.one_star);
                break;
            case 2:
                holder.ratingImage.setImageResource(R.drawable.two_star);
                break;
            case 3:
                holder.ratingImage.setImageResource(R.drawable.three_star);
                break;
            case 4:
                holder.ratingImage.setImageResource(R.drawable.four_star);
                break;
            default:
                holder.ratingImage.setImageResource(R.drawable.five_star);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView bookImage;
        ImageView ratingImage;
        TextView author;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.list_book_image);
            ratingImage = itemView.findViewById(R.id.list_book_ratings);
            author = itemView.findViewById(R.id.list_book_author);
            title = itemView.findViewById(R.id.list_book_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            Intent intent = new Intent(context, BookActivity.class);
            intent.putExtra("id",bookModalList.get(position).getId());
            intent.putExtra("url",bookModalList.get(position).getCoverPhoto());
            intent.putExtra("name",bookModalList.get(position).getName());
            intent.putExtra("rating",bookModalList.get(position).getRating());
            intent.putExtra("author",bookModalList.get(position).getAuthor());
            context.startActivity(intent);
        }
    }
}
