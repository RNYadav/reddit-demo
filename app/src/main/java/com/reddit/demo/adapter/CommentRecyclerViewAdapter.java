package com.reddit.demo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reddit.demo.R;
import com.reddit.demo.model.Comment;
import com.reddit.demo.utils.CommonTools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.reddit.demo.utils.Config.TAG;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentRowHolder> {
    private List<Comment> list;
    private Context context;

    public CommentRecyclerViewAdapter(List<Comment> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CommentRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new CommentRowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentRowHolder holder, int position) {
        final Comment item = list.get(position);
        holder.name.setText(item.getName());
        holder.details.setText(CommonTools.prettyTime(item.getTime())+" - "+CommonTools.prettyNumber(item.getPoints())+" Points");
        holder.title.setText(item.getText());
        try{
            final JSONArray repliesArray = item.getReplies().optJSONObject("data").optJSONArray("children");
            if(repliesArray!=null) {
                holder.comment.setVisibility(View.VISIBLE);
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo: implement inline comments
                        List<Comment> replies = new ArrayList<>();
                        CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(replies);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                        holder.recyclerView.setLayoutManager(layoutManager);
                        holder.recyclerView.setAdapter(adapter);
                        for (int i = 0; i < repliesArray.length() - 1; i++) {
                            replies.add(Comment.CommentParser(repliesArray.optJSONObject(i).optJSONObject("data")));
                            adapter.notifyItemInserted(replies.size() - 1);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onClick: ", e);
        }
        Picasso.get().load(CommonTools.userAvatarlink(item.getImage())).placeholder(R.drawable.placeholder_sqr).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        RecyclerView recyclerView;
        TextView title, name, details, comment;
        public CommentRowHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.name);
            this.title = itemView.findViewById(R.id.title);
            this.details = itemView.findViewById(R.id.details);
            this.recyclerView = itemView.findViewById(R.id.recyclerView);
            this.comment = itemView.findViewById(R.id.comment);
        }
    }
}
