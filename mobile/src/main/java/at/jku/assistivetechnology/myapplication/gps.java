package at.jku.assistivetechnology.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class gps extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, SensorEventListener, LocationListener {

    TextView txtvw;
    Button btn, callapi;
    private GpsTracker gpsTracker;
    RecyclerView recyclerView;
    double pLong = 0;
    double pLat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Init();


    }

    private void Init() {
        txtvw=findViewById(R.id.textView);
        btn=findViewById(R.id.button);
        callapi=findViewById(R.id.btn_callapi);
        recyclerView=findViewById(R.id.recyclerview);

        arrows=findViewById(R.id.arrow);
        compass=findViewById(R.id.imageCompass);

        mSensorManager =  (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new gps();

        //The minimum distance will be the distance selected
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3000, ll);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        callapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI();
            }
        });
    }

    private void callAPI() {

        final NetworkAsyncTask httpsTask = new NetworkAsyncTask(1000, pLong, pLat);
        httpsTask.execute();

        final Handler handler=new Handler();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object response = httpsTask.get();
                    Log.e("Response: ", response.toString());
                    Log.e("Response: ", response.toString());
                    parsexml((String) response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        recyclerView.setLayoutManager(new LinearLayoutManager(gps.this));
                        adapter = new MyRecyclerViewAdapter(gps.this, tmp);
                        adapter.setClickListener(gps.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.invalidate();

                    }
                });

            }

        });
        thread.start();
    }

    MyRecyclerViewAdapter adapter;

    ArrayList<String> tmp = new ArrayList<>();
    Map<Integer, Node> sortedList = new HashMap<>();
    public void parsexml(String response) {

        int counter = 0;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(response)));


            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("tag");
            NodeList receveidRestaurant = doc.getElementsByTagName("node");    // list of every restaurant

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                System.out.println("node: " + nNode.getAttributes());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("\nRestaurant Names : " + eElement.getElementsByTagName("name"));
                    System.out.println("key : " + eElement.getAttribute("k"));
                    if(eElement.getAttribute("k").equals("name")) {
                        tmp.add(eElement.getAttribute("v"));
                        sortedList.put(counter, eElement.getParentNode());
                        counter++;
                    }
                    else if (eElement.getAttribute("k").equals("lat")&&
                            (eElement.getAttribute("k").equals("lon"))){


                    }
                    System.out.println("value : " + eElement.getAttribute("v"));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocation() {

        gpsTracker = new GpsTracker(gps.this);
        if(gpsTracker.canGetLocation())
        {
            pLat = gpsTracker.getLatitude();
            pLong = gpsTracker.getLongitude();
            txtvw.setText("Latitude:"+pLat +"/"+"Longitude"+pLong);
        }else{
            gpsTracker.showSettingsAlert();
        }

    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Element item = (Element) sortedList.get(position);
        double lat = Double.parseDouble(item.getAttribute("lat"));
        double lon = Double.parseDouble(item.getAttribute("lon"));
        hotellat=lat;
        hotelon=lon;
    }

    double hotellat;
    double hotelon;
    SensorManager mSensorManager;
    Sensor sensor;
    ImageView compass;
    ImageView arrows;
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    Location userLoc=new Location("service Provider");

    public void onSensorChanged(SensorEvent sensorEvent) {

        double pk = (180.d/Math.PI);
        double a1 = pLat / pk;
        double a2 = pLong / pk;
        double b1 = hotellat / pk;
        double b2 = hotelon / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        double distance1= 6450000 * tt;
        double distancekm=Math.round(distance1)/1000.0;
        double distancem=Math.round(distance1*100.0)/100.0;

        //distance=distance/1000;
        // distmeters.setText("Distance in Meter:-"+String.valueOf(distance)+"M");
        Log.e("Calculations: ", "Distance:-"+String.valueOf(distancem)+"M"+","+
                "("+String.valueOf(distancekm)+"KM"+")");


        if(distancekm<0.01){

        }
        float degree = Math.round(sensorEvent.values[0]);
        float head = Math.round(sensorEvent.values[0]);
        float bearTo;
        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(hotellat); //hotel latitude setting
        destinationLoc.setLongitude(hotelon); //hotel longitude setting
        bearTo=userLoc.bearingTo(destinationLoc);//calculate bear


        Log.e("Bearing:::", "Bearing:"+String.valueOf(bearTo));
        Log.e("Heading:::", ("Heading: " + String.valueOf(degree) + " degrees"));
        //bearTo = The angle from true north to the destination location from the point we're your currently standing.
        //head = The angle that you've rotated your phone from true north.


        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        head -= geoField.getDeclination(); // converts magnetic north into true north

        if (bearTo < 0) {
            bearTo = bearTo + 360;
            //bearTo = -100 + 360  = 260;
        }
//This is where we choose to point it
        float direction = bearTo - head;

// If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }

        Animation an = new RotateAnimation(currentDegreeNeedle,  direction,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentDegreeNeedle = direction;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        arrows.startAnimation(an);

        RotateAnimation ra=new RotateAnimation(currentDegree,-degree,Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(120);
        ra.setFillAfter(true);
        compass.startAnimation(ra);
        currentDegree=-degree;

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
