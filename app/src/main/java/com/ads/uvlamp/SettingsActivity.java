package com.ads.uvlamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            UserSettings userSettings = new UserSettings(this.getContext());

            EditTextPreference ip_address = findPreference("ip_address");
            EditTextPreference mac_address = findPreference("mac_addres");
            EditTextPreference device_id = findPreference("device_id");
            EditTextPreference firmware_version = findPreference("firmware");
            EditTextPreference current_ssid = findPreference("ssid_network");

            ip_address.setSummary(userSettings.getEspDevice().getDeviceIp());
            mac_address.setSummary(userSettings.getEspDevice().getDeviceMac());
            device_id.setSummary(userSettings.getEspDevice().getDeviceId());
            firmware_version.setSummary(userSettings.getEspDevice().getCurrentFirmware());
            current_ssid.setSummary(userSettings.getEspDevice().getCurrentSsid());
        }
    }
}