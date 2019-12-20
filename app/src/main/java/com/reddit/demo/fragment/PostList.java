package com.reddit.demo.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.reddit.demo.R;
import com.reddit.demo.activity.BaseActivity;
import com.reddit.demo.adapter.PostRecyclerViewAdapter;
import com.reddit.demo.database.AppDatabase;
import com.reddit.demo.database.AppExecutors;
import com.reddit.demo.model.Post;
import com.reddit.demo.utils.Config;
import com.reddit.demo.utils.NetworkHandler;
import com.reddit.demo.utils.RecyclerViewEndLessScroll;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.reddit.demo.utils.Config.TAG;

public class PostList extends Fragment {
    private ArrayList<Post> list = new ArrayList<>();
    private PostRecyclerViewAdapter adapter;
    private String url, after="";
    private ProgressRelativeLayout progressRelativeLayout;

    public PostList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        progressRelativeLayout = view.findViewById(R.id.progressRelativeLayout);
        progressRelativeLayout.showLoading();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new PostRecyclerViewAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if(getArguments()==null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getInstance(getContext(), new AppExecutors());
                    List saved = db.postDao().getAll();
                    if(!saved.isEmpty()) {
                        for(int i=0; i<saved.size(); i++) {
                            list.add((Post) saved.get(i));
                            adapter.notifyItemInserted(list.size()-1);
                        }
                        if(progressRelativeLayout.isLoadingCurrentState())
                            progressRelativeLayout.showContent();
                    }else
                        progressRelativeLayout.showEmpty(R.drawable.vector_fav, "NO FAV FOUND", "Explore the articles and save them for reading them later");
                }
            }).start();
        }
        else {
            recyclerView.addOnScrollListener(new RecyclerViewEndLessScroll(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if(after!=null) {
                        url = new Uri.Builder().scheme(Config.SITE_PROTOCOL)
                                .authority(Config.SITE_DOMAIN)
                                .path("/r/" + ((BaseActivity) getActivity()).dummy + "/" + getArguments().getString("mode") + ".json")
                                .appendQueryParameter("limit", String.valueOf(Config.DATA_LIMIT(getContext())))
                                .appendQueryParameter("after", after)
                                .appendQueryParameter("geo_filter", Config.GEO_LIMIT(getContext()))
                                .build().toString();
                        getPosts();
                    }
                }
            });
            //setup Initial url
            url = new Uri.Builder().scheme(Config.SITE_PROTOCOL)
                    .authority(Config.SITE_DOMAIN)
                    .path("/r/" + ((BaseActivity) getActivity()).dummy + "/" + getArguments().getString("mode") + ".json")
                    .appendQueryParameter("limit", Config.DATA_LIMIT(getContext()))
                    .appendQueryParameter("geo_filter", Config.GEO_LIMIT(getContext()))
                    .build().toString();
            getPosts();
        }
        return view;
    }

    private void getPosts(){
        new NetworkHandler(getContext(), url, new NetworkHandler.TaskListener() {
            @Override
            public void onFinished(JSONObject result) {
                JSONArray jsonArray = result.optJSONObject("data").optJSONArray("children");
                after = result.optJSONObject("data").optString("after");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject item = jsonArray.optJSONObject(i).optJSONObject("data");
                    list.add(new Post(item.optString("name"), item.optString("post_hint"), item.optString("author"), item.optString(""),
                            item.optString("subreddit_name_prefixed"),
                            item.optString("title"), item.optString("url"), Config.SITE_PROTOCOL+"://"+Config.SITE_DOMAIN+item.optString("permalink"), item.optInt("score"), item.optInt("num_comments"), item.optInt("created_utc"),false));
                    Log.e(TAG, "onFinished: " + item);
                    adapter.notifyItemChanged(list.size()-1);
                }
                if(progressRelativeLayout.isLoadingCurrentState()) {
                    if(list.size()>0)
                        progressRelativeLayout.showContent();
                    else
                        progressRelativeLayout.showEmpty(R.drawable.vector_searching, "Nothing Found", "");
                }

            }

            @Override
            public void onFailure(VolleyError volleyError) {

            }
        }).execute();
    }

}
