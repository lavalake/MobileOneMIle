package com.example.ricky.mobilepervasiveonemile;

import java.math.BigInteger;
import java.util.HashMap;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.telephony.TelephonyManager;
import java.security.*;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GPSTracker gps;
    private Location location_current;
    double l_1 = 0;
    double l_2 = 0;
    private String url ="https://enigmatic-spire-7134.herokuapp.com/";
    String imei;
    String hashtext;
    Marker ownMarker;

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
    String Title = null;

    //hashmap to store the marker and post id
    HashMap<Marker, Integer> markerPostMap;
    public HashSet<Integer> ownPostSet;
    private Integer currentId;
    private  long timeStamp;

    //final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(this.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(imei.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        timeStamp = -1;
        setUpMapIfNeeded();

        geocoder = new Geocoder(this, Locale.getDefault());

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }else{
            System.out.println("mMap null");
        }

        gps = new GPSTracker(this);

        if(gps.canGetLocation()) { // gps enabled} // return boolean true/false
            location_current = gps.getLocation();
            if(location_current != null) {
                l_1 = location_current.getLatitude();
                l_2 = location_current.getLongitude();
            }else{
                System.out.println("location null");
                l_1 = 40.4489771;
                l_2 = -79.9309191;
            }

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

        markerPostMap = new HashMap<Marker, Integer>();
        ownPostSet = new HashSet<Integer>();
        // query data from server
        getPosts();



        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);



        //set click listener for button
        FloatingActionButton postNew = (FloatingActionButton) findViewById(R.id.marker_something_new);
        postNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PostNewActivity.class);
                startActivity(intent);
            }
        });

        //set fresh listener for button using sonar
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "exploring!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                mMap.clear();
                posts.clear();
                // query data from server
               getPosts();
                if(ownMarker != null) {
                    MarkerOptions nMark = new MarkerOptions().position(new LatLng(ownMarker.getPosition().latitude,ownMarker.getPosition().longitude))
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(ownMarker.getTitle()).snippet(ownMarker.getSnippet());
                    mMap.addMarker(nMark);
                }

                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(l_1, l_2))
                        .radius(600)
                        .strokeColor(0x2000ff00)
                        .fillColor(0x2000ff00));


            }
        });


    }

    protected void getPosts(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String jsonurl = url + "posts.json";

        JsonArrayRequest sRequest=new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response);
                double l_a = 0;
                double l_o = 0;
                Integer id = 0;

                try {

                    {
                        for(int i=0; i<response.length(); i++) {

                            JSONObject json = response.getJSONObject(i);

                            Title = json.getString("content");


                            l_a = json.getDouble("latitude");
                            l_o = json.getDouble("longitude");
                            String hashImei = json.getString("imei");
                            int postId = json.getInt("id");
                            String addressLine = null;
                            String city = null;
                            try {
                                List<Address> addresses = geocoder.getFromLocation(l_a, l_o, 1);
                                if (addresses.size() > 0) {
                                    addressLine = addresses.get(0).getAddressLine(0);
                                    city = addresses.get(0).getLocality();
                                    System.out.println(addressLine);
                                    addressInfo = addressLine;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            id = json.getInt("id");

                            System.out.println("Title "+Title+" l_1 "+l_a+" l2 "+l_o);
                            PostInfo post_new = new PostInfo(l_a, l_o, Title, "Carnegie Mellon University, Pittsburgh");
                            posts.add(post_new);
                            if(!hashImei.equals(hashtext)) {
                                MarkerOptions nMark = new MarkerOptions().position(new LatLng(l_a, l_o))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(postInfoFull).snippet(addressInfo);
                                nMark.title(Title);


                                Marker m = mMap.addMarker(nMark);
                                if(m == null)
                                    System.out.println("marker "+m);
                                else
                                if(id != null)
                                    markerPostMap.put(m, id);
                            }
                            else{
                                MarkerOptions nMark = new MarkerOptions().position(new LatLng(l_a, l_o))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(postInfoFull).snippet(addressInfo);
                                nMark.title(Title);


                                Marker m = mMap.addMarker(nMark);
                                if(m == null)
                                    System.out.println("marker "+m);
                                else
                                if(id != null) {
                                    markerPostMap.put(m, id);
                                    ownPostSet.add(postId);
                                }
                            }





                            // dddSystem.out.println("test");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                System.out.println("sorry,Error");
            }
        });
        sRequest.setShouldCache(false);
        requestQueue.add(sRequest);
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


        if(getIntent().getStringExtra("input") != null){
            String input = getIntent().getStringExtra("input").toString();
            postInfoFull = input;
            long newTime = System.currentTimeMillis();
            if(timeStamp == -1 || (newTime - timeStamp > 60000)) {
                l_1 = gps.getLatitude();
                l_2 = gps.getLongitude();
                //test for address
                String addressLine = null;
                String city = null;
                try {
                    List<Address> addresses = geocoder.getFromLocation(l_1, l_2, 1);
                    if (addresses.size() > 0) {
                        addressLine = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        System.out.println(addressLine);
                        addressInfo = addressLine;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MarkerOptions mark = new MarkerOptions().position(new LatLng(l_1, l_2))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(postInfoFull).snippet(addressInfo);
                String Title = input;





                PostInfo post_new = new PostInfo(l_1, l_2, Title, "Carnegie Mellon University, Pittsburgh");
                posts.add(post_new);
                mark.title(Title);
                ownMarker = mMap.addMarker(mark);
                markerPostMap.put(ownMarker,-1);
                ownPostSet.add(-1);
                System.out.println(Title);
                timeStamp = newTime;
                RequestQueue requestQueue = Volley.newRequestQueue(this);

                String jsonurl = url + "posts";
                JSONObject jsonObj = new JSONObject();
                JSONObject postObj = new JSONObject();
                /*
                MessageDigest m = null;
                try {
                    m = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                m.reset();
                m.update(imei.getBytes());
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1,digest);
                String hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
                while(hashtext.length() < 32 ){
                    hashtext = "0"+hashtext;
                }*/
                System.out.println("imei hashcode "+imei + " " + hashtext);
                try {

                    jsonObj.put("content", Title);
                    jsonObj.put("latitude", l_1);
                    jsonObj.put("longitude", l_2);
                    jsonObj.put("imei", hashtext);
                    jsonObj.put("like", 0);
                    jsonObj.put("report", 0);
                    jsonObj.put("dontcare", 0);

                    postObj.put("post", jsonObj);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest sRequest = new JsonObjectRequest(Request.Method.POST, jsonurl, postObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            int id = response.getInt("id");
                            markerPostMap.put(ownMarker,id);
                            System.out.println("receive id "+id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        System.out.println("sorry,Error" + arg0);
                    }
                });
                sRequest.setShouldCache(false);
                requestQueue.add(sRequest);
            }
            else{
                System.out.println("new marker!!!!!!!");
                Toast toast = Toast.makeText(MapsActivity.this, "can not create new post, please wait for a while", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 60);
                toast.show();
                //ownMarker.setTitle(postInfoFull);
            }


        }




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
        currentId = markerPostMap.get(marker);
        if(!ownPostSet.contains(currentId)) {

            System.out.println("change color");
            if (marker != ownMarker)
                marker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
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
        System.out.println("marker");
        currentId = markerPostMap.get(marker);
        if (popupWindow == null) {
            View infoWindow = getLayoutInflater().inflate(R.layout.taste, null);
            TextView address = (TextView) infoWindow.findViewById(R.id.address);
            address.setText(addressInfo);
            TextView title = (TextView) infoWindow.findViewById(R.id.show_something);
            title.setText(postInfoFull);
            popupWindow = new PopupWindow(infoWindow);
            //popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
            //popupWindow.showAtLocation(findViewById(R.id.map), Gravity.CENTER_VERTICAL, 200, 200);
            popupWindow.showAsDropDown(infoWindow,720,720);

            ImageButton like = (ImageButton) infoWindow.findViewById(R.id.like);
            like.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MapsActivity.this,"wocao",Toast.LENGTH_SHORT);
                    if(ownPostSet.contains(currentId)){
                        Toast toast = Toast.makeText(MapsActivity.this, "can not like your own post", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 60);
                        toast.show();
                        return;
                    }
                    Toast toast = Toast.makeText(MapsActivity.this, "☆*:.｡. o(≧▽≦)o .｡.:*☆", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 60);
                    toast.show();
                    RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());

                    String jsonurl = url +"posts/" + currentId;
                    JSONObject jsonObj = new JSONObject();
                    JSONObject postObj = new JSONObject();
                    try {

                        //jsonObj.put("id",currentId);
                        jsonObj.put("like", true);
                        jsonObj.put("report", false);
                        jsonObj.put("dontcare", false);
                        postObj.put("post", jsonObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest sRequest=new JsonObjectRequest(Request.Method.PATCH, jsonurl, postObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            System.out.println("sorry,Error");
                        }
                    });
                    sRequest.setShouldCache(false);
                    requestQueue.add(sRequest);
                }
            });

            ImageButton dontcare = (ImageButton) infoWindow.findViewById(R.id.dontcare);
            dontcare.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ownPostSet.contains(currentId)){
                        Toast toast = Toast.makeText(MapsActivity.this, "can not dont care your own post", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 60);
                        toast.show();
                        return;
                    }
                    Toast toast = Toast.makeText(MapsActivity.this, "keep calm because I don't care", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 60);
                    toast.show();
                    RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());

                    String jsonurl = url +"posts/" + currentId;
                    JSONObject jsonObj = new JSONObject();
                    JSONObject postObj = new JSONObject();
                    try {

                        //jsonObj.put("id",currentId);
                        jsonObj.put("dontcare", true);
                        jsonObj.put("like", false);
                        jsonObj.put("report", true);
                        postObj.put("post", jsonObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest sRequest=new JsonObjectRequest(Request.Method.PATCH, jsonurl, postObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            System.out.println("sorry,Error");
                        }
                    });
                    sRequest.setShouldCache(false);
                    requestQueue.add(sRequest);

                }
            });

            ImageButton report = (ImageButton) infoWindow.findViewById(R.id.report);
            report.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ownPostSet.contains(currentId)){
                        Toast toast = Toast.makeText(MapsActivity.this, "can not report your own post", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 60);
                        toast.show();
                        return;
                    }
                    Toast toast = Toast.makeText(MapsActivity.this, "Do you really wanna report this post? We will check if there is something " +
                            "wrong with this post and delete it immediately if we find this post inappropriate. Thanks for your cooperation", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 60);
                    toast.show();
                    RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());

                    String jsonurl = url + "posts/" + currentId;
                    JSONObject jsonObj = new JSONObject();
                    JSONObject postObj = new JSONObject();
                    try {

                        //jsonObj.put("id",currentId);
                        jsonObj.put("report", true);
                        jsonObj.put("like", false);
                        jsonObj.put("dontcare", false);
                        postObj.put("post", jsonObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest sRequest=new JsonObjectRequest(Request.Method.PATCH, jsonurl, postObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            System.out.println("sorry,Error");
                        }
                    });
                    sRequest.setShouldCache(false);
                    requestQueue.add(sRequest);
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
