package at.jku.assistivetechnology.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ListActivity extends WearableActivity implements RestaurantListAdapter.ItemClickListener {

    RecyclerView recyclerView;
    RestaurantListAdapter restaurantListAdapter;
    GpsLocator gpsTracker;
    double pLong = 0;
    double pLat = 0;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Enables Always-on
        try {
            setAmbientEnabled();
            getInit();
            getLocation();
            loadList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocation() {
        gpsTracker = new GpsLocator(ListActivity.this);
        if(gpsTracker.canGetLocation())
        {
            pLat = gpsTracker.getLatitude();
            pLong = gpsTracker.getLongitude();
            callAPI(pLat, pLong);
        }else{
            gpsTracker.showSettingsAlert();
        }

    }

    ArrayList<RestaurantObject> listofRestaurants = new ArrayList<>();
    private void callAPI(double pLat, double pLong) {
        NetworkCall httpsTask = new NetworkCall(1000, pLong, pLat);

        if(Utils.getNetworkStatus(this)){
        httpsTask.execute();
            startServiceCall(httpsTask);
        }
        else
        {
            Toast.makeText(gpsTracker, "Network not connected!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startServiceCall(NetworkCall httpsTask) {
        final Handler handler=new Handler();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object response = httpsTask.get();
                    Log.e("Response: ", response.toString());
                    parseResponse((String) response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(listofRestaurants.isEmpty())
                        {
                            errorTextView.setVisibility(View.VISIBLE);
                            Toast.makeText(ListActivity.this, "No data found..", Toast.LENGTH_SHORT).show();
                         return;
                        }
                        loadList();

                    }
                });

            }

        });
        thread.start();
    }

    public void parseResponse(String response) {
        RestaurantObject restaurantObject=new RestaurantObject();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(response)));

            //NodeList nList = document.getElementsByTagName("tag");
            NodeList receveidRestaurant = document.getElementsByTagName("node");    // list of every restaurant
            int j=0;
            for (int i = 0; i < receveidRestaurant.getLength(); i++) {
               //Parent Node list
                Node nNode = receveidRestaurant.item(i);
                Element eElement = (Element) nNode;
                double restaurant_latitude = Double.parseDouble(eElement.getAttribute("lat"));
                double restaurant_longitude = Double.parseDouble(eElement.getAttribute("lon"));
                Log.e("Parent Node: ", restaurant_latitude+"/"+restaurant_longitude);

                restaurantObject=new RestaurantObject();
                restaurantObject.setId(j+1);
                restaurantObject.setRestaurantDistance(calculateDistance(pLat, pLong,restaurant_latitude,restaurant_longitude, "K")+" kms");

                //Child Node List
                NodeList nList = eElement.getElementsByTagName("tag");
                for (int i1 = 0; i1 < nList.getLength(); i1++) {
                    nNode = nList.item(i1);
                    eElement = (Element) nNode;
                    //Log.e("Child Node:", "Key: "+eElement.getAttribute("k") + "Value: "+eElement.getAttribute("v"));
                    HashMap<String, String> map = new HashMap<>();
                    map.put(eElement.getAttribute("k"), eElement.getAttribute("v"));//Put full child element inside the object
                    restaurantObject.setRestaurantFullData(map);
                    if(eElement.getAttribute("k").contains("name")){
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
        }
        else {
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
        recyclerView=findViewById(R.id.recyclerview);
        errorTextView=findViewById(R.id.txtvw_error_data);
    }

    private void loadList() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantListAdapter = new RestaurantListAdapter(this, listofRestaurants);
        restaurantListAdapter.setClickListener(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(restaurantListAdapter);


    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + restaurantListAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
