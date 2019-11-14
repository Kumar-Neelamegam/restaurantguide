package at.jku.assistivetechnology.myapplication;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import java.util.ArrayList;

public class CompassActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        Bundle extra = getIntent().getBundleExtra("extra");
        ArrayList<RestaurantObject> objects = (ArrayList<RestaurantObject>) extra.getSerializable("restaurantObjects");
                    Log.e("getObjects: ", objects.toString());
    }
}
