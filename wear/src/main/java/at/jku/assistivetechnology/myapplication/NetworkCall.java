package at.jku.assistivetechnology.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class NetworkCall extends AsyncTask {

    private final int radius;
    private final double currentlongitude;
    private final double currentlatitude;
    String response = "";
    String APIURL ="https://lz4.overpass-api.de/api/interpreter";


    public NetworkCall(int radius, double longitude, double latitude) {
        this.radius = radius;
        this.currentlongitude = longitude;
        this.currentlatitude = latitude;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("Executing the osm: ", String.valueOf(radius+"\n"+currentlongitude+"\n"+currentlatitude));
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        URL url = null;
        Object response=null;

        String call = preparePostData();

        Log.e("Call:", call);
        try {
            HttpsURLConnection httpsCon = getHttpsURLConnection(call);
            response = readResponse(httpsCon);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("OSM Response: ", String.valueOf(response));
        return response;
    }

    private Object readResponse(HttpsURLConnection httpsCon) throws IOException {
        int responseCode=httpsCon.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(httpsCon.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        } else {
            response="";
        }
        return response;
    }

    private HttpsURLConnection getHttpsURLConnection(String call) throws IOException {
        URL url;
        url = new URL(APIURL);
        HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
        httpsCon.setDoOutput(true);
        httpsCon.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(httpsCon.getOutputStream());
        out.write(call);
        out.close();
        return httpsCon;
    }

    private String preparePostData() {
        return "<osm-script>" +
                "<query type=\"node\">" +
                "<has-kv k=\"amenity\" v=\"restaurant\"/>" +
                "<has-kv k=\"wheelchair\" v=\"yes\"/>" +
                "<around radius=\""+radius+"\" lat=\""+currentlatitude+"\" lon=\""+currentlongitude+"\"/>" +
                "</query><print/></osm-script>";
    }

}


