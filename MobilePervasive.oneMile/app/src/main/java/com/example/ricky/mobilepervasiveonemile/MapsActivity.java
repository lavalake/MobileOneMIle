package com.example.ricky.mobilepervasiveonemile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.ricky.mobilepervasiveonemile.PostNewActivity;



import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MapView mapView;
    private Location location_current;
    double l_1 = 0;
    double l_2 = 0;

    //set the default number of words for small display
    int len = 20;

    //For same latitude and longitude adjustment
    int max=20;
    Random random = new Random();


    List<PostInfo> posts = new ArrayList<PostInfo>();
    HashSet<Double> Latitude = new HashSet<Double>();
    HashSet<Double> Longitude = new HashSet<Double>();
    //Used to store the post Info the user just type in
    String postInfoFull = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }


        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()) { // gps enabled} // return boolean true/false
            location_current = gps.getLocation();
            l_1 = location_current.getLatitude();
            l_2 = location_current.getLongitude();
            Latitude.add(l_1);
            Longitude.add(l_2);
            //l_1 = 40.4489771, l_2 = -79.9309191
            MarkerOptions mark = new MarkerOptions().position(new LatLng(l_1,l_2))
                    .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            String Title = "You are here";
            mark.title(Title);
            //mMap.addMarker(mark);

            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(l_1, l_2))
                    .radius(1500)
                    .strokeColor(0x2000ff00)
                    .fillColor(0x2000ff00));
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(l_1,
                            l_2));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo((float)13.8);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

            System.out.println(l_1);
        }
        double test_l1 = 40.4489771;
        double test_l2 = -79.9309191;
        MarkerOptions markPost = new MarkerOptions().position(new LatLng(test_l1,test_l2))
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        String Title = "Today I don't feel like doing anything. I just wanna lay in my bed." +
                "don't feel like picking up my phone, so leave a message at the tone cause today I swear I'm not " +
                "doing anything";


        PostInfo post = new PostInfo(40.45,-79.94,"Everybody is 坑货!");
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post.getLatitude(), post.getLongitude())).title(post.getMessage()).alpha((float) 0.7));

        PostInfo post2 = new PostInfo(40.4428000,-79.9364000,Title);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post2.getLatitude(), post2.getLongitude())).title(post2.getMessage()).alpha((float) 0.7));

        posts.add(post);posts.add(post2);

        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);



        //set click listener for button
        Button postNew = (Button) findViewById(R.id.marker_something_new);
        postNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PostNewActivity.class);
                startActivity(intent);
            }
        });

        //set fresh listener for button using sonar
        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "now searching!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                mMap.clear();
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(l_1, l_2))
                        .radius(1500)
                        .strokeColor(0x2000ff00)
                        .fillColor(0x2000ff00));
                for (int i = 0; i < posts.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(
                            new LatLng(posts.get(i).getLatitude(),posts.get(i).getLongitude()))
                    .title(posts.get(i).getMessage()).alpha((float)0.7));
                }
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        System.out.println("resume");
        if(getIntent().getStringExtra("input") == null){
            return;
        }
        String input = getIntent().getStringExtra("input").toString();
        postInfoFull = input;

        MarkerOptions mark = new MarkerOptions().position(new LatLng(l_1,l_2))
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        String Title = input;
        PostInfo post_new = new PostInfo(l_1, l_2,Title);
        posts.add(post_new);
        mark.title(Title);

        mMap.addMarker(mark);
        System.out.println(Title);

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(R.layout.infowindow,null);
        TextView title = (TextView) infoWindow.findViewById(R.id.marker_title);
        //TextView snippet = (TextView) infoWindow.findViewById(R.id.marker_snippet);

        String titleString = marker.getTitle();
        //When click the marker, remember the postInfo for toast display
        postInfoFull = titleString;

        //If the text exceeds the default length, break it
        String[] words = titleString.split(" ");
        if (words.length > len) {
            titleString = "";
            for (int i = 0; i < len; i++) {
                titleString += words[i] + " ";
            }
            titleString += "......>>";
            //snippet.setText("Tab to read more");

        }
        if (TextUtils.isEmpty(titleString)) {
            titleString = "no title";
        }

        title.setText(titleString);



        return infoWindow;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        //Toast.makeText(getApplicationContext(), "latitude = " + l_1 +
                //",longitude = " + l_2, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), postInfoFull,
                Toast.LENGTH_LONG).show();
    }
}
