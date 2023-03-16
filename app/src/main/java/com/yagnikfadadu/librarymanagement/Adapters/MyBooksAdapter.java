package com.yagnikfadadu.librarymanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.yagnikfadadu.librarymanagement.ReturnBookActivity;

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
            holder.returnDate.setTextColor(Color.parseColor("#ffb800"));
            holder.returnDate.setText("Due Date: "+recordModal.getExpectedReturnDate());
        }
        else {
            holder.returnDate.setTextColor(Color.parseColor("#00ce16"));
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
            int position = this.getAdapterPosition();
            Intent intent = new Intent(context, ReturnBookActivity.class);
            if (recordModalModalList.get(position).getReturnDate().isEmpty()) {
                intent.putExtra("name", recordModalModalList.get(position).getBookName());
                intent.putExtra("author", recordModalModalList.get(position).getBookAuthor());
                intent.putExtra("issueDate", recordModalModalList.get(position).getIssueDate());
                intent.putExtra("url", recordModalModalList.get(position).getUrl());
                intent.putExtra("id", recordModalModalList.get(position).getId());
                context.startActivity(intent);
            }else {
                Snackbar.make(title,"Book Already Returned",Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}