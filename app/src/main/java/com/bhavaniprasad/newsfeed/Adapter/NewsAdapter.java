package com.bhavaniprasad.newsfeed.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bhavaniprasad.newsfeed.ContentActivity;
import com.bhavaniprasad.newsfeed.R;
import com.bhavaniprasad.newsfeed.model.NewsFeedData;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.Customview> {
    private ArrayList<NewsFeedData> arrList;
    private Context cnt;
    private LayoutInflater layoutInflater;
    SimpleDateFormat sdf;

    public NewsAdapter(Context context, ArrayList<NewsFeedData> userViewModels) {
        this.arrList=userViewModels;
        this.cnt=context;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    @NonNull
    @Override
    public NewsAdapter.Customview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = layoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        return new Customview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Customview holder, int position) {
        final Customview customview=(Customview) holder;
        customview.author.setText("Author: "+ arrList.get(position).getAuthor());
        customview.title.setText("Title: "+arrList.get(position).getTitle());
        customview.description.setText("Description: "+arrList.get(position).getDescription());
        customview.content.setText("Content: "+arrList.get(position).getContent());

        SpannableString spannableString = new SpannableString(arrList.get(position).getUrl());
        spannableString.setSpan(new UnderlineSpan(), 0, arrList.get(position).getUrl().length(), 0);
        customview.url.setText(spannableString);

        customview.url.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent browseintent = new Intent(cnt, ContentActivity.class);
                browseintent.putExtra("url",arrList.get(position).getUrl());

                cnt.startActivity(browseintent);
            }
        });
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            customview.publishedAt.setText("Published at: "+sdf.parse(arrList.get(position).getPublishedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        customview.name.setText("Name: "+arrList.get(position).getName());
        Picasso.with(cnt).load(arrList.get(position).getImageurl()).into(customview.imageView);


    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class Customview extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView author,url,title,content,description,publishedAt,name;
        ImageView imageView;
        public Customview(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            url = itemView.findViewById(R.id.url);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            content = itemView.findViewById(R.id.content);
            publishedAt = itemView.findViewById(R.id.publishedAt);
            name=itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.circleImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
