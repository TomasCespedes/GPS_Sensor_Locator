package com.example.ehar.SensorExample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class MainActivity
        extends AppCompatActivity
        implements Observer {

    // TextViews
    private TextView accel_x_view = null;
    private TextView accel_y_view = null;
    private TextView starting_latitude = null;
    private TextView starting_longitude = null;
    private TextView ending_latitude = null;
    private TextView ending_longitude = null;

    // Buttons
    private Button start, stop, drop_pin, reset;

    // Locations
    private Location startLocation;
    private Location endLocation;

    private Observable accel;
    private LocationHandler location;
    private LocationManager lm;

    final public static int REQUEST_ASK_FINE_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        accel_x_view = (TextView) findViewById(R.id.accel_x);
        accel_y_view = (TextView) findViewById(R.id.accel_y);
        starting_latitude = (TextView) findViewById(R.id.starting_latitude);
        starting_longitude = (TextView) findViewById(R.id.starting_longitude);
        ending_latitude = (TextView) findViewById(R.id.ending_latitude);
        ending_longitude = (TextView) findViewById(R.id.ending_longitude);

        // Find buttons
        start = (Button) findViewById(R.id.start_button);
        stop = (Button) findViewById(R.id.stop_button);
        drop_pin = (Button) findViewById(R.id.pin_button);
        reset = (Button) findViewById(R.id.reset_button);

        // Create new AccelerometerHandler
        this.accel = new AccelerometerHandler(500, this);
        this.accel.addObserver(this);

        // Create new LocationHandler
        this.location = new LocationHandler(this);
        this.location.addObserver(this);

        // Start button onClick
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocation = getLastKnownLocation();
                double start_lat = startLocation.getLatitude();
                double start_long = startLocation.getLongitude();

                starting_latitude.setText(Double.toString(start_lat));
                starting_longitude.setText(Double.toString(start_long));

            }
        });

        // Stop button onClick
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endLocation = getLastKnownLocation();
                double end_lat = endLocation.getLatitude();
                double end_long = endLocation.getLongitude();

                ending_latitude.setText(Double.toString(end_lat));
                ending_longitude.setText(Double.toString(end_long));
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof AccelerometerHandler) {
            float[] values = (float[]) o;
            accel_x_view.setText(Float.toString(values[0]));
            accel_y_view.setText(Float.toString(values[1]));

        }
        else if (observable instanceof LocationHandler) {
            Location l = (Location) o;
            double lat = l.getLatitude();
            double lon = l.getLongitude();

            Toast.makeText(MainActivity.this, "Lat: " + lat +
                    " Lon: " + lon, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ASK_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("INFO: ", "Permission not granted for location.");
            }
            else {
                Log.i("INFO: ", "Permission granted for location.");
                //this.location.initializeLocationManager();
            }
    }

    private Location getLastKnownLocation() {
        lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }
}
