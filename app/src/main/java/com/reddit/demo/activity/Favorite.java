package com.reddit.demo.activity;

import android.os.Bundle;

import com.reddit.demo.R;
import com.reddit.demo.fragment.PostList;

public class Favorite extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setUpToolbar("Favorite");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new PostList())
                .commit();
    }
}
