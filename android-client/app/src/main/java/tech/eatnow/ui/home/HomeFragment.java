package tech.eatnow.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tech.eatnow.MainActivity;
import tech.eatnow.R;
import tech.eatnow.SettingsActivity;

import static tech.eatnow.SettingsActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private MapView mapView;
    private GoogleMap map;

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View window;
        //private final View contents;

        public CustomInfoWindowAdapter() {
            this.window = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            //this.contents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, window);
            return window;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, window);
            return window;
        }

        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = view.findViewById(R.id.marker_provider_name);
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = view.findViewById(R.id.marker_food_names);
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView foodsView = root.findViewById(R.id.foods_recycler_view);
        if (foodsView != null) {
            foodsView.setAdapter(((MainActivity) getActivity()).foodAdapter);
            foodsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        mapView = root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

                if (prefs.getBoolean("use_current_location", false)) {
                    if (SettingsActivity.hasLocationPermission(getContext())){
                        onLocationEnable(getContext());
                    } else {
                        onLocationDisable(getContext());
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    }
                } else {
                    onLocationDisable(getContext());
                }

                prefs.registerOnSharedPreferenceChangeListener(HomeFragment.this);

                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(
                    "use_current_location", grantResults[0] == PackageManager.PERMISSION_GRANTED)
            .apply();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("use_current_location")) {
            if (sharedPreferences.getBoolean("use_current_location", false))
                onLocationEnable(HomeFragment.this.getContext());
            else
                onLocationDisable(HomeFragment.this.getContext());
        }
    }

    public void onLocationEnable(Context context) {
        if (map != null) {
            map.setMyLocationEnabled(true);
            LocationServices.getFusedLocationProviderClient(context).getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location currentLocation = task.getResult();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                    currentLocation.getLatitude(),
                                    currentLocation.getLongitude()),
                                    15.0f
                            ));
                        }
                    });
        }
    }

    public void onLocationDisable(Context context) {
        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null)
            mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null)
            mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }
}