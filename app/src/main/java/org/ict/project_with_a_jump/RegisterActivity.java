package org.ict.project_with_a_jump;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {
    //인증번호
    static final int SMS_SEND_PERMISSOW = 1;
    //다음 api
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    Button nextbutton, registerbutton, authentication, authenticationCheck;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private EditText email_join, pwd_join;
    private EditText name_join, birth_join, daum_resultDetail_join, companyName_join;
    private TextView daum_result_join, daum_result2_join, daum_result3_join;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Boolean nextPage; // 위치 인증 확인
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private Button gpsCheck; // 위치 인증 버튼
    private Boolean gpsCheckData = false;
    private GpsTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_join = findViewById(R.id.email_join);
        pwd_join = findViewById(R.id.pwd_join);
        name_join = findViewById(R.id.name_join);
        birth_join = findViewById(R.id.birth_join2);
        daum_result_join = findViewById(R.id.daum_result_join);
        daum_result2_join = findViewById(R.id.daum_result2_join);
        daum_result3_join = findViewById(R.id.daum_result3_join);
        daum_resultDetail_join = findViewById(R.id.daum_resultDetail_join);
        companyName_join = findViewById(R.id.companyName_join);
        registerbutton = findViewById(R.id.registerbutton);
        nextbutton = findViewById(R.id.nextbutton);
        authentication = findViewById(R.id.authentication);
        authenticationCheck = findViewById(R.id.authenticationCheck);
        gpsCheck = findViewById(R.id.gpsCheck);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("project_with_a_jump").child("ManageAccount");


        //다음 api
        if (daum_result_join != null) {
            daum_result_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RegisterActivity.this, WebViewActivity.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });
        }

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gpsCheckData == false) {
                    isCheckGps("register");
                } else {
                    register();
                }
            }
        });

