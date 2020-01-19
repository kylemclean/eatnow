package tech.eatnow;

import android.content.Intent;
import android.icu.text.MeasureFormat;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    public FirestoreRecyclerAdapter foodAdapter;

    private HashMap<Provider, Marker> markers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFoods();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initFirebase();
        updateFoods();
    }

    private void initFirebase() {
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void updateFoods() {
        final CollectionReference foods = firestore.collection("foods");
        Query query = foods.limit(100);

        markers.clear();

        FirestoreRecyclerOptions<Food> options = new FirestoreRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();
        foodAdapter = new FirestoreRecyclerAdapter<Food, FoodHolder>(options) {
            @Override
            public void onBindViewHolder(final FoodHolder holder, int position, final Food food) {
                // TODO
                ((TextView) holder.itemView.findViewById(R.id.food_name)).setText(food.name);
                storage.getReferenceFromUrl(food.photo).getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                ImageView photoView = holder.itemView.findViewById(R.id.food_photo);
                                Glide.with(photoView.getContext())
                                        .load(task.getResult())
                                        .into(photoView);
                            }
                        }
                );

                ((TextView) holder.itemView.findViewById(R.id.food_description)).setText(food.description);

                holder.itemView.findViewById(R.id.food_card_view).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("You did it", "You clicked on a thing");
                            }
                        }
                );

                food.provider.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            return;

                        final Provider provider = task.getResult().toObject(Provider.class);

                        if (provider != null) {
                            ((TextView)holder.itemView.findViewById(R.id.provider_name)).setText(provider.name);


                            ((MapView) findViewById(R.id.map_view)).getMapAsync(
                                    new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            if (!markers.containsKey(provider)) {
                                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(provider.location.getLatitude(),
                                                                provider.location.getLongitude()))
                                                        .title(provider.name)
                                                        .snippet(food.name)
                                                );
                                                markers.put(provider, marker);
                                            } else {
                                                markers.get(provider).setSnippet(
                                                        markers.get(provider).getSnippet() + "\n" + food.name
                                                );
                                            }

                                        }
                                    }
                            );

                            final TextView distanceToFood = findViewById(R.id.distance_to_food);
                            if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                                    .getBoolean("use_current_location", false)) {
                                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                        .getLastLocation().addOnCompleteListener(
                                        new OnCompleteListener<Location>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Location> task) {
                                                Location foodLocation = new Location("jeff");
                                                foodLocation.setLatitude(provider.location.getLatitude());
                                                foodLocation.setLongitude(provider.location.getLongitude());

                                                // TODO fix
                                                distanceToFood.setText(task.getResult().distanceTo(foodLocation) + " m");
                                            }
                                        }
                                );
                            } else {
                                distanceToFood.setVisibility(View.GONE);
                            }

                            holder.itemView.findViewById(R.id.get_directions_button).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                                    "geo:" + provider.location + "?q=" +
                                                            provider.name.replace(' ', '+')
                                            ));
                                            startActivity(intent);
                                        }
                                    }
                            );
                        }
                    }
                });
            }


            @Override
            public FoodHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.food_item, group, false);

                return new FoodHolder(view);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        foodAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        foodAdapter.stopListening();
    }
}
