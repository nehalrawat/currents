package edu.cornell.cusd.upson;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences p = getSharedPreferences("Account", MODE_PRIVATE);
        System.out.println(p.getString("netid", "in the main"));
        if (p.getString("netid", "not there").equals("not there")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);
        ToggleButton ventilationToggle = (ToggleButton) this.findViewById(R.id.toggleVentilation);
        ventilationToggle.setChecked(p.getBoolean("ventilationToggle", false));
        ventilationToggle.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String state = "officeon";
                if(!isChecked) state = "officeoff";
                SharedPreferences p = getSharedPreferences("Account", MODE_PRIVATE);
                SharedPreferences.Editor e = p.edit();
                e.putBoolean("ventilationToggle", isChecked);
                e.commit();
                e.apply();
                Intent intent = new Intent(MainActivity.this, HttpIntentService.class);
                intent.putExtra("caller", state);
                startService(intent);
            }
        });
        ToggleButton locationToggle = (ToggleButton) this.findViewById(R.id.toggleLocation);
        locationToggle.setChecked(p.getBoolean("locationToggle", false));
        if(p.getBoolean("locationToggle", false)) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);
        }
        locationToggle.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences p = getSharedPreferences("Account", MODE_PRIVATE);
                SharedPreferences.Editor e = p.edit();
                e.putBoolean("locationToggle", isChecked);
                e.commit();
                e.apply();
                if(isChecked) {
                    Intent intent = new Intent(MainActivity.this.getApplicationContext(), LocationService.class);
                    startService(intent);
                }
                else {
                    Intent intent = new Intent(MainActivity.this.getApplicationContext(), LocationService.class);
                    stopService(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location){
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Result result) {

    }

    public void officeOn(View view) {
        Intent intent1 = new Intent(this.getApplicationContext(), HttpIntentService.class);
        startService(intent1);
    }
}
