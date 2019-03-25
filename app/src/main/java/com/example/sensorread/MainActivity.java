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
    private LineChart xChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorReadText = findViewById(R.id.sensorReadText);
        startStopButton = findViewById(R.id.startStopButton);
        progressBar = findViewById(R.id.progressBar);
        startStopButton.setText(isStarted ? "STOP" : "START");
        progressBar.setMax((int) mLight.getMaximumRange());

        xChart = findViewById(R.id.xChart);
//        xChart.getDescription().setEnabled(true);
        xChart.setTouchEnabled(false);
        xChart.setScaleEnabled(false);
        xChart.setPinchZoom(false);
        xChart.setData(new LineData());

        YAxis leftAxis = xChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawGridLines(true);

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        sensorReadText.setText(Float.toString(lux) + " Lux");
        progressBar.setProgress((int) lux);

        LineData data = xChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), lux), 0);
            data.notifyDataChanged();

            xChart.notifyDataSetChanged();
            xChart.setVisibleXRangeMaximum(150);
            xChart.moveViewToX(data.getEntryCount());
        }

    }

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStarted) {
            sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStarted) {
            sensorManager.unregisterListener(this);
        }
    }


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
