package com.example.ricky.mobilepervasiveonemile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MapView mapView;
    private Location location_current;
    double l_1 = 0;
    double l_2 = 0;

    PopupWindow popupWindow;

    //set the default number of words for small display
    int len = 20;

    //For same latitude and longitude adjustment
    int max=20;

    boolean hasPop = false;

    List<PostInfo> posts = new ArrayList<>();
    HashSet<Double> Latitude = new HashSet<Double>();
    HashSet<Double> Longitude = new HashSet<Double>();
    //Used to store the post Info the user just type in
    String postInfoFull = null;
    String addressInfo = null;
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

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
                    .radius(600)
                    .strokeColor(0x2000ff00)
                    .fillColor(0x2000ff00));
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(l_1,
                            l_2));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo((float)15);

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


        // adding testing points, no meaning here, I have no fucking idea what I am doing here but you will know, so, keep calm
        String addressLine = "";
        String city = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(l_1 + 0.00001,l_2 + 0.00001,1);
            if (addresses.size() > 0) {
                addressLine = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                System.out.println(addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PostInfo post = new PostInfo(l_1 + 0.00001, l_2 + 0.00001,"Can't wait to show others our fantastic Android Application! We worked so hard on that and " +
                "we believe you guys will love it!!!!",addressLine + ", " + city);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post.getLatitude(), post.getLongitude())).title(post.getMessage()).alpha((float) 0.7)
                .snippet(post.getAddress()));

        try {
            List<Address> addresses = geocoder.getFromLocation(l_1 + 0.001,l_2 + 0.001,1);
            if (addresses.size() > 0) {
                addressLine = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                System.out.println(addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostInfo post2 = new PostInfo(l_1 + 0.001, l_2 + 0.001, "Oh my god. The lunch is awful. Well, I cannot complain more when I see a lovely girl doing her homework while eating that tiny sandwich. I " +
                "wish I could help her. Oh, my girlfriend is coming. Never mind.", addressLine + ", " + city);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post2.getLatitude(), post2.getLongitude())).title(post2.getMessage()).alpha((float) 0.7)
                .snippet(post2.getAddress()));

        try {
            List<Address> addresses = geocoder.getFromLocation(l_1 + 0.001,l_2 - 0.001,1);
            if (addresses.size() > 0) {
                addressLine = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                System.out.println(addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostInfo post3 = new PostInfo(l_1 + 0.001, l_2 - 0.001, "Come on everyone! We have built a wonderful app on IOS!! Please come to see how amazing it is!!!" +
                "You know where we are, don't you!", addressLine + ", " + city);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post3.getLatitude(), post3.getLongitude())).title(post3.getMessage()).alpha((float) 0.7)
                .snippet(post3.getAddress()));

        try {
            List<Address> addresses = geocoder.getFromLocation(l_1 - 0.002,l_2 - 0.002,1);
            if (addresses.size() > 0) {
                addressLine = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                System.out.println(addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostInfo post4 = new PostInfo(l_1 - 0.002, l_2 - 0.002, "Three midterm exams today, ORZ...", addressLine + ", " + city);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post4.getLatitude(), post4.getLongitude())).title(post4.getMessage()).alpha((float) 0.7)
                .snippet(post4.getAddress()));

        try {
            List<Address> addresses = geocoder.getFromLocation(l_1 - 0.002,l_2 + 0.001,1);
            if (addresses.size() > 0) {
                addressLine = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                System.out.println(addressLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PostInfo post5 = new PostInfo(l_1 - 0.002, l_2 + 0.001, "Hey, does anyone want to work on 10601B hw6 together? We have three people in the study room in Wean Library but we are " +
                "stuck on problem2. Can anyone help us?", addressLine + ", " + city);
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(post5.getLatitude(), post5.getLongitude())).title(post5.getMessage()).alpha((float) 0.7)
                .snippet(post5.getAddress()));


        posts.add(post);posts.add(post2);posts.add(post3);posts.add(post4);
        posts.add(post5);

        try {
            List<Address> addresses = geocoder.getFromLocation(l_1,l_2,1);
            if (addresses.size() > 0) {
                addressLine = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                addressInfo = addressLine + ", " + city;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);



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
                Toast toast = Toast.makeText(getApplicationContext(), "exploring!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                mMap.clear();


                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(l_1, l_2))
                        .radius(600)
                        .strokeColor(0x2000ff00)
                        .fillColor(0x2000ff00));
                for (int i = 0; i < posts.size(); i++) {
                    //Test for getting address from latitude and longitude
                            mMap.addMarker(new MarkerOptions().position(
                                    new LatLng(posts.get(i).getLatitude(),posts.get(i).getLongitude()))
                                    .title(posts.get(i).getMessage()).alpha((float)0.7)
                            .snippet(posts.get(i).getAddress()));
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
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(postInfoFull).snippet(addressInfo);
        String Title = input;
        GPSTracker gps_new = new GPSTracker(this);
        l_1 = gps_new.getLatitude();
        l_2 = gps_new.getLongitude();
        PostInfo post_new = new PostInfo(l_1, l_2,Title,addressInfo);
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
        addressInfo = marker.getSnippet();
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
        /*
        Toast.makeText(getApplicationContext(), postInfoFull,
                Toast.LENGTH_LONG).show();
        */
        if (popupWindow == null) {
            View infoWindow = getLayoutInflater().inflate(R.layout.taste, null);
            TextView address = (TextView) infoWindow.findViewById(R.id.address);
            address.setText(addressInfo);
            TextView title = (TextView) infoWindow.findViewById(R.id.show_something);
            title.setText(postInfoFull);
            popupWindow = new PopupWindow(infoWindow);
            popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
            //popupWindow.showAtLocation(infoWindow, Gravity.BOTTOM, 0, 0);
            popupWindow.showAsDropDown(infoWindow,600,600);

            ImageButton like = (ImageButton) infoWindow.findViewById(R.id.like);
            like.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MapsActivity.this,"wocao",Toast.LENGTH_SHORT);
                    Toast toast = Toast.makeText(MapsActivity.this, "☆*:.｡. o(≧▽≦)o .｡.:*☆", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 60);
                    toast.show();
                }
            });

            ImageButton dontcare = (ImageButton) infoWindow.findViewById(R.id.dontcare);
            dontcare.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(MapsActivity.this, "keep calm because I don't care", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 60);
                    toast.show();
                }
            });

            ImageButton report = (ImageButton) infoWindow.findViewById(R.id.report);
            report.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(MapsActivity.this, "Do you really wanna report this post? We will check if there is something " +
                            "wrong with this post and delete it immediately if we find this post inappropriate. Thanks for your cooperation", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 60);
                    toast.show();
                }
            });

        }
        else {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow = null;
                return;
            }
        }


/*
        TextView textView = new TextView(this);
        textView.setText("This is a toast");
*/
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
