package at.jku.assistivetechnology.myapplication.coreModules.coreModules;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;

import java.util.Locale;

import at.jku.assistivetechnology.domain.objects.RestaurantObject;
import at.jku.assistivetechnology.domain.utilities.GpsLocator;
import at.jku.assistivetechnology.myapplication.R;
import at.jku.assistivetechnology.myapplication.coreModules.utilities.SharedPrefUtils;

public class CompassActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener, TextToSpeech.OnInitListener {


    TextView txtvw_moreinfo;
    View test;
    AppCompatImageView imgvw_audio, imgvw_info;
    ImageView compass, arrow;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    GpsLocator gpsLocator;//=new GpsLocator();
    RestaurantObject objects;
    private TextToSpeech tts;
    TextView distances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        try {
            setTheme();
            init();
           // vibrateAlertUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTheme() {
        SharedPrefUtils sharedPrefUtils = SharedPrefUtils.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Compass");
        LinearLayout parent=findViewById(R.id.parent);
        if (sharedPrefUtils.isDarkMode()) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkcommonaccent)));
            parent.setBackground(new ColorDrawable(getResources().getColor(R.color.darkcommonaccent)));
        } else {
            setTheme(R.style.AppTheme_Light);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lightcommonaccent)));
            parent.setBackground(new ColorDrawable(getResources().getColor(R.color.lightcommonaccent)));
        }
    }

    private void init() {


        txtvw_moreinfo = findViewById(R.id.txtvw_moreinfo);
        imgvw_audio = findViewById(R.id.imgvw_audio);
        imgvw_info = findViewById(R.id.imgvw_info);
        gpsLocator = new GpsLocator(this);
        compass = findViewById(R.id.imgvw_compass);
        arrow = findViewById(R.id.imgvw_arrow);
        distances = findViewById(R.id.txtvw_distance);
        test=findViewById(R.id.test);

        setInitialData();

        setSensorChanges();

        imgvw_audio.setOnClickListener(CompassActivity.this);
        imgvw_info.setOnClickListener(CompassActivity.this);

        tts = new TextToSpeech(this, this);//init text to speak

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateAlertUser();
            }
        });
    }

    private void setInitialData() {
        objects = (RestaurantObject) getIntent().getSerializableExtra("extra");

        txtvw_moreinfo.setText(objects.getRestaurantName().toString());

        restaurant_Latitude = objects.getRestaurantLatitude();
        restaurant_Longitude = objects.getRestaurantLongitude();


    }


    double restaurant_Latitude;
    double restaurant_Longitude;
    SensorManager mSensorManager;
    Sensor sensor;
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;

    private void setSensorChanges() {

        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        calculateBearingDistance(sensorEvent.values[0]);

    }

    private void calculateBearingDistance(float value) {
        double pk = (180.d / Math.PI);
        double a1 = gpsLocator.getLatitude() / pk;
        double a2 = gpsLocator.getLongitude() / pk;
        double b1 = restaurant_Latitude / pk;
        double b2 = restaurant_Longitude / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        double distance1 = 6450000 * tt;
        double distancekm = Math.round(distance1) / 1000.0;
        double distancem = Math.round(distance1 * 100.0) / 100.0;

        //distance=distance/1000;
        // distmeters.setText("Distance in Meter:-"+String.valueOf(distance)+"M");

        distances.setText("Distance: " + "(" + String.valueOf(distancekm) + "KM" + ")");


        if (distancekm < 0.10) {
           vibrateAlertUser();
        }
        float degree = Math.round(value);
        float head = Math.round(value);
        float bearTo;
        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(restaurant_Latitude); //hotel latitude setting
        destinationLoc.setLongitude(restaurant_Longitude); //hotel longitude setting
        bearTo = gpsLocator.getLocation().bearingTo(destinationLoc);//calculate bear


        //bearin.setText("Bearing:"+String.valueOf(bearTo));
        //headings.setText("Heading: " + String.valueOf(degree) + " degrees");
        //bearTo = The angle from true north to the destination location from the point we're your currently standing.
        //head = The angle that you've rotated your phone from true north.


        GeomagneticField geoField = new GeomagneticField(Double.valueOf(gpsLocator.getLocation().getLatitude()).floatValue(), Double
                .valueOf(gpsLocator.getLocation().getLongitude()).floatValue(),
                Double.valueOf(gpsLocator.getLocation().getAltitude()).floatValue(),
                System.currentTimeMillis());
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

        Animation an = new RotateAnimation(currentDegreeNeedle, direction,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentDegreeNeedle = direction;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        arrow.startAnimation(an);

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(120);
        ra.setFillAfter(true);
        compass.startAnimation(ra);
        currentDegree = -degree;
    }

    private void vibrateAlertUser() {
        Toast.makeText(this, "You almost reached the restaurant..", Toast.LENGTH_SHORT).show();
// Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

// Vibrate for 400 milliseconds
        v.vibrate(400);

        showNotification();
    }

    private void showNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_restaurant_item)
                        .setContentTitle("Restaurant Guide")
                        .setContentText("You reached your destination!");

        Intent notificationIntent = new Intent(this, CompassActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearMemory();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgvw_info:
                callMoreInfoIntent();
                break;

            case R.id.imgvw_audio:
                playAudio();
                break;
        }
    }

    private void playAudio() {
        speakOut();
    }

    private void callMoreInfoIntent() {

        startActivity(new Intent(CompassActivity.this, MoreInfoActivity.class).putExtra("extra", objects));

    }

    public void clearMemory() {
        this.finish();
        gpsLocator.stopUsingGPS();
        mSensorManager.unregisterListener(this);
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.GERMAN);
            tts.setPitch((float) 0.4);
            tts.setSpeechRate(1);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                Toast.makeText(CompassActivity.this, "Not supported", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut() {
        CharSequence text = txtvw_moreinfo.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");

    }

}
