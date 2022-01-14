package org.ict.project_with_a_jump;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentGraph2 extends Fragment implements View.OnClickListener {
    DatabaseReference rootRef;
    DatabaseReference databaseReference;

    Button button, clean;
    TextView term;
    ArrayList values = new ArrayList(); //그래프 데이터 값
    ArrayList days = new ArrayList(); //그래프 x축 라벨
    String findDate = null;
    String writeDate = null;
    int y, m;
    int monthSum = 0;
    int index;
    private LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment_graph2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //새로고침
        clean = view.findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().detach(FragmentGraph2.this).commit();
                getParentFragmentManager().beginTransaction().attach(FragmentGraph2.this).commit();
            }
        });

        button = view.findViewById(R.id.chooseTerm);
        button.setOnClickListener(this);

        lineChart = view.findViewById(R.id.lineChart);

        //기간 기본 설정
        term = view.findViewById(R.id.term);
        Calendar cal = Calendar.getInstance();
        int startYear = cal.get(Calendar.YEAR) - 1;
        int startMonth = cal.get(Calendar.MONTH);
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH);

        String str1 = startYear + "년 " + startMonth + "월";
        String str2 = endYear + "년 " + endMonth + "월";
        term.setText("선택된 기간: " + str1 + "~" + str2);

        SharedPreferences receivedPref = getActivity().getSharedPreferences("companyInfo", Context.MODE_PRIVATE);
        String companyName = receivedPref.getString("placeName", "default");

        /* 월별 데이터 가져와서 그래프 그리기 */
        rootRef = FirebaseDatabase.getInstance().getReference("project_with_a_jump").child("ManageList");
        databaseReference= rootRef.child(companyName);

        initialize();
        showMonthChart(startYear, startMonth, endYear, endMonth);
    }

    //'기간 선택' 버튼 누르면 다이얼로그 뜸
    @Override
    public void onClick(View view) {
        PeriodDialog periodDialog = new PeriodDialog(getContext(), new PeriodDialog.PeriodDialogListener() {
            @Override
            public void close(int startYear, int startMonth, int endYear, int endMonth) {
                initialize();
                //선택된 값으로 그래프 그리기
                showMonthChart(startYear, startMonth, endYear, endMonth);
            }
        });
        periodDialog.setUpTerm(term);
    }

    /* 그래프 그리기 */
    public void showMonthChart(int firstYear, int firstMonth, int lastYear, int lastMonth) {
        index = 0;

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년MM월");

        y = firstYear; //2021
        m = firstMonth; //08

        Calendar cal = Calendar.getInstance();

        //해당 기간동안 데이터 가져오기 반복
        while ((y < lastYear) || ((y == lastYear) && (m <= lastMonth))) {
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, m-1);

            findDate = sdf2.format(cal.getTime());
            writeDate = sdf1.format(cal.getTime());

            days.add(writeDate); //x축 라벨 추가
            databaseReference.child(findDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        //'oooo년oo월'의 모든 일을 돌아가며 총합 계산
                        for (DataSnapshot myDataSnapshot : snapshot.getChildren()) {
                            long count = myDataSnapshot.getChildrenCount();
                            monthSum += count;
                        }
                        values.add(new BarEntry(monthSum, index)); //데이터 값 추가
                        drawChart(values, days);
                        monthSum = 0;
                        index++;
                    } else {
                        values.add(new BarEntry(0, index)); //데이터 값 추가
                        drawChart(values, days);
                        index++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

            m++;
            if (m >= 13) {
                m = 1;
                y += 1;
            }

            cal.clear(Calendar.YEAR);
            cal.clear(Calendar.MONTH);
        }
    }

    //values와 days로 라인 그래프 그리기
    public void drawChart(ArrayList values, ArrayList days) {

        LineDataSet lineDataSet = new LineDataSet(values, "월별 방문자 수");
        lineDataSet.setCircleColor(Color.rgb(63,81,181)); //그래프 포인트(?값) 색
        lineDataSet.setColor(Color.rgb(63,81,181)); //그래프 색
        lineDataSet.setCircleSize(3.5f);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(16f);
        lineDataSet.setLineWidth(2f);

        LineData lineData = new LineData(days, lineDataSet);
        lineChart.setData(lineData);

        //데이터값 float->int
        ValueFormatter vf = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "" + (int) value;
            }
        };
        lineData.setValueFormatter(vf);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(true);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(12f);

        //오른쪽 y축 비활성화
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true); //차트 터치x
        lineChart.setPinchZoom(false);
        lineChart.setVisibleXRange(5, 5);
        lineChart.setExtraOffsets(5f, 10f, 20f, 15f);
        lineChart.moveViewToX(1);
        lineChart.setDescription("년도/월");
        lineChart.setDescriptionTextSize(12f);
        lineChart.invalidate();
    }

    //기존 그래프 초기화
    public void initialize() {
        values.clear();
        days.clear();
        lineChart.invalidate();
        lineChart.clear();
    }
}