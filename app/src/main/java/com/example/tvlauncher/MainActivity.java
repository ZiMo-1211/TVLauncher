package com.example.tvlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView gridRecyclerView;
    private TextView tvDateTime;

    // 👇 关键改动：将原来的 TextView 改为 View，解决 ClassCastException 崩溃
    private View btnKeystone;
    private View btnMiracast;
    private View btnSignalSource;
    private View btnMyApps;
    private View btnSettings;

    private AppGridAdapter adapter;
    private List<AppInfo> appList = new ArrayList<>();

    private Handler clockHandler = new Handler();
    private Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            updateDateTime();
            clockHandler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        loadDefaultApps();
        setupBottomButtons();
        updateDateTime();

        clockHandler.post(clockRunnable);

        gridRecyclerView.requestFocus();
    }

    private void initViews() {
        gridRecyclerView = findViewById(R.id.grid_recycler_view);
        tvDateTime = findViewById(R.id.tv_datetime);
        // 👇 findViewById 会精确找到外层线性布局，返回 View
        btnKeystone = findViewById(R.id.btn_keystone);
        btnMiracast = findViewById(R.id.btn_miracast);
        btnSignalSource = findViewById(R.id.btn_signal_source);
        btnMyApps = findViewById(R.id.btn_my_apps);
        btnSettings = findViewById(R.id.btn_settings);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        gridRecyclerView.setLayoutManager(layoutManager);

        adapter = new AppGridAdapter(this, appList, appInfo -> {
            launchApp(appInfo.getPackageName());
        });

        gridRecyclerView.setAdapter(adapter);
        gridRecyclerView.setFocusable(true);
        gridRecyclerView.setFocusableInTouchMode(true);
    }

    private void loadDefaultApps() {
        PackageManager pm = getPackageManager();

        String[][] defaultApps = {
                {"NETFLIX", "com.netflix.mediaclient"},
                {"YouTube", "com.google.android.youtube"},
                {"Google Play", "com.android.vending"},
                {"chrome", "com.android.chrome"}
        };

        appList.clear();

        for (String[] app : defaultApps) {
            String name = app[0];
            String packageName = app[1];
            Drawable icon = null;

            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                icon = pm.getApplicationIcon(appInfo);
            } catch (PackageManager.NameNotFoundException e) {
                try {
                    icon = pm.getApplicationIcon(getPackageName());
                } catch (PackageManager.NameNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (icon == null) {
                icon = getResources().getDrawable(android.R.drawable.sym_def_app_icon);
            }

            appList.add(new AppInfo(name, packageName, icon, false));
        }

        adapter.updateData(appList);
    }

    private void launchApp(String packageName) {
        try {
            PackageManager pm = getPackageManager();

            try {
                pm.getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                showMessage(packageName + " 未安装");
                return;
            }

            Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
                return;
            }

            PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (pkgInfo.activities != null) {
                for (ActivityInfo activity : pkgInfo.activities) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName(packageName, activity.name);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                }
            }

            showMessage(packageName + " 已安装但无可用启动入口");
        } catch (Exception e) {
            showMessage("启动失败: " + e.getMessage());
        }
    }

    private void showMessage(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void setupBottomButtons() {
        btnKeystone.setOnClickListener(v -> launchApp("com.keystone.app"));
        btnMiracast.setOnClickListener(v -> launchApp("com.miracast.app"));
        btnSignalSource.setOnClickListener(v -> launchApp("com.signal.source"));

        btnMyApps.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllAppsActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
                showMessage("无法打开设置");
            }
        });

        View[] buttons = {btnKeystone, btnMiracast, btnSignalSource, btnMyApps, btnSettings};
        for (View btn : buttons) {
            btn.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.bg_focus);
                    v.setScaleX(1.1f);
                    v.setScaleY(1.1f);
                } else {
                    v.setBackgroundResource(R.drawable.bg_button);
                    v.setScaleX(1.0f);
                    v.setScaleY(1.0f);
                }
            });
        }

        for (int i = 0; i < buttons.length; i++) {
            if (i > 0) {
                buttons[i].setNextFocusLeftId(buttons[i - 1].getId());
            } else {
                buttons[i].setNextFocusLeftId(buttons[i].getId());
            }
            if (i < buttons.length - 1) {
                buttons[i].setNextFocusRightId(buttons[i + 1].getId());
            } else {
                buttons[i].setNextFocusRightId(buttons[i].getId());
            }
        }

        gridRecyclerView.setNextFocusDownId(btnKeystone.getId());
    }

    private void updateDateTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a 'Saturday,' MMMM d", Locale.US);
        String dateTimeStr = sdf.format(now);
        tvDateTime.setText(dateTimeStr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clockHandler.removeCallbacks(clockRunnable);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            gridRecyclerView.requestFocus();
        }
    }
}
