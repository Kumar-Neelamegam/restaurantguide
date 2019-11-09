package at.jku.assistivetechnology.myapplication;

import java.util.HashMap;

public class RestaurantObject {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantLatitude() {
        return restaurantLatitude;
    }

    public void setRestaurantLatitude(String restaurantLatitude) {
        this.restaurantLatitude = restaurantLatitude;
    }

    public String getRestaurantLongitude() {
        return restaurantLongitude;
    }

    public void setRestaurantLongitude(String restaurantLongitude) {
        this.restaurantLongitude = restaurantLongitude;
    }

    public HashMap<String, String> getRestaurantFullData() {
        return restaurantFullData;
    }

    public void setRestaurantFullData(HashMap<String, String> restaurantFullData) {
        this.restaurantFullData = restaurantFullData;
    }

    int id;
    String restaurantName;
    String restaurantLatitude;
    String restaurantLongitude;
    HashMap<String, String> restaurantFullData;

    public String getRestaurantDistance() {
        return restaurantDistance;
    }

    public void setRestaurantDistance(String restaurantDistance) {
        this.restaurantDistance = restaurantDistance;
    }

    String restaurantDistance;

}
