package tech.eatnow;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    public interface LocationRequestResponseListener {
        void onLocationRequestResponse(boolean granted);
    }

    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 0;
    private LocationRequestResponseListener locationRequestResponseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0)
                getPreferenceManager().getSharedPreferences().edit().putBoolean(
                        "use_current_location", grantResults[0] == PackageManager.PERMISSION_GRANTED)
            .apply();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            getPreferenceManager().findPreference("use_current_location")
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(
                                final Preference preference, final Object newValue) {

                            if ((boolean) newValue) {
                                // Setting current location to on

                                if (SettingsActivity.hasLocationPermission(getContext()))
                                    // Accept change if location permission is granted
                                    return true;

                                // If location permission has not been granted, request it
                                // asynchronously and deny the change for now
                                /*start*/
                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        SettingsFragment.this.getActivity(),
                                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    // Show an explanation to the user *asynchronously* -- don't block
                                    // this thread waiting for the user's response! After the user
                                    // sees the explanation, try again to request the permission.
                                    new AlertDialog.Builder(SettingsFragment.this.getContext())
                                            .setMessage(R.string.location_permission_request_text)
                                            .setTitle(R.string.location_permission_request_title)
                                            .setNeutralButton(android.R.string.ok, null)
                                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    requestPermissions(
                                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                                                }
                                            })
                                            .show();
                                    return false;
                                } else {
                                    // No explanation needed; request the permission
                                    requestPermissions(
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                                    return false;
                                }
                            }
                            return true;
                        }
                    });
        }
    }

    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}