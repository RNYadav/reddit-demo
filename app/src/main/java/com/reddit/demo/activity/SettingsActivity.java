package com.reddit.demo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

import androidx.preference.PreferenceFragmentCompat;

import com.reddit.demo.R;

public class SettingsActivity extends BaseActivity implements Preference.OnPreferenceClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setUpToolbar("Settings");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onDestroy() {
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            restartActivity();
        }

        private void restartActivity() {
            Intent intent = getActivity().getIntent();
            getActivity().overridePendingTransition(0, 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        }
    }

}