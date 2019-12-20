package com.reddit.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reddit.demo.R;
import com.reddit.demo.activity.CommentList;
import com.reddit.demo.activity.Profile;
import com.reddit.demo.database.AppDatabase;
import com.reddit.demo.database.AppExecutors;
import com.reddit.demo.model.Post;
import com.reddit.demo.utils.CommonTools;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostRecyclerViewAdapter  extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostRowHolder> {
    private List<Post> list;
    private Context context;

    public PostRecyclerViewAdapter(List<Post> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PostRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new PostRowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PostRowHolder holder, int position) {
        final Post item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getCategory());
        holder.details.setText(item.getName() +" • "+CommonTools.prettyTime(item.getTime()));
        holder.comment.setText(" "+CommonTools.prettyNumber(item.getComments())+" Comments");
        holder.like.setText(CommonTools.prettyNumber(item.getVotes()));
        holder.fav.setImageResource(item.isfav()?R.drawable.ic_favorited:R.drawable.ic_favorite);
        if(item.getContent()!=null)
        switch (item.getPostType()){
            case "image":
                holder.content.setVisibility(View.VISIBLE);
                Picasso.get().load(item.getContent()).placeholder(R.drawable.placeholder_sqr).into(holder.content, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        holder.content.setVisibility(View.GONE);
                    }
                });
                break;
            case "link":
                holder.contentPanel.setVisibility(View.VISIBLE);
                holder.contentPanel.setText(item.getUrl());
                holder.contentPanel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(item.getUrl()));
                        context.startActivity(i);
                    }
                });
                break;
        }


        /** Click Listener **/
        //share Intent
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle()+"\nCheck this out at - \n"+item.getUrl());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        });
        //Comment Intent
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentList.class);
                intent.putExtra("url", item.getUrl());
                context.startActivity(intent);
            }
        });
        //Profile Intent
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileIntent(item.getCategory());
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileIntent(item.getName());
            }
        });
        //like dislike error
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Demo App!, Responses are prohibited.", Toast.LENGTH_SHORT).show();
            }
        });
        //Fav Row
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getInstance(context, new AppExecutors());
                        if(item.isfav()){
                            item.setIsfav(false);
                            db.postDao().deletePost(item);
                        }else {
                            item.setIsfav(true);
                            db.postDao().insertPost(item);
                        }
                        holder.fav.setImageResource(item.isfav()?R.drawable.ic_favorited:R.drawable.ic_favorite);
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void profileIntent(String name){
        Intent intent = new Intent(context, Profile.class);
        if(name.contains("r/")){
            String[] split = name.split("/");
            name = split[1];
        }
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    public class PostRowHolder extends RecyclerView.ViewHolder {
        TextView title, name, like, comment, share, details, contentPanel;
        ImageView fav, content;
        public PostRowHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.name = itemView.findViewById(R.id.name);
            this.details = itemView.findViewById(R.id.details);
            this.like = itemView.findViewById(R.id.likecount);
            this.comment = itemView.findViewById(R.id.comment);
            this.share = itemView.findViewById(R.id.share);
            this.fav = itemView.findViewById(R.id.fav);
            this.content = itemView.findViewById(R.id.thumb);
            this.contentPanel = itemView.findViewById(R.id.contentPanel);
        }
    }
}
