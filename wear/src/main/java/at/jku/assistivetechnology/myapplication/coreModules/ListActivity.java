package at.jku.assistivetechnology.myapplication.coreModules;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import at.jku.assistivetechnology.domain.networkCalls.NetworkCall;
import at.jku.assistivetechnology.domain.objects.RestaurantObject;
import at.jku.assistivetechnology.domain.utilities.GpsLocator;
import at.jku.assistivetechnology.domain.utilities.Utils;
import at.jku.assistivetechnology.myapplication.R;

public class ListActivity extends WearableActivity implements RestaurantListAdapter.ItemClickListener, View.OnClickListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    WearableRecyclerView recyclerView;
    RestaurantListAdapter restaurantListAdapter;
    GpsLocator gpsTracker;
    int radiusOption = 3000;
    double pLong = 0;
    double pLat = 0;
    TextView errorTextView;
    AppCompatImageView imgvw_radius, imgvw_refresh;
    LinearLayout ll_progress;
    SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        try {
            setAmbientEnabled();
            getInit();
            retrieveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveData() {
        listofRestaurants = new ArrayList<>();//make a new instances
        getLocation();
        loadList();
    }

    private void getLocation() {
        try {
            gpsTracker = new GpsLocator(ListActivity.this);
            if (gpsTracker.canGetLocation()) {
                //pLat = 48.33;//gpsTracker.getLatitude();
                pLat = gpsTracker.getLatitude();
                //pLong = 14.31;//gpsTracker.getLongitude();
                pLong = gpsTracker.getLongitude();
                callAPI(pLat, pLong);
            } else {
                gpsTracker.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ArrayList<RestaurantObject> listofRestaurants = new ArrayList<>();

    private void callAPI(double pLat, double pLong) {
        NetworkCall httpsTask = new NetworkCall(radiusOption, pLong, pLat);

        if (Utils.getNetworkStatus(this)) {
            errorTextView.setVisibility(View.GONE);
            httpsTask.execute();
            ll_progress.setVisibility(View.VISIBLE);
            startServiceCall(httpsTask);
        } else {
            Toast.makeText(ListActivity.this, "Network not connected!!", Toast.LENGTH_SHORT).show();
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("No network available!!");
            ll_progress.setVisibility(View.GONE);
        }
    }

    private void startServiceCall(NetworkCall httpsTask) {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object response = httpsTask.get();
                    if(response!=null) {Log.e("Response: ", response.toString());
                    parseResponse((String) response);}
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listofRestaurants.isEmpty()) {
                            ll_progress.setVisibility(View.GONE);
                            errorTextView.setVisibility(View.VISIBLE);
                            Toast.makeText(ListActivity.this, "No data found..", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ll_progress.setVisibility(View.GONE);
                        loadList();

                    }
                });

            }

        });
        thread.start();
    }

    public void parseResponse(String response) {
        RestaurantObject restaurantObject = new RestaurantObject();
        listofRestaurants = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(response)));

            //NodeList nList = document.getElementsByTagName("tag");
            NodeList receveidRestaurant = document.getElementsByTagName("node");    // list of every restaurant
            int j = 0;
            for (int i = 0; i < receveidRestaurant.getLength(); i++) {
                //Parent Node list
                Node nNode = receveidRestaurant.item(i);
                Element eElement = (Element) nNode;
                double restaurant_latitude = Double.parseDouble(eElement.getAttribute("lat"));
                double restaurant_longitude = Double.parseDouble(eElement.getAttribute("lon"));
                //Log.e("Parent Node: ", restaurant_latitude + "/" + restaurant_longitude);

                restaurantObject = new RestaurantObject();
                restaurantObject.setId(j + 1);
                restaurantObject.setRestaurantDistance(calculateDistance(pLat, pLong, restaurant_latitude, restaurant_longitude, "K") + " kms");
                restaurantObject.setRestaurantLatitude(restaurant_latitude);
                restaurantObject.setRestaurantLongitude(restaurant_longitude);
                //Child Node List
                HashMap<String, String> map=new HashMap<>();
                NodeList nList = eElement.getElementsByTagName("tag");
                for (int i1 = 0; i1 < nList.getLength(); i1++) {
                    nNode = nList.item(i1);
                    eElement = (Element) nNode;
                    //Log.e("Child Node:", "Key: "+eElement.getAttribute("k") + "Value: "+eElement.getAttribute("v"));
                    map.put(eElement.getAttribute("k"), eElement.getAttribute("v"));//Put full child element inside the object
                    restaurantObject.setRestaurantFullData(map);
                    if (eElement.getAttribute("k").contains("name")) {
                        restaurantObject.setRestaurantName(eElement.getAttribute("v"));
                    }
                }

                listofRestaurants.add(restaurantObject);
                j++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    DecimalFormat df = new DecimalFormat(".##");

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return Double.parseDouble((df.format(dist)));
        }
    }

    private void getInit() {
        recyclerView = findViewById(R.id.recyclerview);
        errorTextView = findViewById(R.id.txtvw_error_data);
        imgvw_radius = findViewById(R.id.imgvw_radius);
        imgvw_refresh = findViewById(R.id.imgvw_refresh);
        imgvw_refresh.setOnClickListener(this);
        imgvw_radius.setOnClickListener(this);
        ll_progress=findViewById(R.id.ll_progress);
        spinKitView=findViewById(R.id.progressBar1);
        Sprite animate = new CubeGrid();
        spinKitView.setIndeterminateDrawable(animate);
    }

    private void loadList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // To align the edge children (first and last) with the center of the screen
        recyclerView.setEdgeItemsCenteringEnabled(true);
        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        recyclerView.setCircularScrollingGestureEnabled(true);
        recyclerView.setBezelFraction(0.5f);
        recyclerView.setScrollDegreesPerScreen(90);
        restaurantListAdapter = new RestaurantListAdapter(this, listofRestaurants);
        restaurantListAdapter.setClickListener(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(restaurantListAdapter);

    }


    @Override
    public void onItemClick(View view, int position) {
       // Toast.makeText(this, "You clicked " + restaurantListAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        callOptionDialog(position);
    }

    private void callOptionDialog(int position) {

        final CharSequence[] items = {
                "Show Compass", "More Info"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                switch (items[item].toString()) {
                    case "Show Compass":
                        callCompassActivity(position);
                        break;

                    case "More Info":
                        callMoreInfoActivity(position);
                        break;
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void callMoreInfoActivity(int itemPosition) {
        RestaurantObject tempRestaurantObjects=listofRestaurants.get(itemPosition);
        startActivity(new Intent(ListActivity.this, MoreInfoActivity.class).putExtra("extra",tempRestaurantObjects));
       // finish();
    }

    private void callCompassActivity(int itemPosition) {
        RestaurantObject tempRestaurantObjects=listofRestaurants.get(itemPosition);
        startActivity(new Intent(ListActivity.this, CompassActivity.class).putExtra("extra",tempRestaurantObjects));
        //finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsTracker != null)
            gpsTracker.stopUsingGPS();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgvw_radius:
                callRadiusOptionMenu();
                break;

            case R.id.imgvw_refresh:
                retrieveData();
                break;
        }
    }

    private void callRadiusOptionMenu() {

        final CharSequence[] items = {
                "1 Km", "2 Kms", "3 Kms", "4 Kms", "5 Kms"
        };

       AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Choose your distance radius");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                switch (items[item].toString()) {
                    case "1 Km":
                        radiusOption = 1000;
                        break;

                    case "2 Kms":
                        radiusOption = 2000;
                        retrieveData();
                        break;

                    case "3 Kms":
                        radiusOption = 3000;
                        retrieveData();
                        break;
                    case "4 Kms":
                        radiusOption = 4000;
                        retrieveData();
                        break;

                    case "5 Kms":
                        radiusOption = 5000;
                        retrieveData();
                        break;
                }

            }
        });
        alert = builder.create();
        alert.show();
    }


}
