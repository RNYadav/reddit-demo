package com.reddit.demo;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.reddit.demo.activity.BaseActivity;
import com.reddit.demo.activity.Favorite;
import com.reddit.demo.activity.SettingsActivity;
import com.reddit.demo.adapter.ViewPagerFragmentAdapter;
import com.reddit.demo.fragment.PostList;

import java.util.ArrayList;
import java.util.List;

public class Home extends BaseActivity  implements SearchView.OnQueryTextListener{
    SearchView searchView;
    MenuItem item;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dummy = getIntent().getStringExtra("name")!=null?getIntent().getStringExtra("name"):"popular";
        setUpToolbar(dummy=="popular"?"Reddit Demo":dummy);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        setupView();
        findViewById(R.id.fav).setVisibility(View.VISIBLE);
        findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Favorite.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(dummy!="popular")
            return false;
        getMenuInflater().inflate(R.menu.search, menu);
        item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            searchView.setMaxWidth(Integer.MAX_VALUE);
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            assert searchManager != null;
            searchView.setOnQueryTextListener(this);
            searchView.setQuery(dummy, false);
        }
        if(getIntent().getStringExtra("name")!=null) {
            item.expandActionView();
            searchView.setQuery(dummy, true);
        }
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                if(!dummy.equals("popular")) {
                    dummy = "popular";
                    item.collapseActionView();
                    setupView();
                }
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true; // Return true to expand action view
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                item.collapseActionView();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void setupView() {
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

    @Override
    public boolean onQueryTextSubmit(String queryText) {
        if(queryText == null || queryText.isEmpty())
            return false;
        dummy = queryText.trim();
        searchView.clearFocus();
        //progressRelativeLayout.showLoading();
        setupView();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
