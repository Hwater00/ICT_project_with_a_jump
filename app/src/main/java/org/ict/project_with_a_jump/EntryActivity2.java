package org.ict.project_with_a_jump;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class EntryActivity2 extends Activity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<EntryList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    TextView Today, NowLocation;
    EditText UserNum;

    //현재 시간
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss");
    String formatDate = dateFormat.format(date);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.listViewEntry);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        Today = (TextView) findViewById(R.id.txDate);
        UserNum = findViewById(R.id.UserNum);
        NowLocation = findViewById(R.id.txDate);

        //시간 출력
        NowLocation.setText(formatDate);

        database = FirebaseDatabase.getInstance();

        //intent
        Intent intent = getIntent();
        String user_num = intent.getStringExtra("user_num");
        UserNum.setText(user_num);
        String gpsData = intent.getStringExtra("gpsData");
        NowLocation.setText(gpsData);


        databaseReference = database.getReference("EntryList");
        /*
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EntryList entry = snapshot.getValue(EntryList.class);
                    arrayList.add(entry);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity2", String.valueOf(databaseError.toException()));
            }
        });

        adapter = new EntryAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

         */
    }
}
