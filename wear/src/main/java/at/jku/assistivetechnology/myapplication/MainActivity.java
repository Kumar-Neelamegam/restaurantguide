package at.jku.assistivetechnology.myapplication;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class MainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rowitem);

        // Enables Always-on
        setAmbientEnabled();
    }
}
