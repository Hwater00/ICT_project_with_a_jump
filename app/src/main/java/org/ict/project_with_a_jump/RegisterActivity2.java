package org.ict.project_with_a_jump;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class RegisterActivity2 extends AppCompatActivity {
    EditText phoneNumberAccess;
    EditText Access;
    Button authentication;
    Button authenticationCheck;
    Button nextbutton;
    String phoneNumber;

    //인증번호
    static final int SMS_SEND_PERMISSOW = 1;
    String checkNum;

    /**
     * @param len:생성한 난수의 길이
     * @poram dupCd:중복 허용 여부
     */
    public static String numberGen(int len, int dupCd) {

        Random rand = new Random();
        String numStr = "";

        for (int i = 0; i < len; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            if (dupCd == 1) {
                numStr += ran;
            } else if (dupCd == 2) {
                if (!numStr.contains(ran)) {
                    numStr += ran;
                } else {
                    i -= 1;
                }
            }
        }
        return numStr;
    }


    private void sendSMS(String phoneNumber, String message) {
        PendingIntent pi = PendingIntent.getActivities(this, 0,
                new Intent[]{new Intent(this, RegisterActivity2.class)}, 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        Toast.makeText(getBaseContext(), "문자 메세지를 확인해주세요", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        phoneNumberAccess = findViewById(R.id.phoneNumberAccess);
        Access = findViewById(R.id.Access);
        authentication = findViewById(R.id.authentication);
        authenticationCheck = findViewById(R.id.authenticationCheck);
        nextbutton = findViewById(R.id.nextbutton);

        /***
         * 문자 보내기 권한 확인
         */
        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissonCheck != PackageManager.PERMISSION_GRANTED) {
            //보내기 권한 거부
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(getApplicationContext(), "SMS 권한이 필요합니다", Toast.LENGTH_SHORT).show();

            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSOW);
        }


        authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNum = numberGen(4, 1);
                sendSMS(phoneNumberAccess.getText().toString(), "인증번호" + checkNum);

            }
        });


        //인증번호 일치 확인 버튼
        authenticationCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "인증번호를 확인하였습니다", Toast.LENGTH_SHORT).show();
            }
        });


        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Access.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity2.this, "인증번호를 입력하세요.", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent2 = new Intent(RegisterActivity2.this, RegisterActivity.class);
                    intent2.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent2);
                }

            }
        });


    }



}