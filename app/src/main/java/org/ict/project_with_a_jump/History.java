package org.ict.project_with_a_jump;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class History extends Fragment {
    //Toolbar toolbar;
    FragmentGraph1 fragment1;
    FragmentGraph2 fragment2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragment1 = new FragmentGraph1();
        fragment2 = new FragmentGraph2();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment1).commit();

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("이번 달 기록"));
        tabs.addTab(tabs.newTab().setText("월별 기록"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
                int position = tab.getPosition();
                if (position == 0) {
                    transaction2.replace(R.id.container, fragment1).commit();
                } else if (position == 1) {
                    transaction2.replace(R.id.container, fragment2).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}