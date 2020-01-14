package at.jku.assistivetechnology.myapplication.coreModules.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefUtils {
    private static final String SHARED_PREF_NAME = "RestaurantGuide";
    private static SharedPrefUtils INSTANCE = null;
    private SharedPreferences preferences;

    public static SharedPrefUtils getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SharedPrefUtils(context);
            return INSTANCE;
        }
        return INSTANCE;
    }

    private SharedPrefUtils(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
    }


    public void isDarkMode(Boolean b) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("darkMode", b);
        editor.apply();
    }

    public Boolean isDarkMode() {
        return preferences.getBoolean("darkMode", true);
    }

    public void clearAllPrefs() {
        preferences.edit().clear().apply();
    }
}
