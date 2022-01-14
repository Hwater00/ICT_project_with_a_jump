package org.ict.project_with_a_jump;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    // fine_location : coarse보다 더 정확한 위치 제공, coarse_location : 도시 블록 내에 위치 정확성 제공

    private boolean saveData;
    String facilityName = "";
    private SharedPreferences checkData;

    private GpsTracker gpsTracker;
    private TextView textView_notice;
    private TextView user_name;
    private TextView welcome;
    private EditText editText;
    private Button ShowLocationButton;
    private Button FindLocationButton;
    private Button changeBtn;
    private Button listBtn;
    private Button modifyBtn;
    private CheckBox auto_check;

    String num;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 사용자의 현재 위치 도로명주소
        user_name = findViewById(R.id.user_name); // 사용자명
        welcome = findViewById(R.id.welcome);
        editText = findViewById(R.id.editText);

        ShowLocationButton = findViewById(R.id.show); // 시설 찾기
        FindLocationButton = findViewById(R.id.find); // 직접 검색
        changeBtn = findViewById(R.id.changeBtn); // 계정 유형 변경
        listBtn = findViewById(R.id.listBtn); // 명부 입장 기록 리스트
        modifyBtn = findViewById(R.id.modifyBtn); // 사용자 정보 수정
        auto_check = findViewById(R.id.auto_check); // 자동 입장 체크


        if (!checkLocationServicesStatus()) { // GPS 활성화가 되어 있지 않다면
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }


        // 본인인증 화면으로부터 사용자 정보 받아오기
        Intent intentFromCertification = getIntent();
        user_name.setText(intentFromCertification.getStringExtra("user_name"));
        num = intentFromCertification.getStringExtra("user_num");// 개인안심번호
        address = intentFromCertification.getStringExtra("user_address");


        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 사업자 데이터 연결
        //DatabaseReference refer = reference.child("project_with_a_jump").child("ManageAccount");

        /*
        refer.child(getGpsTracker()).child("companyName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);

                if (value != null) {
                    facilityName = value;

                } else {
                    facilityName = "not found";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
                */


        // 시설 입장
        ShowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((facilityName).equals("not found")) {
                    Toast.makeText(MainActivity.this, "현재 위치에 해당하는 시설을 찾을 수 없습니다.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "투썸플레이스 김포풍무점"+ " 입장", Toast.LENGTH_LONG).show();

                    // 해당 시설의 명부 화면으로 이동
                    Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                    intent.putExtra("facilityName", "투썸플레이스 김포풍무점");
                    intent.putExtra("user_num", num);
                    intent.putExtra("user_name", user_name.getText().toString());
                    intent.putExtra("user_address", address);
                    // intent.putExtra("check_data", saveData);
                    startActivityForResult(intent, 0);
                }
            }
        });

        // 직접 검색
        FindLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();

                if (input.equals("")) { // 입력칸이 빈칸이라면
                    Toast.makeText(MainActivity.this, "시설명을 입력해주세요", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "투썸플레이스 김포풍무점"+ " 입장", Toast.LENGTH_LONG).show();

                    // 해당 시설의 명부 화면으로 이동
                    Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
                    intent.putExtra("facilityName", "투썸플레이스 김포풍무점");
                    intent.putExtra("user_num", num);
                    intent.putExtra("user_name", user_name.getText().toString());
                    intent.putExtra("user_address", address);
                    // intent.putExtra("check_data", saveData);
                    startActivityForResult(intent, 0);
                }
            }

        });

        // 계정 유형 전환 처리
        changeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // 사업자 로그인 화면으로 이동
                Intent change = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(change);
            }
        });

        // 사용자가 입장한 시설 명부 기록 확인
        listBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), EntryActivity2.class);
                intent.putExtra("user_num", num);
                intent.putExtra("gpsData", "경기 김포시 풍무로 83");
                startActivityForResult(intent, 0);
            }
        });

        // 사용자 정보 수정 처리
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), ReCertiftActivity.class);
                intent.putExtra("user_num", num);
                intent.putExtra("user_name", user_name.getText().toString());
                intent.putExtra("user_address", address);
                startActivity(intent);
            }
        });


        // 체크박스 값 유지
        checkData = getSharedPreferences("checkData", MODE_PRIVATE);
        load();

        if (saveData) {
            auto_check.setChecked(saveData);
        }

        auto_check.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auto_check.isChecked()) { // 자동 입장을 사용자가 선택했다면
                    ShowLocationButton.callOnClick();
                    save();
                } else {
                    save();
                }
            }
        });
    }

    public Boolean isChange (String who) {
        if (!who.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    // 주소나 사용자의 입력값을 통해 시설명을 찾아주는 함수
    public void findFacility(String data) {
        if (data.equals("뿡")) {
            findFacility(getGpsTracker());

        }

    }

    private void load() { // 설정값 호출
        saveData = checkData.getBoolean("SAVE_CHECK_DATA", false);

        if (saveData) {
            ShowLocationButton.callOnClick(); // 시설 찾기 버튼의 이벤트가 실행됨
        }
    }

    private void save() { // 설정값 저장
        SharedPreferences.Editor editor = checkData.edit();

        editor.putBoolean("SAVE_CHECK_DATA", auto_check.isChecked());
        editor.apply();
    }

    // 뒤로가기 제어
   // @Override
    //public void onBackPressed() {
      //  AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle("정말로 종료하시겠습니까?");

        //alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
          //  @Override
           // public void onClick(DialogInterface dialog, int which) {

            //}
        //});
        //alert.setNegativeButton("종료", new DialogInterface.OnClickListener() {
          //  @Override
           // public void onClick(DialogInterface dialog, int which) {
             //   finishAffinity();
            //}
        //});

        //AlertDialog alert2 = alert.create();
        //alert2.show();
    //}

    // 현재 위치의 주소 받아오는 메소드
    public String getGpsTracker() {
        gpsTracker = new GpsTracker(MainActivity.this);

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
                    Toast.makeText(MainActivity.this, "위치 권한이 거부되었습니다. 앱을 재실행하여 권한을 허용해주세요.", Toast.LENGTH_LONG).show();

                } else {
                    // "다시 묻지 않음"을 선택하고 권한을 거부한 경우
                    Toast.makeText(MainActivity.this, "위치 권한이 거부되었습니다. 설정(앱 정보)을 통해 권한을 허용해주세요. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() { //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음

        } else {  // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}