package com.reddit.demo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.reddit.demo.R;
import com.reddit.demo.adapter.ViewPagerFragmentAdapter;
import com.reddit.demo.fragment.PostList;

import java.util.ArrayList;
import java.util.List;

public class Profile extends BaseActivity {
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dummy = getIntent().getStringExtra("name");
        setUpToolbar(dummy);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), this.getLifecycle());
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);
        list.clear();
        list.add("new");
        list.add("hot");
        list.add("rising");
        list.add("top");
        list.add("controversial");
        for(String s: list) {
            Bundle bundle = new Bundle();
            PostList all = new PostList();
            bundle.putString("mode",s);
            all.setArguments(bundle);
            adapter.addFragment(all);
        }

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(list.get(position));
            }
        }).attach();
    }
}
