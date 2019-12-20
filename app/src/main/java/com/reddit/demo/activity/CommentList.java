package com.reddit.demo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.reddit.demo.R;
import com.reddit.demo.adapter.CommentRecyclerViewAdapter;
import com.reddit.demo.model.Comment;
import com.reddit.demo.utils.Config;
import com.reddit.demo.utils.NetworkHandler;
import com.reddit.demo.utils.RecyclerViewEndLessScroll;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.reddit.demo.utils.Config.TAG;

public class CommentList extends BaseActivity {
    String url, after = "null";
    ArrayList<Comment> list = new ArrayList<>();
    CommentRecyclerViewAdapter adapter;
    ProgressRelativeLayout progressRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        setUpToolbar("Comments");
        url = getIntent().getStringExtra("url")+".json?limit="+ Config.DATA_LIMIT(this);
        final Uri myUri = Uri.parse(getIntent().getStringExtra("url")+".json");
        url = new Uri.Builder().scheme(Config.SITE_PROTOCOL)
                .authority(Config.SITE_DOMAIN)
                .path(myUri.getPath())
                .appendQueryParameter("limit", Config.DATA_LIMIT(this))
                .appendQueryParameter("geo_filter", Config.GEO_LIMIT(this))
                .build().toString();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        progressRelativeLayout = findViewById(R.id.progressRelativeLayout);
        progressRelativeLayout.showLoading();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerViewEndLessScroll(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(!after.equals("null")) {
                    url = new Uri.Builder().scheme(Config.SITE_PROTOCOL)
                            .authority(Config.SITE_DOMAIN)
                            .path(myUri.getPath()+ ".json")
                            .appendQueryParameter("limit", Config.DATA_LIMIT(CommentList.this))
                            .appendQueryParameter("after", after)
                            .appendQueryParameter("geo_filter", Config.GEO_LIMIT(CommentList.this))
                            .build().toString();
                    getComments();
                }
            }
        });
        adapter = new CommentRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
        getComments();
    }

    private void getComments() {
        new NetworkHandler(this, url, new NetworkHandler.TaskListener() {
            @Override
            public void onFinished(JSONObject result) {
                Toast.makeText(CommentList.this, "Loaded Comments", Toast.LENGTH_SHORT).show();
                JSONArray jsonArray = result.optJSONArray("jsonArray").optJSONObject(1).optJSONObject("data").optJSONArray("children");
                for(int i = 0; i < jsonArray.length()-1; i++) {
                    JSONObject item = jsonArray.optJSONObject(i).optJSONObject("data");
                    Log.e(TAG, "onFinished: "+item.optString("body"));
                    list.add(Comment.CommentParser(item));
                    adapter.notifyItemInserted(list.size()-1);
                    if(progressRelativeLayout.isLoadingCurrentState())
                        progressRelativeLayout.showContent();
                }
                after = result.optJSONArray("jsonArray").optJSONObject(1).optJSONObject("data").optString("after");
            }

            @Override
            public void onFailure(VolleyError volleyError) {
                Log.e(TAG, "onFailure: "+volleyError );
                progressRelativeLayout.showEmpty(R.drawable.vector_searching,"Nothing Found", "");
            }
        }).execute();
    }
}
