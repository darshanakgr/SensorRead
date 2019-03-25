package com.example.sensorread;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor mLight;
    private TextView sensorReadText;
    private Button startStopButton;
    private ProgressBar progressBar;
    private boolean isStarted = false;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting access to sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Getting references to form's fields
        sensorReadText = findViewById(R.id.sensorReadText);
        startStopButton = findViewById(R.id.startStopButton);
        progressBar = findViewById(R.id.progressBar);
        startStopButton.setText(isStarted ? "STOP" : "START");
        progressBar.setMax((int) mLight.getMaximumRange());

        // Initialize chart
        chart = findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setData(new LineData());

        // Configure chart's y axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawGridLines(true);

    }

    /**
     * This is a trigger method that will be registered with the sensor manager
     *
     * @param event - details of the sensor event
     */
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // Obtain the light sensor's value
        float lux = event.values[0];
        sensorReadText.setText(Float.toString(lux) + " Lux");
        progressBar.setProgress((int) lux);

        // Creating data for the line chart
        LineData data = chart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), lux), 0);
            data.notifyDataChanged();

            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(150);
            chart.moveViewToX(data.getEntryCount());
        }

    }

    /**
     * This will produce an empty dataset
     *
     * @return set
     */
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Light Sensor (Lux)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.GREEN);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    /**
     * Triggering event that comes with the SensorEventListener class
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Will register the event listener when the application resumes the activity according to the button status
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isStarted) {
            sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Will unregister the event listener when the application resumes the activity according to the button status
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (isStarted) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * This method will be triggered when the start/stop button is pressed
     * Will change the name of the button accordingly
     * @param view
     */
    public void startStopButtonPressed(View view) {
        if (!isStarted) {
            sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
            isStarted = true;

        } else {
            sensorManager.unregisterListener(this);
            isStarted = false;
        }
        startStopButton.setText(isStarted ? "STOP" : "START");
    }
}