//        문자 보내기 권한 확인 (코드 구현중!!!)
//        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
//        if (permissonCheck != PackageManager.PERMISSION_GRANTED) {
//            //보내기 권한 거부
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
//                Toast.makeText(getApplicationContext(), "SMS 권한이 필요합니다", Toast.LENGTH_SHORT).show();
//
//            }
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSOW);
//        }
//
//        authentication.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkNum = numberGen(4,1);
//
//                sendSMS(phoneNumberAccess.getText().toString(),"인증번호"+checkNum);
//            }
//        });
//
//        //인증번호 일치 확인 버튼
//        authenticationCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"인증번호를 확인하였습니다",Toast.LENGTH_SHORT).show();
//            }
//        });


        //!!!!파베 이미지 업로드할 때 데이터 전달
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextPage) {
                    if (gpsCheckData == false) {
                        isCheckGps("next");

                    } else {
                        Intent intent2 = new Intent(RegisterActivity.this, RegisterActivity3.class);
                        intent2.putExtra("name", name_join.getText().toString());
                        intent2.putExtra("companyName", companyName_join.getText().toString());
                        startActivity(intent2);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "계정을 먼저 생성하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 위치 인증 버튼 처리
        gpsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkLocationServicesStatus()) { // GPS 활성화가 되어 있지 않다면
                    showDialogForLocationServiceSetting();
                } else {
                    checkRunTimePermission();

                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                    dialog.setTitle("이 주소가 맞나요?").setMessage(getGpsTracker());

                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            gpsCheckData = true;
                        }
                    });

                    dialog.setNegativeButton("넘어가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            gpsCheckData = false;
                        }
                    });

                    dialog.setNeutralButton("재시도", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.setMessage(getGpsTracker());
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                        }
                    });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void register() {
        //회원가입 처리 시작
        String email = email_join.getText().toString();
        String pwd = pwd_join.getText().toString();
        String name = name_join.getText().toString();
        String birth = birth_join.getText().toString();
        String daum1 = daum_result_join.getText().toString();
        String daum2 = daum_resultDetail_join.getText().toString();
        String daum3 = daum_result2_join.getText().toString();
        String daum4 = daum_result3_join.getText().toString();
        String companyName = companyName_join.getText().toString();

        //체크하기
        Boolean isGoToJoin = true;

        if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (pwd.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (birth.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "생년월일을 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (daum1.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "시설우편번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (daum2.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "시설 세부주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (companyName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "시설 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            isGoToJoin = false;
        }
        if (pwd.length() < 6) {
            Toast.makeText(RegisterActivity.this, "비밀번호를 6자리 이상으로 입력해주세요", Toast.LENGTH_LONG).show();
            isGoToJoin = false;
        }


        if (isGoToJoin) {
            //Firebase Auth 진행
            mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        ManageAccount account = new ManageAccount();
                        account.setIdToken(firebaseUser.getUid());
                        account.setEmailId(firebaseUser.getEmail());
                        account.setPassword(pwd);
                        account.setName(name);
                        account.setBirth(birth);
                        account.setDaum1(daum1);
                        account.setDaum2(daum2);
                        account.setDaum3(daum3);
                        account.setDaum4(daum4);
                        account.setCompanyName(companyName);

                        // 위치 인증을 통해 얻은 사업장 주소
                        if (gpsCheckData == false) {
                            account.setGpsData("비어있음");
                        } else {
                            account.setGpsData(getGpsTracker());
                        }

                        //setvalue(): database에 insert(삽입)
                        mDatabaseRef.child(account.getGpsData()).setValue(account);
                        mDatabaseRef.child(companyName).setValue(account);

                        SharedPreferences pref = getSharedPreferences("companyInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("placeName", account.getCompanyName());
                        editor.commit();

                        Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                        nextPage = true;

                    } else {
                        Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                        nextPage = false;
                    }
                }
            });
            nextPage = true;
        } else {
            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show();
            nextPage = false;
        }
    }

    // 인증 버튼 확인 처리
    private void isCheckGps(String who) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
        dialog.setTitle("위치 인증이 이뤄지지 않았습니다.").setMessage("인증이 이루어져야 명부가 생성됩니다.");

        dialog.setPositiveButton("인증하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                gpsCheck.callOnClick();
            }
        });

        dialog.setNegativeButton("넘어가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                gpsCheckData = false;

                if (who == "register") {
                    register();

                } else if (who == "next") {
                    Intent intent2 = new Intent(RegisterActivity.this, RegisterActivity3.class);
                    intent2.putExtra("name", name_join.getText().toString());
                    intent2.putExtra("companyName", companyName_join.getText().toString());
                    startActivity(intent2);
                }
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
        intent.putExtra("gpsCheckData", gpsCheckData);
    }

    // 현재 위치의 주소 받아오는 메소드
    public String getGpsTracker() {
        gpsTracker = new GpsTracker(RegisterActivity.this);

        String latitude = Double.toString(gpsTracker.getLatitude());
        String longitude = Double.toString(gpsTracker.getLongitude());

        return searchRoadAddress(longitude, latitude); // 경도, 위도 순서
    }

    public String searchRoadAddress(String x, String y) {
        GetJSONObjectTask get = new GetJSONObjectTask();
        String road_address = "";

        try {
            road_address = get.execute().get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return road_address;
    }

    // onRequestPermissionsResult의 결과를 리턴받는 메소드
    public void onRequestPermssionsResult(int permsRequestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 권한이 모두 허용됐는지 확인
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 권한이 모두 설정됨 -> 위치 가져오기 가능
                getGpsTracker();
            } else {
                // 거부된 권한이 있는 경우
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) // 사용자가 이전에 권한을 거절했다면 true 반환
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 권한 거부만 한 경우
                    Toast.makeText(RegisterActivity.this, "위치 권한이 거부되었습니다. 앱을 재실행하여 권한을 허용해주세요.", Toast.LENGTH_LONG).show();

                } else {
                    // "다시 묻지 않음"을 선택하고 권한을 거부한 경우
                    Toast.makeText(RegisterActivity.this, "위치 권한이 거부되었습니다. 설정(앱 정보)을 통해 권한을 허용해주세요. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() { //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음

        } else {  // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(RegisterActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(RegisterActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(RegisterActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... 주소, 지명 => 위도, 경도 좌표로 변호나
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    5);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0) + "\n";
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    //코드 구현중!!!!!
    /*//sms 기능
    private void sendSMS(String phoneNumber, String message)
    {
        PendingIntent pi = PendingIntent.getActivities(this, 0,
                new Intent[]{new Intent(this, SubActivity.class)}, 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        Toast.makeText(getBaseContext(), "문자 메세지를 확인해주세요", Toast.LENGTH_SHORT).show();
    }

    public static String numberGen(int len, int dupCd){

        Random rand = new Random();
        String numStr= "";

        for(int i=0; i<len; i++){
            String ran = Integer.toString(rand.nextInt(10));
            if(dupCd==1){
                numStr+= ran;
            }else if(dupCd==2){
                if(!numStr.contains(ran)){
                    numStr+= ran;
                }else{
                    i-=1;
                }
            }
        }
        return numStr;
    }

    *//*public void onClick_cf(View v) {

        Intent intent_02= new Intent(getApplicationContext(), MainActivity2.class);
        String ph= phoneNumberAccess.getText().toString();
        intent_02.putExtra("입력한 번호",ph);
        startActivity(intent_02);
    }*/


    //다음 api
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    Log.d("data", data);

                    int idx = data.indexOf(",");
                    //data2 = 우편번호
                    String data2 = data.substring(0, idx);
                    Log.d("data2", data2);

                    String cutData = data.substring(idx + 1).trim();
                    Log.d("cutData", cutData);

                    int idx2 = cutData.indexOf("(");
                    String cutData2 = cutData.substring(0, idx2);
                    Log.d("cutData2", cutData2);
                    String cutData3 = cutData.substring(idx2).trim();
                    Log.d("cutData3", cutData3);

                    if (data != null) {
                        daum_result_join.setText(cutData);
                        daum_result2_join.setText(data2);
                        daum_result3_join.setText(cutData3);
                    }
                }
                break;
        }
    }


}
