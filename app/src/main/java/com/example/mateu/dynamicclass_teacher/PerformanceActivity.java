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
import java.util.List;

public class PerformanceActivity extends AppCompatActivity {
    private static final String TAG = "PerformanceActivity";

    private GraphView mGraphViewPerformance;
    private MaterialSpinner mSpinnerChapter;
    private MaterialSpinner mSpinnerStudent;
    private MaterialSpinner mSpinnerDifficulty;

    static final String ALL_CHAPTERS = "All Chapters";
    static final String ALL_STUDENTS = "All Students";
    static final String ALL_DIFFICULTIES = "All Difficulties";

    //starting values
    private String mChapterFilter = ALL_CHAPTERS;
    private String mStudentFilter = ALL_STUDENTS;
    private String mDifficultyFilter = ALL_DIFFICULTIES;

    private int mChapterIndex = 1;
    private int mStudentIndex = 1;
    private int mDifficultyIndex = 1;

    private PerformanceAdapter mPerformanceAdapter;

    private List<String> mChapterList;
    private List<String> mStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        Bundle bundle = getIntent().getExtras();
        String subjectCode = bundle.getString("subjectCode");

        mPerformanceAdapter = new PerformanceAdapter(subjectCode);

        mGraphViewPerformance = findViewById(R.id.graphViewPerformance);
        mSpinnerChapter = findViewById(R.id.spinnerChapter);
        mSpinnerStudent = findViewById(R.id.spinnerStudent);
        mSpinnerDifficulty = findViewById(R.id.spinnerDifficulty);

        setupGraph();
        setupSpinners();
        readData();
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
                    return super.formatLabel(value, false) + " %";
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
        mChapterList = mPerformanceAdapter.getListOfChaptersName();
        mStudentList = mPerformanceAdapter.getListOfStudentsName();

        List<String> spinnerChapterList = new ArrayList<>(mChapterList);
        spinnerChapterList.add(ALL_CHAPTERS);
        mSpinnerChapter.setItems(spinnerChapterList);

        List<String> spinnerStudentList = new ArrayList<>(mStudentList);
        spinnerStudentList.add(ALL_STUDENTS);
        mSpinnerStudent.setItems(spinnerStudentList);

        mSpinnerDifficulty.setItems(1, 2, 3, 4, 5, ALL_DIFFICULTIES);

