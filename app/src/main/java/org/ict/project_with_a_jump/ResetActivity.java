package org.ict.project_with_a_jump;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ResetActivity extends AppCompatActivity {
    static final int SMS_SEND_PERMISSOW = 1;
    private final String Validation = "^.(?=.*[0-9])(?=.*[0-9ㄱ-ㅎ가-힣]).*$";
    // 회원가입
    Button registerbut;
    // sms
    EditText user_pnumber;
    Button sendSMSBt;
    EditText inputCheckNum;
    Button checkBt;
    // 자동 로그인 처리
    SharedPreferences auto;
    String autoName;
    String autoNum;
    String autoAddress;
    //안증번호 비교를 위한 쉐어드 저장
    String checkNum;
    private EditText user_name, user_ad, user_ce;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private Boolean check = false; // 휴대폰 번호 인증 확인

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        sendSMSBt = findViewById(R.id.send_sms_button);
        user_pnumber = findViewById(R.id.user_pnumber);
        inputCheckNum = findViewById(R.id.input_check_num);
        checkBt = findViewById(R.id.check_button4);

        user_name = findViewById(R.id.user_name);
        user_ad = findViewById(R.id.user_ad);
        user_ce = findViewById(R.id.user_ce);
        registerbut = findViewById(R.id.registerbut);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project_with_a_jump").child("UserAccount");

        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        autoName = auto.getString("autoName", null);
        autoNum = auto.getString("autoNum", null);
        autoAddress = auto.getString("autoAddress", null);


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

        sendSMSBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNum = numberGen(4, 1);
                System.out.println(checkNum);
                sendSMS(user_pnumber.getText().toString(), "인증번호 : " + checkNum);
            }
        });

        registerbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = user_name.getText().toString();
                String num = user_ce.getText().toString();
                String address = user_ad.getText().toString();


                if (name.isEmpty() || num.isEmpty() || address.isEmpty()) {
                    if (name.isEmpty()) {
                        Toast.makeText(ResetActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    }

                    if (address.isEmpty()) {
                        Toast.makeText(ResetActivity.this, "주소를 입력하세요\n예) 서울특별시 도봉구 쌍문2동", Toast.LENGTH_SHORT).show();
                    }

                    if (num.isEmpty()) {
                        Toast.makeText(ResetActivity.this, "개인안심번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    //Firebase 데이터 저장
                    if (check == true) {
                        UserAccount User = new UserAccount();
                        User.setName(name);
                        User.setAddress(address);
                        User.setNum(num);

                        //setvalue(): database에 insert(삽입)
                        mDatabaseRef.child(num).setValue(User);

                        mDatabaseRef.child(num).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot != null) {
                                    Toast.makeText(ResetActivity.this, "계정이 등록되었습니다", Toast.LENGTH_SHORT).show();

                                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = auto.edit();
                                    editor.putString("autoName", name);
                                    editor.putString("autoNum", num);
                                    editor.putString("autoAddress", address);
                                    editor.commit();

                                    // 사용자 홈 화면으로 사용자 기본 정보 전달
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("user_name", name);
                                    intent.putExtra("user_num", num);
                                    intent.putExtra("user_address", address);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(ResetActivity.this, "계정 등록에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    } else {
                        Toast.makeText(ResetActivity.this, "휴대폰 번호 인증이 이뤄지지 않았습니다\n" +
                                "인증을 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //인증번호 일치 확인 버튼
        checkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((checkNum).equals(inputCheckNum.getText().toString())) {
                    check = true;
                    Toast.makeText(getApplicationContext(), "인증번호를 확인하였습니다", Toast.LENGTH_SHORT).show();
                } else {
                    check = false;
                    Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다\n" +
                            "다시 확인해주세요!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /***
     *sms 기능
     * @param phoneNumber
     * @param message
     */

    private void sendSMS(String phoneNumber, String message) {
        Intent intent = new Intent(this, CertifyActivity.class);

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        pi.cancel(); // 새로 열리는 pi 닫기

        Toast.makeText(getBaseContext(), "문자 메세지를 확인해주세요", Toast.LENGTH_SHORT).show();
    }
}