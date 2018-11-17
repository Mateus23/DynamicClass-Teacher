package com.example.mateu.dynamicclass_teacher;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
    private ProgressBar mProgressBar;

    static final String ALL_CHAPTERS = "Todos";
    static final String ALL_STUDENTS = "Todos";
    static final String ALL_DIFFICULTIES = "Todas";

    static final String STUDENTS_LABEL = "Estudante";
    static final String CHAPTERS_LABEL = "Capítulo";
    static final String PERCENTAGE_LABEL = "Pontos (Percentual)";

    static final String RIGHT_LABEL = "Correto";
    static final String WRONG_LABEL = "Incorreto";

    private CountDownTimer mCountDownTimer;

    private PerformanceAdapter mPerformanceAdapter;
    private List<String> mChapterList;
    private List<String> mStudentList;

    //starting values
    private String mChapterFilter = ALL_CHAPTERS;
    private String mStudentFilter = ALL_STUDENTS;
    private String mDifficultyFilter = ALL_DIFFICULTIES;

    private int mChapterIndex = 1;
    private int mStudentIndex = 1;
    private int mDifficultyIndex = 1;

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
        mProgressBar = findViewById(R.id.progressBar);

        waitDataDownload();
    }

    private void waitDataDownload(){
        mCountDownTimer = new CountDownTimer(5000, 500) {
            @Override
            public void onTick(long l) {
                if (mPerformanceAdapter.getIsReady()){
                    setupScreen();
                }
            }

            @Override
            public void onFinish() {
                setupScreen();
            }
        };
        mCountDownTimer.start();
    }

    private void setupScreen(){
        mCountDownTimer.cancel();
        mProgressBar.setVisibility(View.INVISIBLE);
        setupGraph();
        setupSpinners();
        readData();
    }

    private void setupGraph(){
        mGraphViewPerformance.setTitle("Performance");
        mGraphViewPerformance.setTitleTextSize(50);

        LegendRenderer legendRenderer = mGraphViewPerformance.getLegendRenderer();
        legendRenderer.setVisible(true);
        legendRenderer.setAlign(LegendRenderer.LegendAlign.TOP);

        GridLabelRenderer gridLabelRenderer = mGraphViewPerformance.getGridLabelRenderer();
        gridLabelRenderer.setHorizontalAxisTitle(STUDENTS_LABEL);
        gridLabelRenderer.setVerticalAxisTitle(PERCENTAGE_LABEL);
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
        viewport.setMinX(-3);
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
        mSpinnerChapter.setSelectedIndex(spinnerChapterList.size() - 1);

        List<String> spinnerStudentList = new ArrayList<>(mStudentList);
        spinnerStudentList.add(ALL_STUDENTS);

        mSpinnerStudent.setItems(spinnerStudentList);
        mSpinnerStudent.setSelectedIndex(spinnerStudentList.size() - 1);

        mSpinnerDifficulty.setItems(1, 2, 3, 4, 5, ALL_DIFFICULTIES);
        mSpinnerDifficulty.setSelectedIndex(5);

        mSpinnerChapter.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mChapterFilter = item.toString();
                mChapterIndex = position;
                mGraphViewPerformance.removeAllSeries();
                readData();
            }
        });

        mSpinnerStudent.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mStudentFilter = item.toString();
                mStudentIndex = position;
                mGraphViewPerformance.removeAllSeries();
                readData();
            }
        });

        mSpinnerDifficulty.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                mDifficultyFilter = item.toString();
                mDifficultyIndex = position + 1;
                mGraphViewPerformance.removeAllSeries();
                readData();
            }
        });
    }

    private void readData(){
        boolean allChapters = mChapterFilter.equals(ALL_CHAPTERS);
        boolean allStudents = mStudentFilter.equals(ALL_STUDENTS);
        boolean allDifficulties = mDifficultyFilter.equals(ALL_DIFFICULTIES);

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
            DataPoint rightData = new DataPoint(i, score);
            DataPoint wrongData = new DataPoint(i, 100 - score);
            rightPoints[i] = rightData;
            wrongPoints[i] = wrongData;
        }

        BarGraphSeries<DataPoint> rightSeries = new BarGraphSeries<>(rightPoints);
        BarGraphSeries<DataPoint> wrongSeries = new BarGraphSeries<>(wrongPoints);

        setupSeries(rightSeries, wrongSeries, CHAPTERS_LABEL);
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
        BarGraphSeries<DataPoint> wrongSeries = new BarGraphSeries<>(new DataPoint[]{wrongPoints});

        setupSeries(rightSeries, wrongSeries, STUDENTS_LABEL);
    }

    private void setupSeries(BarGraphSeries<DataPoint> rightSeries, BarGraphSeries<DataPoint> wrongSeries, final String label){

        final List<String> nameList;
        if (label.equals(STUDENTS_LABEL)){
            nameList = mStudentList;
        }
        else{
            nameList = mChapterList;
        }

        rightSeries.setColor(Color.GREEN);
        rightSeries.setAnimated(true);
        rightSeries.setTitle(RIGHT_LABEL);
        rightSeries.setSpacing(30);
        rightSeries.setDrawValuesOnTop(true);
        rightSeries.setValuesOnTopColor(Color.BLACK);
        rightSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), label + ": " + nameList.get((int) dataPoint.getX()) + "\n" + RIGHT_LABEL + ": " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        wrongSeries.setColor(Color.RED);
        wrongSeries.setAnimated(true);
        wrongSeries.setTitle(WRONG_LABEL);
        wrongSeries.setSpacing(30);
        wrongSeries.setDrawValuesOnTop(true);
        wrongSeries.setValuesOnTopColor(Color.BLACK);
        wrongSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mGraphViewPerformance.getContext(), label + ": " + nameList.get((int) dataPoint.getX()) + "\n" + WRONG_LABEL + ": " + dataPoint.getY() + "%", Toast.LENGTH_SHORT).show();
            }
        });

        mGraphViewPerformance.getGridLabelRenderer().setHorizontalAxisTitle(label);

        mGraphViewPerformance.addSeries(rightSeries);
        mGraphViewPerformance.addSeries(wrongSeries);
    }
}

