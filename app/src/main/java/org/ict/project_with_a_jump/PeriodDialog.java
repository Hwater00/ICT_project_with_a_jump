package org.ict.project_with_a_jump;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class PeriodDialog {
    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2021;

    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private final Context context;
    private final PeriodDialogListener listener;
    NumberPicker picker1, picker2, picker3, picker4;
    int pickedValue1, pickedValue2, pickedValue3, pickedValue4;
    Button cancel, confirm;
    Calendar cal;


    public PeriodDialog(@NonNull Context context, PeriodDialogListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setUpTerm(TextView term) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_period_dialog);
        dialog.show();

        cal = Calendar.getInstance();

        //start
        picker1 = (NumberPicker) dialog.findViewById(R.id.startYear);
        picker2 = (NumberPicker) dialog.findViewById(R.id.startMonth);

        picker1.setMinValue(MIN_YEAR);
        picker1.setMaxValue(MAX_YEAR);
        picker1.setValue(cal.get(Calendar.YEAR) - 1);
        picker1.setWrapSelectorWheel(false); //더이상 값 올라가지 않게

        picker2.setMinValue(MIN_MONTH);
        picker2.setMaxValue(MAX_MONTH);
        picker2.setValue(cal.get(Calendar.MONTH));
        picker2.setWrapSelectorWheel(false);

        //end
        picker3 = (NumberPicker) dialog.findViewById(R.id.endYear);
        picker4 = (NumberPicker) dialog.findViewById(R.id.endMonth);

        picker3.setMinValue(MIN_YEAR);
        picker3.setMaxValue(MAX_YEAR);
        picker3.setValue(cal.get(Calendar.YEAR));
        picker3.setWrapSelectorWheel(false); //더이상 값 올라가지 않게

        picker4.setMinValue(MIN_MONTH);
        picker4.setMaxValue(MAX_MONTH);
        picker4.setValue(cal.get(Calendar.MONTH));
        picker4.setWrapSelectorWheel(false);

        //기본 설정
        pickedValue1 = picker1.getValue();
        pickedValue2 = picker2.getValue();
        pickedValue3 = picker3.getValue();
        pickedValue4 = picker4.getValue();

        //term.setText(pickedValue1+"년 "+pickedValue2+"월~"+pickedValue3+"년 "+pickedValue4+"월");

        //'완료', '취소' 버튼 눌렀을 때
        cancel = (Button) dialog.findViewById(R.id.button_cancel);
        confirm = (Button) dialog.findViewById(R.id.button_confirm);

        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int before, int after) {
                pickedValue1 = after;
            }
        });
        picker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int before, int after) {
                pickedValue2 = after;
            }
        });
        picker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int before, int after) {
                pickedValue3 = after;
            }
        });
        picker4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int before, int after) {
                pickedValue4 = after;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((pickedValue1 > pickedValue3) || ((pickedValue1 == pickedValue3) && (pickedValue2 >= pickedValue4))) {
                    Toast.makeText(confirm.getContext(), "종료 날짜를 시작 날짜 이후로 설정해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String msg1 = pickedValue1 + "년 " + pickedValue2 + "월";
                    String msg2 = pickedValue3 + "년 " + pickedValue4 + "월";
                    term.setText("선택된 기간: "+msg1 + "~" + msg2);
                    listener.close(pickedValue1, pickedValue2, pickedValue3, pickedValue4);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public interface PeriodDialogListener {
        void close(int startYear, int startMonth, int endYear, int endMonth);
    }
}