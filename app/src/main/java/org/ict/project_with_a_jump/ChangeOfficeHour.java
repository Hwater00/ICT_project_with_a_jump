package org.ict.project_with_a_jump;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeOfficeHour extends Fragment {
    Button save;
    EditText mon1, mon2;
    EditText tue1, tue2;
    EditText wed1, wed2;
    EditText thu1, thu2;
    EditText fri1, fri2;
    EditText sat1, sat2;
    EditText sun1, sun2;

    String[] days = new String[14];
    String[] dayName = {"mon1", "mon2", "tue1", "tue2", "wed1", "wed2", "thu1", "thu2", "fri1", "fri2", "sat1", "sat2", "sun1", "sun2"};
    String[] title = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
    boolean[] result = new boolean[7];

    DatabaseReference rootRef;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_change_office_hour, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        save = view.findViewById(R.id.save);
        mon1 = view.findViewById(R.id.mon1);
        mon2 = view.findViewById(R.id.mon2);
        tue1 = view.findViewById(R.id.tue1);
        tue2 = view.findViewById(R.id.tue2);
        wed1 = view.findViewById(R.id.wed1);
        wed2 = view.findViewById(R.id.wed2);
        thu1 = view.findViewById(R.id.thu1);
        thu2 = view.findViewById(R.id.thu2);
        fri1 = view.findViewById(R.id.fri1);
        fri2 = view.findViewById(R.id.fri2);
        sat1 = view.findViewById(R.id.sat1);
        sat2 = view.findViewById(R.id.sat2);
        sun1 = view.findViewById(R.id.sun1);
        sun2 = view.findViewById(R.id.sun2);

        //저장된 값 불러오기
        takeInfo(mon1, mon2, dayName[0], dayName[1]);
        takeInfo(tue1, tue2, dayName[2], dayName[3]);
        takeInfo(wed1, wed2, dayName[4], dayName[5]);
        takeInfo(thu1, thu2, dayName[6], dayName[7]);
        takeInfo(fri1, fri2, dayName[8], dayName[9]);
        takeInfo(sat1, sat2, dayName[10], dayName[11]);
        takeInfo(sun1, sun2, dayName[12], dayName[13]);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edittext값 가져오기
                days[0] = mon1.getText().toString();
                days[1] = mon2.getText().toString();

                days[2] = tue1.getText().toString();
                days[3] = tue2.getText().toString();

                days[4] = wed1.getText().toString();
                days[5] = wed2.getText().toString();

                days[6] = thu1.getText().toString();
                days[7] = thu2.getText().toString();

                days[8] = fri1.getText().toString();
                days[9] = fri2.getText().toString();

                days[10] = sat1.getText().toString();
                days[11] = sat2.getText().toString();

                days[12] = sun1.getText().toString();
                days[13] = sun2.getText().toString();

                //영업 시작, 종료 시간이 모두 입력되거나 모두 입력되지 않은 경우(휴무)에만 데이터 저장
                for (int i = 0; i < days.length; i += 2) {
                    if (((days[i].equals("")) && (days[i + 1].equals(""))) || (!(days[i].equals(""))) && (!(days[i + 1].equals("")))) {
                        result[i / 2] = true;
                        saveInfo(days[i], days[i + 1], dayName[i], dayName[i + 1], title[i / 2]);
                    } else {
                        result[i / 2] = false;
                    }
                }

                for (int j = 0; j < result.length; j++) {
                    if (result[j] == false) {
                        Toast.makeText(getContext(), "영업 시작 시간과 종료 시간을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(getContext(), "영업시간을 변경했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //저장된 영업시간 불러오기
    public void takeInfo(EditText editText1, EditText editText2, String key1, String key2) {
        SharedPreferences pref = getActivity().getSharedPreferences("officeTime", Context.MODE_PRIVATE);
        String result1 = pref.getString(key1, "default");
        String result2 = pref.getString(key2, "default");
        if (result1 != "default") {
            editText1.setText(result1);
            editText2.setText(result2);
        }
    }

    //영업시간 수정(한 요일의 오픈/종료 시간 같이 저장)
    public void saveInfo(String input1, String input2, String key1, String key2, String day) {
        //SharedPreferences에 저장
        SharedPreferences pref = getActivity().getSharedPreferences("officeTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key1, input1);
        editor.putString(key2, input2);
        editor.commit();

        //파이어베이스에 저장
        SharedPreferences receivedPref = getActivity().getSharedPreferences("companyInfo", Context.MODE_PRIVATE);
        String companyName = receivedPref.getString("placeName", "default");

        rootRef = FirebaseDatabase.getInstance().getReference("project_with_a_jump").child("ManageAccount");
        databaseReference= rootRef.child(companyName).child("officeHour").child(day);

        Time schedule = new Time();
        schedule.setOpen(input1);
        schedule.setClosed(input2);
        databaseReference.setValue(schedule);
    }

}