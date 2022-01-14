package org.ict.project_with_a_jump;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {
    Button registerButton;
    Button loginButton;
    FirebaseAuth firebaseAuth; //firebase 인스턴스 선언
    private Button login;
    private EditText email_login;
    private EditText pwd_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        login = findViewById(R.id.login);
        email_login = findViewById(R.id.email_login);
        pwd_login = findViewById(R.id.pwd_login);

        //firebaseauth 인스턴스 초기화
        firebaseAuth = FirebaseAuth.getInstance();

        //버튼 누름
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //text를 문자열로 가져옴

                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();

                boolean isGoToLogin = true;

                //체크하기
                if (email.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    isGoToLogin = false;
                }

                if (pwd.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "비밀번호 입력하세요.", Toast.LENGTH_SHORT).show();
                    isGoToLogin = false;
                }

                //signinwithemailandpassword 메서드 사용
                //addOnCompleteListener, onComplete 메서드 사용

                if (isGoToLogin) {
                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(MainActivity2.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity2.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity2.this, HomeDefault.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity2.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        registerButton = findViewById(R.id.사업자register);

        //setonclicklistener :버튼 클릭 이벤트 처리
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}