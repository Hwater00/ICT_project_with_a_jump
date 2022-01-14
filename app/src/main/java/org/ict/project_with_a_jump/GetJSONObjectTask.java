package org.ict.project_with_a_jump;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GetJSONObjectTask extends AsyncTask<String, String, String> {
    String GEOCODE_USER_INFO = "KakaoAK 14dfd0dab42dd87c185a32513d8fbe58";
    String result = null;
    String x = "";
    String y = "";

    /*
    GetJSONObjectTask (String x, String y) {
        this.x = x;
        this.y = y;
    }

     */

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        String value = "";

        try {
            // URL url = new URL("https://dapi.kakao.com/v2/local/geo/coord2address.json?input_coord=WGS84&x="+x+"&y="+y);
            URL url = new URL("https://dapi.kakao.com/v2/local/geo/coord2address.json?input_coord=WGS84&x=126.706308&y=37.504675");

            Charset charset = StandardCharsets.UTF_8;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", GEOCODE_USER_INFO);
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();

                JSONObject obj = new JSONObject(result);
                String size = obj.getString("meta");


                if (size != "0") {
                    JSONArray array = obj.getJSONArray("documents");
                    JSONObject obj2 = (JSONObject) array.get(0);
                    JSONObject obj3 = obj2.getJSONObject("road_address");
                    value = obj3.getString("address_name");
                    // value += " (" + obj3.getString("building_name") + ")";


                }

            } else {
                result = "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void onPostExecute(String s) {

    }
}
