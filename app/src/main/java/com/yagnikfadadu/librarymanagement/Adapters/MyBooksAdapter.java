package com.yagnikfadadu.librarymanagement.Adapters;

import android.content.Context;
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

import com.google.android.material.snackbar.Snackbar;
import com.yagnikfadadu.librarymanagement.ModalClass.RecordModal;
import com.yagnikfadadu.librarymanagement.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.ViewHolder> {
    Context context;
    ArrayList<RecordModal> recordModalModalList;

    public MyBooksAdapter(Context context, ArrayList<RecordModal> recordModalList){
        this.context = context;
        this.recordModalModalList = recordModalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_books,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordModal recordModal = recordModalModalList.get(position);
        String name = recordModal.getBookName();
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
        holder.author.setText(recordModal.getBookAuthor());
        Bitmap bmp = null;
        try {
            URL url = new URL(recordModal.getUrl());
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.bookImage.setImageBitmap(bmp);
        holder.issueDate.setText("Issue date: "+recordModal.getIssueDate());
        if(recordModal.getReturnDate().isEmpty()){
            holder.returnDate.setText("Due Date: "+recordModal.getExpectedReturnDate());
        }
        else {
            holder.returnDate.setText("Returned On:"+recordModal.getReturnDate());
        }

    }

    @Override
    public int getItemCount() {
        return recordModalModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView bookImage;
        ImageView ratingImage;
        TextView author;
        TextView title;
        TextView issueDate,returnDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.my_book_image);
            author = itemView.findViewById(R.id.my_book_author);
            title = itemView.findViewById(R.id.my_book_title);
            issueDate=itemView.findViewById(R.id.issue_date);
            returnDate=itemView.findViewById(R.id.return_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(view, "You have issued this book", Snackbar.LENGTH_SHORT).show();
        }
    }
}