package com.reddit.demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.reddit.demo.R;
import com.reddit.demo.database.AppDatabase;
import com.reddit.demo.database.AppExecutors;
import com.reddit.demo.utils.CommonTools;

public class BaseActivity extends AppCompatActivity {
   public String dummy = "test";
   AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(CommonTools.getAppTheme(this));
        db = AppDatabase.getInstance(this, new AppExecutors());
        super.onCreate(savedInstanceState);
    }

    public void setUpToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                if(!title.equals("Reddit Demo"))
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return false;
    }
}
