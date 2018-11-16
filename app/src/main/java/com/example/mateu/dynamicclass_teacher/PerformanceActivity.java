package com.example.mateu.dynamicclass_teacher;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PerformanceActivity extends AppCompatActivity {
    private GraphView mGraphViewPerformance;
    private MaterialSpinner mSpinnerChapter;
    private MaterialSpinner mSpinnerStudent;
    private MaterialSpinner mSpinnerDifficulty;

    private String mChapterFilter = "All Chapters";
    private String mStudentFilter = "All Students";
    private String mDifficultyFilter = "All Difficulties";

    private List<String> studentsList = new ArrayList<>();
    private List<String> mAlternativesList = new ArrayList<>();
    private HashMap<String, Integer> scores = new HashMap<>();

    private String mSubjectCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        //Declare views
        mGraphViewPerformance = findViewById(R.id.graphViewPerformance);
        mSpinnerChapter = findViewById(R.id.spinnerChapter);
        mSpinnerStudent = findViewById(R.id.spinnerStudent);
        mSpinnerDifficulty = findViewById(R.id.spinnerDifficulty);

        Bundle bundle = getIntent().getExtras();
        mSubjectCode = bundle.getString("subjectCode");



        studentsList.add("StudentA");
        studentsList.add("StudentB");
        studentsList.add("StudentC");
        studentsList.add("StudentD");
        studentsList.add("StudentE");
        studentsList.add("StudentF");
        studentsList.add("StudentG");
        studentsList.add("StudentH");
        studentsList.add("StudentI");
        studentsList.add("StudentJ");

        Random random = new Random();
        for (int i = 0; i < studentsList.size(); i++){
            scores.put(studentsList.get(i), random.nextInt(100));
        }
        studentsList.add("All Students");

        mAlternativesList.add()


        setupGraph();
        setupSpinners();
        generateSeries();
    }

    private void setupGraph(){
        mGraphViewPerformance.setTitle("Performance");

        LegendRenderer legendRenderer = mGraphViewPerformance.getLegendRenderer();
        legendRenderer.setVisible(true);
        legendRenderer.setAlign(LegendRenderer.LegendAlign.TOP);

        GridLabelRenderer gridLabelRenderer = mGraphViewPerformance.getGridLabelRenderer();
        gridLabelRenderer.setHorizontalAxisTitle("Estudantes");
        gridLabelRenderer.setVerticalAxisTitle("Pontos (Percentual)");
        gridLabelRenderer.setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX){
                    return null;
                }
                else {
                    return super.formatLabel(value, isValueX) + " %";
                }
            }
        });

        Viewport viewport = mGraphViewPerformance.getViewport();
        viewport.setScrollable(true);
        viewport.setScalable(true);

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(-2);
        viewport.setMaxX(5);

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-10);
        viewport.setMaxY(110);
    }

    private void setupSpinners(){
        mSpinnerChapter.setItems("All Chapters", "Chapter1", "Chapter2", "Chapter3");
        mSpinnerStudent.setItems(studentsList);
        mSpinnerDifficulty.setItems("All Difficulties", 1, 2, 3, 4, 5);

        mSpinnerChapter.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mChapterFilter = item.toString();
                updateGraph();
            }
        });

        mSpinnerStudent.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mStudentFilter = item.toString();
                updateGraph();
            }
        });

        mSpinnerDifficulty.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mDifficultyFilter = item.toString();
                updateGraph();
            }
        });
    }

    private void updateGraph(){
        Toast.makeText(PerformanceActivity.this, "updateGraph", Toast.LENGTH_SHORT).show();
        mGraphViewPerformance.removeAllSeries();
        generateSeries();
    }

    private void generateSeries(){
        int size = studentsList.size();

        DataPoint[] rightPoints = new DataPoint[size];
        DataPoint[] wrongPoints = new DataPoint[size];

        for (int i = 0; i < size; i++){
            int score = scores.get(studentsList.get(i));
            DataPoint rightData = new DataPoint(i + 1, score);
            DataPoint wrongData = new DataPoint(i + 1, 100 - score);
            rightPoints[i] = rightData;
            wrongPoints[i] = wrongData;
        }

        BarGraphSeries<DataPoint> rightSeries = new BarGraphSeries<>(rightPoints);
        rightSeries.setColor(Color.GREEN);
        rightSeries.setAnimated(true);
        rightSeries.setTitle("Right");
        rightSeries.setSpacing(50);
        rightSeries.setDrawValuesOnTop(true);
        rightSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), "Student: " + studentsList.get((int) dataPoint.getX()) + "\n" + "Right: " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        BarGraphSeries<DataPoint> wrongSeries = new BarGraphSeries<>(wrongPoints);
        wrongSeries.setColor(Color.RED);
        wrongSeries.setAnimated(true);
        wrongSeries.setTitle("Wrong");
        wrongSeries.setSpacing(50);
        wrongSeries.setDrawValuesOnTop(true);
        wrongSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), "Student: " + studentsList.get((int) dataPoint.getX()) + "\n" + "Wrong: " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        mGraphViewPerformance.addSeries(rightSeries);
        mGraphViewPerformance.addSeries(wrongSeries);
    }
}

