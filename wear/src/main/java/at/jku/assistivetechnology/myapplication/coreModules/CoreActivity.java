package at.jku.assistivetechnology.myapplication.coreModules;

import android.Manifest;
import android.content.Context;

import androidx.core.app.ActivityCompat;

import at.jku.assistivetechnology.myapplication.R;
import at.jku.assistivetechnology.myapplication.utilities.RuntimePermissionsActivity;

public abstract  class CoreActivity extends RuntimePermissionsActivity implements  ActivityCompat.OnRequestPermissionsResultCallback{
    private Context context;
    private static final int REQUEST_PERMISSIONS = 20;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setContentView(int layoutResID) {
        try {
            super.setContentView(layoutResID);
            bindViews();
            setContext(this);
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to bind all views related to resourceLayout
     */
    protected abstract void bindViews();

    /**
     * called to set view listener for views
     */
    protected abstract void setListeners();

    //***************************************************************************************************
    public void isStoragePermissionGranted() {

        CoreActivity.super.requestAppPermissions(new
                        String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE

                }, R.string
                        .runtime_permissions_txt
                , REQUEST_PERMISSIONS);

    }
    //***************************************************************************************************
//End
}
