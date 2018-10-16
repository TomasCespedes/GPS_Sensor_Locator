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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
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

    // New TableLayout
    TableLayout stk;

    // Location, Handler, and Manager
    private Location startLocation;
    private Location endLocation;
    private Location prevLocation;
    private Observable accel;
    private LocationHandler location;
    private LocationManager lm;

    // Counter for pin clicks
    private int pincount = 0;

    //Sounds
    MediaPlayer mpPin;
    MediaPlayer mpStop;
    MediaPlayer mpStart;
    MediaPlayer mpReset;

    // Decimal format
    DecimalFormat df = new DecimalFormat("0.######");

    final public static int REQUEST_ASK_FINE_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all views
        init();
        addTableHeaders();

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

                // Get location when user hits start
                startLocation = getLastKnownLocation();
                double start_lat = startLocation.getLatitude();
                double start_long = startLocation.getLongitude();

                // Save start location for first pin
                prevLocation = startLocation;

                // Set starting coordinates texts
                starting_coordinates.setText("(" + Double.toString(start_lat) + ", " +
                                            Double.toString(start_long) + ")");

                // Disable Buttons and change
                // colors accordingly
                start.setEnabled(false);
                start.setBackgroundColor(Color.GRAY);
                stop.setEnabled(true);
                stop.setBackgroundColor(Color.RED);
                drop_pin.setEnabled(true);
                drop_pin.setBackgroundColor(Color.CYAN);

                // Sound for start button
                mpStart.start();
            }
        });

        // Stop button onClick
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get location when user hits stop
                endLocation = getLastKnownLocation();
                double end_lat = endLocation.getLatitude();
                double end_long = endLocation.getLongitude();

                // Set the ending coordinates text
                ending_coordinates.setText("(" + Double.toString(end_lat) + ", " +
                                        Double.toString(end_long) + ")");

                // Change button enabled and
                // colors accordingly
                stop.setEnabled(false);
                stop.setBackgroundColor(Color.GRAY);
                drop_pin.setEnabled(false);
                drop_pin.setBackgroundColor(Color.GRAY);

                // Sound for stop button
                mpStop.start();
            }
        });

        // Drop pin button onClick
        drop_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Update pin counter
                pincount ++;

                // Create a new pin
                addNewPin();

                // Sound for Pin button
                mpPin.start();
            }
        });


        // Reset button onClick
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove all views and re add headers
                stk.removeAllViews();
                addTableHeaders();
                pincount = 0;


                // Reset text of starting/ending coordinates
                starting_coordinates.setText("(0, 0)");
                ending_coordinates.setText("(0, 0)");

                // Reset all the buttons
                drop_pin.setEnabled(false);
                drop_pin.setBackgroundColor(Color.GRAY);
                start.setEnabled(true);
                start.setBackgroundColor(Color.GREEN);
                stop.setEnabled(false);
                stop.setBackgroundColor(Color.GRAY);

                // Sound for reset button
                mpReset.start();
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {

        // Observable acceleration change
        if (observable instanceof AccelerometerHandler) {
            float[] values = (float[]) o;
            accel_x_view.setText(Float.toString(values[0]));
            accel_y_view.setText(Float.toString(values[1]));

        }

        // Observable location change
        else if (observable instanceof LocationHandler) {
            // constantly get current location
            Location l = (Location) o;
            double lat = l.getLatitude();
            double lon = l.getLongitude();

            // Update current coordinates on the fly
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


    /**
     * Borrowed from https://stackoverflow.com/questions/18207470
     * /adding-table-rows-dynamically-in-android/22682248#22682248
     */

    // Initialize all views to clean up code
    public void init() {

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

        // Initialize MediaPlayers for our sounds
        mpPin = MediaPlayer.create(this.getApplicationContext(), R.raw.droppin);
        mpStop = MediaPlayer.create(this.getApplicationContext(), R.raw.stoppin);
        mpStart = MediaPlayer.create(this.getApplicationContext(), R.raw.start);
        mpReset = MediaPlayer.create(this.getApplicationContext(), R.raw.resetpin);

        }

    // Add new pin to the table
    public void addNewPin() {

        stk = (TableLayout) findViewById(R.id.table_main);

        // Get current location
        Location currentLocation = getLastKnownLocation();
        double latitude = currentLocation.getLatitude();
        double longitude = currentLocation.getLongitude();

        // Pin Number
        TableRow tbrow = new TableRow(this);
        TextView t1v = new TextView(this);
        t1v.setText("" + pincount);
        t1v.setTextColor(Color.WHITE);
        t1v.setGravity(Gravity.CENTER);
        tbrow.addView(t1v);

        // Latitude
        TextView t2v = new TextView(this);
        t2v.setText(df.format(latitude));
        t2v.setTextColor(Color.WHITE);
        t2v.setGravity(Gravity.CENTER);
        tbrow.addView(t2v);

        // Longitude
        TextView t3v = new TextView(this);
        t3v.setText(df.format(longitude));
        t3v.setTextColor(Color.WHITE);
        t3v.setGravity(Gravity.CENTER);
        tbrow.addView(t3v);

        // Distance
        TextView t4v = new TextView(this);
        t4v.setText(df.format(1000* Utility.greatCircleDistance(
                prevLocation.getLatitude(),
                prevLocation.getLongitude(),
                currentLocation.getLatitude(),
                currentLocation.getLongitude())));

        t4v.setTextColor(Color.WHITE);
        t4v.setGravity(Gravity.CENTER);
        tbrow.addView(t4v);

        // Velocity
        TextView t5v = new TextView(this);
        t5v.setText(df.format(prevLocation.bearingTo(currentLocation)));
        t5v.setTextColor(Color.WHITE);
        t5v.setGravity(Gravity.CENTER);
        tbrow.addView(t5v);

        // add row view to the stack
        stk.addView(tbrow);

        // Save Previous location
        prevLocation = currentLocation;

    }

    public void addTableHeaders() {

        // Table layout
        // Adds first rows for categories
        // Drop Pin onClick should add data
        stk = (TableLayout) findViewById(R.id.table_main);

        // Pin Number
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" Pin Number ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);

        // Latitude
        TextView tv1 = new TextView(this);
        tv1.setPadding(20,9,20,0);
        tv1.setText(" Latitude ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);

        // Longitude
        TextView tv2 = new TextView(this);
        tv2.setPadding(20,9,20,0);
        tv2.setText(" Longitude ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);

        // Distance
        TextView tv3 = new TextView(this);
        tv3.setPadding(20, 0, 20, 0);
        tv3.setText(" Distance From Previous");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);

        // Velocity
        TextView tv4 = new TextView(this);
        tv4.setText(" Velocity ");
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);
        stk.addView(tbrow0);
    }

    }

