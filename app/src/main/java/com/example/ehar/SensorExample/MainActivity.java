package com.example.ehar.SensorExample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
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
    private TextView starting_coordinates = null;
    private TextView ending_coordinates = null;
    private TextView current_coordinates = null;

    // Buttons
    private Button start, stop, drop_pin, reset;

    // Locations
    private Location startLocation;
    private Location endLocation;

    private Observable accel;
    private LocationHandler location;
    private LocationManager lm;

    //Sounds
    MediaPlayer mpPin;
    MediaPlayer mpStop;
    MediaPlayer mpStart;
    MediaPlayer mpReset;

    final public static int REQUEST_ASK_FINE_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        accel_x_view = (TextView) findViewById(R.id.accel_x);
        accel_y_view = (TextView) findViewById(R.id.accel_y);
        starting_coordinates = (TextView) findViewById(R.id.starting_coordinates);
        ending_coordinates = (TextView) findViewById(R.id.ending_coordinates);
        current_coordinates = (TextView) findViewById(R.id.current_coordinates);


        // Find buttons
        start = (Button) findViewById(R.id.start_button);
        stop = (Button) findViewById(R.id.stop_button);
        drop_pin = (Button) findViewById(R.id.pin_button);
        reset = (Button) findViewById(R.id.reset_button);

        //Initialize button colors
        this.start.setBackgroundColor(Color.GREEN);
        this.stop.setBackgroundColor(Color.GRAY);
        this.drop_pin.setBackgroundColor(Color.GRAY);
        this.reset.setBackgroundColor(Color.YELLOW);

        //Properly enable buttons
        this.start.setEnabled(true);
        this.stop.setEnabled(false);
        this.drop_pin.setEnabled(false);
        this.reset.setEnabled(true);

        //Initalize MediaPlayers for our sounds
        mpPin = MediaPlayer.create(this.getApplicationContext(), R.raw.droppin);
        mpStop = MediaPlayer.create(this.getApplicationContext(), R.raw.stoppin);
        mpStart = MediaPlayer.create(this.getApplicationContext(), R.raw.start);
        mpReset = MediaPlayer.create(this.getApplicationContext(), R.raw.resetpin);

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

                starting_coordinates.setText("(" + Double.toString(start_lat) + ", " +
                                            Double.toString(start_long) + ")");


                start.setEnabled(false);
                start.setBackgroundColor(Color.GRAY);

                stop.setEnabled(true);
                stop.setBackgroundColor(Color.RED);

                drop_pin.setEnabled(true);
                drop_pin.setBackgroundColor(Color.CYAN);

                mpStart.start();
            }
        });

        // Stop button onClick
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endLocation = getLastKnownLocation();
                double end_lat = endLocation.getLatitude();
                double end_long = endLocation.getLongitude();

                ending_coordinates.setText("(" + Double.toString(end_lat) + ", " +
                                        Double.toString(end_long) + ")");

                stop.setEnabled(false);
                stop.setBackgroundColor(Color.GRAY);

                drop_pin.setEnabled(false);
                drop_pin.setBackgroundColor(Color.GRAY);

                mpStop.start();
            }
        });

        // Drop pin button onClick
        drop_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to add pin to scrollview here.
                //put current_latitude.getText() into scrollview;
                //put current_longitude.getText() into scrollview;
                mpPin.start();
            }
        });


        // Reset button onClick
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to reset the scrollview here.

                starting_coordinates.setText("(0, 0)");
                ending_coordinates.setText("(0, 0)");

                drop_pin.setEnabled(false);
                drop_pin.setBackgroundColor(Color.GRAY);

                start.setEnabled(true);
                start.setBackgroundColor(Color.GREEN);

                stop.setEnabled(false);
                stop.setBackgroundColor(Color.GRAY);

                mpReset.start();
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

            /*
            Toast.makeText(MainActivity.this, "Lat: " + lat +
                    " Lon: " + lon, Toast.LENGTH_LONG).show();
            */
            current_coordinates.setText("(" + Double.toString(lat) + ", " +
                                        Double.toString(lon) + ")");
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
