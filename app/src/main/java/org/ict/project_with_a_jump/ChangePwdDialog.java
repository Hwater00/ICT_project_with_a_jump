package org.ict.project_with_a_jump;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePwdDialog extends DialogFragment {
    TextView confirm1;
    TextView confirm2;
    EditText beforePwd;
    EditText afterPwd1;
    EditText afterPwd2;
    Button button_confirm;
    Button button_cancel;
    private Fragment fragment;

    DatabaseReference rootRef;
    DatabaseReference databaseReference;

    public ChangePwdDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_change_pwd_dialog, container, false);

        confirm1 = view.findViewById(R.id.confirm1);
        confirm2 = view.findViewById(R.id.confirm2);
        button_confirm = view.findViewById(R.id.button_confirm);
        button_cancel = view.findViewById(R.id.button_cancel);
        beforePwd = view.findViewById(R.id.beforePwd);
        afterPwd1 = view.findViewById(R.id.afterPwd1);
        afterPwd2 = view.findViewById(R.id.afterPwd2);

        Bundle args = getArguments();
        String value = args.getString("key");

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");

        if (fragment != null) {
            DialogFragment dialogFragment = (DialogFragment) fragment;

            SharedPreferences receivedPref = getActivity().getSharedPreferences("companyInfo", Context.MODE_PRIVATE);
            String companyName = receivedPref.getString("placeName", "default");

            rootRef = FirebaseDatabase.getInstance().getReference();
            databaseReference = rootRef.child("project_with_a_jump").child("ManageAccount").child(companyName);

            //기존 비밀번호 일치 여부 확인
            confirm1.setOnClickListener(new View.OnClickListener() {
                String savedPwd;

                @Override
                public void onClick(View v) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot != null) {
                                ManageAccount user = snapshot.getValue(ManageAccount.class);
                                savedPwd = user.getPassword();

                                //기존 비밀번호와 일치하면 새 비밀번호 입력 edittext 활성화
                                String inputPwd = beforePwd.getText().toString();
                                if (inputPwd.equals(savedPwd)) {
                                    confirm2.setText("확인되었습니다.\n새 비밀번호를 설정해주세요.");
                                    setEditPossible(afterPwd1);
                                    setEditPossible(afterPwd2);
                                } else {
                                    confirm2.setText("일치하지 않습니다.\n다시 입력해주세요.");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            });

            //다이얼로그 '취소' 버튼
            button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogFragment.dismiss();
                }
            });

            //다이얼로그 '완료' 버튼
            button_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newPwd1 = afterPwd1.getText().toString();
                    String newPwd2 = afterPwd2.getText().toString();

                    if (newPwd1.equals("")) {
                        Toast.makeText(getContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (newPwd1.equals(newPwd2)) {
                        //새로운 비밀번호 업데이트
                        Toast.makeText(getContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updatePassword(newPwd1);
                        databaseReference.child("password").setValue(newPwd1); //새로운 비밀번호 파이어베이스에 저장
                        dialogFragment.dismiss();

                        Intent intent = new Intent(getActivity(), MainActivity2.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        return view;

    }

    public void setEditPossible(EditText editText) {
        editText.setFocusable(true);
        editText.setClickable(true);
        editText.setEnabled(true);
        editText.setFocusableInTouchMode(true);
    }
}