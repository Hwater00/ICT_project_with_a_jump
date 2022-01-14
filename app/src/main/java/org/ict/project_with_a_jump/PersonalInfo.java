package org.ict.project_with_a_jump;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalInfo extends Fragment {
    DatabaseReference rootRef;
    DatabaseReference databaseReference;

    TextView showName;
    TextView showBirth;
    TextView showEmailId;
    TextView showCompanyName;
    TextView showAddress1;
    TextView showAddress2;
    Button changePassword;
    Button logout;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_personal_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showName = view.findViewById(R.id.name);
        showBirth = view.findViewById(R.id.birth);
        showEmailId = view.findViewById(R.id.email);
        changePassword = view.findViewById(R.id.changePassword);
        showCompanyName = view.findViewById(R.id.placeName);
        showAddress1 = view.findViewById(R.id.address1);
        showAddress2 = view.findViewById(R.id.address2);
        changePassword = view.findViewById(R.id.changePassword);
        logout = view.findViewById(R.id.logout);

        //파이어베이스로부터 개인 정보 가져오기
        SharedPreferences pref = getActivity().getSharedPreferences("companyInfo", Context.MODE_PRIVATE);
        String companyName = pref.getString("placeName", "default");

        //파이어베이스로부터 개인 정보 가져오기
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference("project_with_a_jump").child("ManageAccount");
        databaseReference = rootRef.child(companyName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    ManageAccount userAccount = snapshot.getValue(ManageAccount.class);
                    String name = userAccount.getName();
                    String birth = userAccount.getBirth();
                    String emailId = userAccount.getEmailId();
                    String companyName = userAccount.getCompanyName();
                    String address1 = userAccount.getDaum1();
                    String address2 = userAccount.getDaum2();

                    showName.setText(name + " 님");
                    showEmailId.setText(emailId);
                    showBirth.setText(birth);
                    showCompanyName.setText(companyName);
                    showAddress1.setText(address1);
                    showAddress2.setText(address2);
                } else {
                    showName.setText("오류");
                    showEmailId.setText("오류");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //비밀번호 재설정 다이얼로그 띄우기
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("key", "value");
                ChangePwdDialog changePwdDialog = new ChangePwdDialog();
                changePwdDialog.setArguments(args);
                changePwdDialog.show(getActivity().getSupportFragmentManager(), "tag");
            }
        });

        //로그아웃
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity2.class);
                startActivity(intent);
            }
        });
    }
}