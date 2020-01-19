package tech.eatnow.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tech.eatnow.MainActivity;
import tech.eatnow.R;
import tech.eatnow.SettingsActivity;

import static tech.eatnow.SettingsActivity.PERMISSION_REQUEST_ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment {

    private MapView mapView;
    private GoogleMap map;


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

                map.addMarker(new MarkerOptions()
                        .position(new LatLng(0.0, 0.0))
                        .title("Jeff"));

                if (SettingsActivity.hasLocationPermission(getContext())) {
                    onLocationEnable();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                }
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

    public void onLocationEnable() {
        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    public void onLocationDisable() {
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