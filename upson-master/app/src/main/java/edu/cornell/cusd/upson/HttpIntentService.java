package edu.cornell.cusd.upson;

import android.app.IntentService;
import android.content.Intent;

import com.amazonaws.services.sqs.model.OverLimitException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;

// Documentation for the Postgresql database can be found at https://jdbc.postgresql.org/documentation/94/index.html
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class HttpIntentService extends IntentService {

    public HttpIntentService() {
        super("HttpIntentService");
    }

    /*
    Runs upon the intent getting passed. Will connect to the server and write to the appropriate category
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String caller = intent.getStringExtra("caller");
            HttpURLConnection connection;
            String urlName = "http://currents.us-west-2.elasticbeanstalk.com/index.php?query=";
            String code = "m77zi5aQ46iWNuA6Qhkn21kX5A03EwzS";
            if(caller.equals("officeon")) {
                try {
                    String id = getSharedPreferences("Account", MODE_PRIVATE).getString("id", "0");
                    urlName += URLEncoder.encode("update room set wantsofficeon=true where id=", "UTF-8");
                    urlName += URLEncoder.encode(id, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (caller.equals("officeoff")) {
                try {
                    urlName += URLEncoder.encode("update room set wantsofficeon=false where id=", "UTF-8");
                    String id = getSharedPreferences("Account", MODE_PRIVATE).getString("id", "0");
                    urlName += URLEncoder.encode(id, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            else if(caller.equals("upsonenter")) {
                try {
                    urlName += URLEncoder.encode("update room set xbeeinrange=true where id=", "UTF-8");
                    String id = getSharedPreferences("Account", MODE_PRIVATE).getString("id", "0");
                    urlName += URLEncoder.encode(id, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            else if(caller.equals("upsonexit")) {
                try {
                    urlName += URLEncoder.encode("update room set xbeeinrange=false where id=", "UTF-8");
                    String id = getSharedPreferences("Account", MODE_PRIVATE).getString("id", "0");
                    urlName += URLEncoder.encode(id, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (caller.equals("addid")) {
                System.out.println("Going into addid");
                try {
                    urlName += URLEncoder.encode("insert into room (id,rname,xbeeinrange,piron,eventstart,ml,wantsofficeon) values (12,'newroom',false,false,false,false,false)", "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (caller.equals("login")) {
                try {
                    String password = intent.getStringExtra("pswd");
                    String netid = intent.getStringExtra("netid");
                    urlName += URLEncoder.encode("select * from person where netid=", "UTF-8");
                    urlName += URLEncoder.encode("'"+netid+"'", "UTF-8");
                    urlName += URLEncoder.encode(" and pswd=", "UTF-8");
                    urlName += URLEncoder.encode("'"+password+"'", "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            urlName += "&id=" + code;
            URL url = null;
            try {
                url = new URL(urlName);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                if (caller.equals("login")) {
                    Intent intent1 = new Intent(this, LoginActivity.class);
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    System.out.println("sb!! " + sb.toString());
                    if(sb.toString() != "" && sb.toString() != "Failed") intent1.putExtra("valid", true);
                    else intent1.putExtra("valid", false);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
                connection.disconnect();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopSelf();
    }
}
