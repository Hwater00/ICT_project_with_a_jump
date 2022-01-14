package org.ict.project_with_a_jump;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity4 extends AppCompatActivity {
    Button nextbutton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register4);

        nextbutton4 = findViewById(R.id.nextbutton4);
        nextbutton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity4.this, RegisterActivity5.class);
                startActivity(intent);
            }
        });
    }
}