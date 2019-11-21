package at.jku.assistivetechnology.myapplication.coreModules;

import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.Locale;

import at.jku.assistivetechnology.myapplication.R;
import at.jku.assistivetechnology.myapplication.objects.RestaurantObject;
import at.jku.assistivetechnology.myapplication.utilities.GpsLocator;


public class CompassActivity extends WearableActivity implements SensorEventListener, View.OnClickListener, TextToSpeech.OnInitListener {


    TextView txtvw_moreinfo;
    AppCompatImageView imgvw_audio, imgvw_info;
    ImageView compass, arrow;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    GpsLocator gpsLocator;//=new GpsLocator();
    RestaurantObject objects;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        txtvw_moreinfo=findViewById(R.id.txtvw_moreinfo);
        imgvw_audio=findViewById(R.id.imgvw_audio);
        imgvw_info=findViewById(R.id.imgvw_info);
        gpsLocator=new GpsLocator(this);
        compass=findViewById(R.id.imgvw_compass);
        arrow=findViewById(R.id.imgvw_arrow);


        setInitialData();

        setSensorChanges();

        imgvw_audio.setOnClickListener(CompassActivity.this);
        imgvw_info.setOnClickListener(CompassActivity.this);

        tts = new TextToSpeech(this, this);//init text to speak
    }

    private void setInitialData() {
        objects = (RestaurantObject) getIntent().getSerializableExtra("extra");

        txtvw_moreinfo.setText(objects.getRestaurantName().toString());

        restaurant_Latitude=objects.getRestaurantLatitude();
        restaurant_Longitude=objects.getRestaurantLongitude();


    }


    double restaurant_Latitude;
    double restaurant_Longitude;
    SensorManager mSensorManager;
    Sensor sensor;
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    Location userLoc=new Location("service Provider");
    double currentLong = 0;
    double currentLat = 0;


    private void setSensorChanges() {

        mSensorManager =  (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        calculateBearingDistance(event.values[0]);
    }

    private void calculateBearingDistance(float value) {
        double pk = (180.d/Math.PI);
        double a1 = gpsLocator.getLatitude() / pk;
        double a2 = gpsLocator.getLongitude() / pk;
        double b1 = restaurant_Latitude / pk;
        double b2 = restaurant_Longitude / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        double distance1= 6450000 * tt;
        double distancekm=Math.round(distance1)/1000.0;
        double distancem=Math.round(distance1*100.0)/100.0;

        //distance=distance/1000;
        Log.e("Calculations: ", "Distance:-"+String.valueOf(distancem)+"M"+","+"("+String.valueOf(distancekm)+"KM"+")");

        if(distancekm<0.01){

        }
        float degree = Math.round(value);
        float head = Math.round(value);
        float bearTo;
        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(restaurant_Latitude); //hotel latitude setting
        destinationLoc.setLongitude(restaurant_Longitude); //hotel longitude setting
        bearTo=userLoc.bearingTo(destinationLoc);//calculate bear


       // Log.e("Bearing:::", "Bearing:"+String.valueOf(bearTo));
       // Log.e("Heading:::", ("Heading: " + String.valueOf(degree) + " degrees"));
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

        an.setDuration(100);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        arrow.startAnimation(an);

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
        switch (v.getId())
        {
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

        startActivity(new Intent(CompassActivity.this, MoreInfoActivity.class).putExtra("extra",objects));

    }

    public void clearMemory()
    {
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
        CharSequence text =txtvw_moreinfo.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,"id1");

    }

}
