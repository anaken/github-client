package biz.mesto.anaken.githubclient;

import android.os.StrictMode;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GcmSender {

    public static final String API_KEY = "AIzaSyC0Ashtrf2M9MWlnaPj27uQFSRwnNag-Ls";

    public static void send(Map<String, String> args) throws JSONException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();

            for (Map.Entry<String, String> entry : args.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                jData.put(key, value);
            }

            jGcmData.put("to", "/topics/global");

            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            Log.e("MYLOG", "GcmSender responce: " + resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
