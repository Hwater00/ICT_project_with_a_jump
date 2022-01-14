package org.ict.project_with_a_jump;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InitActivity extends AppCompatActivity {

    private Button userBtn;
    private Button manageBtn;
    private boolean saveData;
    private SharedPreferences runData; // 최초 실행 정보 저장
    private SharedPreferences choiceData;  // 버튼 선택 정보 저장
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        userBtn = findViewById(R.id.userBtn);
        manageBtn = findViewById(R.id.manageBtn);

        // 앱 첫 실행이 아닌 경우, 바로 사용자 유형별(사용자, 사업자) 화면 연결
        runData = getSharedPreferences("runData", MODE_PRIVATE);
        choiceData = getSharedPreferences("choiceData", MODE_PRIVATE);
        editor = choiceData.edit();

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                editor.putString("select", "userBtn");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), CertifyActivity.class);
                startActivity(intent);
            }
        });

        manageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                editor.putString("select", "manageBtn");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

        checkFirstRun();
    }


    private void checkFirstRun() {
        boolean isFirstRun = runData.getBoolean("isFirstRun", true);

        editor = choiceData.edit();

        if (isFirstRun) {
            runData.edit().putBoolean("isFirstRun", false).apply();

        } else {
            switch (choiceData.getString("select", "")) {
                case "userBtn":
                    userBtn.callOnClick();
                    break;

                case "manageBtn":
                    manageBtn.callOnClick();
                    break;
            }
        }
    }


}