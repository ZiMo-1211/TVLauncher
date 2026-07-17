package com.example.tvlauncher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllAppsActivity extends AppCompatActivity {

    private RecyclerView appsRecyclerView;
    private TextView tvTitle;
    private TextView btnBack;

    private AllAppsAdapter adapter;
    private List<AppInfo> allAppsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        initViews();
        setupRecyclerView();
        loadAllApps();
        setupBackButton();
    }

    private void initViews() {
        appsRecyclerView = findViewById(R.id.apps_recycler_view);
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        tvTitle.setText("全部应用");
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        appsRecyclerView.setLayoutManager(layoutManager);

        adapter = new AllAppsAdapter(this, allAppsList, appInfo -> {
            launchApp(appInfo.getPackageName());
        });

        appsRecyclerView.setAdapter(adapter);
        appsRecyclerView.setFocusable(true);
        appsRecyclerView.setFocusableInTouchMode(true);
    }

    private void loadAllApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        allAppsList.clear();

        for (ApplicationInfo appInfo : installedApps) {
            boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            String packageName = appInfo.packageName;
            String appName = pm.getApplicationLabel(appInfo).toString();
            Drawable icon = pm.getApplicationIcon(appInfo);

            allAppsList.add(new AppInfo(appName, packageName, icon, isSystemApp));
        }

        Collections.sort(allAppsList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        adapter.updateData(allAppsList);
        appsRecyclerView.requestFocus();
    }

    private void launchApp(String packageName) {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
                finish();
            } else {
                android.widget.Toast.makeText(this, "无法启动: " + packageName,
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "启动失败: " + e.getMessage(),
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());

        btnBack.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setBackgroundResource(R.drawable.bg_focus);
                v.setScaleX(1.1f);
                v.setScaleY(1.1f);
            } else {
                v.setBackgroundResource(0);
                v.setScaleX(1.0f);
                v.setScaleY(1.0f);
            }
        });

        appsRecyclerView.setNextFocusUpId(btnBack.getId());
        btnBack.setNextFocusDownId(appsRecyclerView.getId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
