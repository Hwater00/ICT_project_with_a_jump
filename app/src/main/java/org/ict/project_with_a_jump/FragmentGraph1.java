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
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
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
import java.util.Date;
import java.util.Locale;

public class FragmentGraph1 extends Fragment {
    DatabaseReference rootRef;
    DatabaseReference databaseReference;

    TextView nowDate1, nowDate2, total;
    BarDataSet barDataSet;
    BarData barData;
    ArrayList values = new ArrayList(); //그래프 데이터 값
    ArrayList days = new ArrayList(); //그래프 x축 라벨
    int totalNumber = 0;
    private BarChart barChart;

    Button clean;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment_graph1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //새로고침
        clean = view.findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getParentFragmentManager().beginTransaction().detach(FragmentGraph1.this).commit();
               getParentFragmentManager().beginTransaction().attach(FragmentGraph1.this).commit();
            }
        });

        /* 해당 년도/월 나옴 */
        nowDate1 = view.findViewById(R.id.nowDate1);
        nowDate2 = view.findViewById(R.id.nowDate2);

        Calendar cal = Calendar.getInstance(new Locale("en", "US"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년\n  M월");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년");
        SimpleDateFormat sdf2 = new SimpleDateFormat("M월");
        nowDate1.setText(sdf1.format(cal.getTime()));
        nowDate2.setText(sdf2.format(cal.getTime()));

        /* 그래프 */
        barChart = view.findViewById(R.id.barChart);
        //오늘 날짜에 해당하는 데이터 가져오기
        long today = System.currentTimeMillis();
        Date date = new Date(today);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy년MM월");
        //SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd일");
        String month = dateFormat1.format(date); //이번 달

        values.clear();
        days.clear();
        barChart.invalidate();
        barChart.clear();
        totalNumber = 0;

        SharedPreferences receivedPref = getActivity().getSharedPreferences("companyInfo", Context.MODE_PRIVATE);
        String companyName = receivedPref.getString("placeName", "default");

        /* 일별 방문자 수 파악, 그래프에 반영하기 */
        total = view.findViewById(R.id.total); //총 방문자 수
        rootRef = FirebaseDatabase.getInstance().getReference("project_with_a_jump").child("ManageList");
        databaseReference= rootRef.child(companyName);
        databaseReference.child(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int index = 0;
                if (snapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : snapshot.getChildren()) {
                        long count = myDataSnapshot.getChildrenCount();
                        values.add(new BarEntry((int) count, index));
                        days.add(myDataSnapshot.getKey());
                        index++;
                        totalNumber += (int) count;
                        total.setText(totalNumber + "명");
                    }
                    showChart(values, days);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    public void showChart(ArrayList values, ArrayList days) {
        barDataSet = new BarDataSet(values, "일별 방문자 수");

        barData = new BarData(days, barDataSet);
        barDataSet.setColor(Color.rgb(63,81,181));
        barDataSet.setValueTextSize(16f);
        barChart.setData(barData);

        //데이터값 float->int
        ValueFormatter vf = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "" + (int) value;
            }
        };
        barData.setValueFormatter(vf);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
        xAxis.setEnabled(true);
        xAxis.setTextSize(12f);

        //y축
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);

        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setDescription(null);
        barChart.setVisibleXRangeMinimum(2);
        barChart.setVisibleXRangeMaximum(7);
        barChart.moveViewToX(7);
        barChart.invalidate();
    }}