        mSpinnerChapter.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mChapterFilter = item.toString();
                mChapterIndex = position;
                mGraphViewPerformance.removeAllSeries();
                setupSpinners();
                readData();
            }
        });

        mSpinnerStudent.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mStudentFilter = item.toString();
                mStudentIndex = position;
                mGraphViewPerformance.removeAllSeries();
                setupSpinners();
                readData();
            }
        });

        mSpinnerDifficulty.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mDifficultyFilter = item.toString();
                mDifficultyIndex = position;
                mGraphViewPerformance.removeAllSeries();
                setupSpinners();
                readData();
            }
        });
    }

    private void readData(){
        boolean allChapters = mChapterFilter.equals(ALL_CHAPTERS);
        boolean allStudents = mStudentFilter.equals(ALL_STUDENTS);
        boolean allDifficulties = mDifficultyFilter.equals(ALL_DIFFICULTIES);

//        Toast.makeText(this, "" +
//                        "allChapters: " + allChapters + "\n" +
//                        "allStudents: " + allStudents + "\n" +
//                        "allDifficulties: " + allDifficulties + "\n"
//                , Toast.LENGTH_SHORT).show();

        if (allChapters){
            List<Integer> percentageList;

            if (allStudents){
                if (allDifficulties){
                    percentageList = mPerformanceAdapter.getPercentageOfAllClass();
                }
                else {
                    percentageList = mPerformanceAdapter.getPercentageOfAllClassForDifficulty(mDifficultyIndex);
                }
            }
            else {
                if (allDifficulties){
                    percentageList = mPerformanceAdapter.getPercentageOfRightAnswersForAllChaptersStudent(mStudentIndex);
                }
                else {
                    percentageList = mPerformanceAdapter.getPercentageOfRightAnswersForAllChaptersStudentAndDIfficulty(mStudentIndex, mDifficultyIndex);
                }
            }
            generateSeriesAllChapters(percentageList);

        }
        else {
            int[] results;

            if (allStudents){
                if (allDifficulties){
                    results = mPerformanceAdapter.getRightAndWrongForChapterAllStudents(mChapterIndex);
                }
                else {
                    results = mPerformanceAdapter.getRightAndWrongForChapterAllStudentsAndDifficulty(mChapterIndex, mDifficultyIndex);
                }
            }
            else {
                if (allDifficulties){
                    results = mPerformanceAdapter.getRightAndWrongForChapterStudent(mStudentIndex, mChapterIndex);
                }
                else {
                    results = mPerformanceAdapter.getRightAndWrongForChapterStudentAndDifficulty(mStudentIndex, mChapterIndex, mDifficultyIndex);
                }
            }
            generateSeriesSingleChapter(results);

        }
    }

    private void generateSeriesAllChapters(List<Integer> percentageList){
        int size = percentageList.size();

        DataPoint[] rightPoints = new DataPoint[size];
        DataPoint[] wrongPoints = new DataPoint[size];

        for (int i = 0; i < size; i++){
            int score = percentageList.get(i);
            DataPoint rightData = new DataPoint(i + 1, score);
            DataPoint wrongData = new DataPoint(i + 1, 100 - score);
            rightPoints[i] = rightData;
            wrongPoints[i] = wrongData;
        }

        BarGraphSeries<DataPoint> rightSeries = new BarGraphSeries<>(rightPoints);
        rightSeries.setColor(Color.GREEN);
        rightSeries.setAnimated(true);
        rightSeries.setTitle("Right");
        rightSeries.setSpacing(20);
        rightSeries.setDrawValuesOnTop(true);
        rightSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), "Student: " + mStudentList.get((int) dataPoint.getX() - 1) + "\n" + "Right: " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        BarGraphSeries<DataPoint> wrongSeries = new BarGraphSeries<>(wrongPoints);
        wrongSeries.setColor(Color.RED);
        wrongSeries.setAnimated(true);
        wrongSeries.setTitle("Wrong");
        wrongSeries.setSpacing(20);
        wrongSeries.setDrawValuesOnTop(true);
        wrongSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), "Student: " + mStudentList.get((int) dataPoint.getX() - 1) + "\n" + "Wrong: " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        mGraphViewPerformance.addSeries(rightSeries);
        mGraphViewPerformance.addSeries(wrongSeries);
    }

    private void generateSeriesSingleChapter(int[] results){
        int rights = results[0];
        int wrongs = results[1];
        int total = rights + wrongs;
        int rightsPercentage = rights*100/total;
        int wrongsPercentage = wrongs*100/total;

        DataPoint rightPoints = new DataPoint(0, rightsPercentage);
        DataPoint wrongPoints = new DataPoint(0, wrongsPercentage);

        BarGraphSeries<DataPoint> rightSeries = new BarGraphSeries<>(new DataPoint[]{rightPoints});
        rightSeries.setColor(Color.GREEN);
        rightSeries.setAnimated(true);
        rightSeries.setTitle("Right");
        rightSeries.setSpacing(20);
        rightSeries.setDrawValuesOnTop(true);
        rightSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), "Student: " + mStudentList.get((int) dataPoint.getX() - 1) + "\n" + "Right: " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        BarGraphSeries<DataPoint> wrongSeries = new BarGraphSeries<>(new DataPoint[]{wrongPoints});
        wrongSeries.setColor(Color.RED);
        wrongSeries.setAnimated(true);
        wrongSeries.setTitle("Wrong");
        wrongSeries.setSpacing(20);
        wrongSeries.setDrawValuesOnTop(true);
        wrongSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), "Student: " + mStudentList.get((int) dataPoint.getX() - 1) + "\n" + "Wrong: " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        mGraphViewPerformance.addSeries(rightSeries);
        mGraphViewPerformance.addSeries(wrongSeries);
    }
}

