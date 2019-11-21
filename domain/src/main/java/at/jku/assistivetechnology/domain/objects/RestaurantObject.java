package at.jku.assistivetechnology.domain.objects;

import java.io.Serializable;
import java.util.HashMap;

public class RestaurantObject implements Serializable  {

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

    public Double getRestaurantLatitude() {
        return restaurantLatitude;
    }

    public void setRestaurantLatitude(Double restaurantLatitude) {
        this.restaurantLatitude = restaurantLatitude;
    }

    public Double getRestaurantLongitude() {
        return restaurantLongitude;
    }

    public void setRestaurantLongitude(Double restaurantLongitude) {
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
    Double restaurantLatitude;
    Double restaurantLongitude;
    HashMap<String, String> restaurantFullData;

    public String getRestaurantDistance() {
        return restaurantDistance;
    }

    public void setRestaurantDistance(String restaurantDistance) {
        this.restaurantDistance = restaurantDistance;
    }

    String restaurantDistance;

}
