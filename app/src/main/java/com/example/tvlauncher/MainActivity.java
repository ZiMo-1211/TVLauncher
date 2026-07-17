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
    private TextView btnKeystone;
    private TextView btnMiracast;
    private TextView btnSignalSource;
    private TextView btnMyApps;
    private TextView btnSettings;

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

    /**
     * 绑定布局文件中所有视图引用
     */
    private void initViews() {
        gridRecyclerView = findViewById(R.id.grid_recycler_view);
        tvDateTime = findViewById(R.id.tv_datetime);
        btnKeystone = findViewById(R.id.btn_keystone);
        btnMiracast = findViewById(R.id.btn_miracast);
        btnSignalSource = findViewById(R.id.btn_signal_source);
        btnMyApps = findViewById(R.id.btn_my_apps);
        btnSettings = findViewById(R.id.btn_settings);
    }

    /**
     * 配置四列应用网格的 RecyclerView 及其适配器
     *
     * 每个条目的点击事件通过 AppInfo 中存储的包名委托给 {@link #launchApp(String)}
     */
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

    /**
     * 加载默认应用列表到网格中
     *
     * 通过 PackageManager 解析每个应用的图标。
     * 若应用未安装则依次降级：启动器自身图标 → 系统默认图标。
     */
    private void loadDefaultApps() {
        PackageManager pm = getPackageManager();

        String[][] defaultApps = {
                {"NETFLIX", "com.netflix.mediaclient"},
                {"YouTube", "com.google.android.youtube"},
                {"Google Play", "com.android.vending"},
                {"Chrome", "com.android.chrome"}
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

    /**
     * 根据包名尝试启动应用
     *
     * 解析策略：
     * 1. 通过 PackageManager 验证应用是否已安装
     * 2. 获取标准启动 Intent（ACTION_MAIN + CATEGORY_LAUNCHER）
     * 3. 降级方案——遍历清单中所有 Activity 并启动第一个
     *
     * @param packageName 目标应用的完整包名
     */
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

    /**
     * 在屏幕底部显示短暂停留的 Toast 消息
     *
     * @param msg 要显示的消息文本
     */
    private void showMessage(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    /**
     * 配置底部五个导航按钮：
     * Keystone、Miracast、Signal Source、MyApps 和 Settings
     *
     * 每个按钮包含：
     * - 点击监听，绑定各自对应的操作
     * - 焦点变化监听，提供视觉反馈（缩放 + 高亮）
     * - 水平方向焦点遍历顺序（从左到右）
     */
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

        TextView[] buttons = {btnKeystone, btnMiracast, btnSignalSource, btnMyApps, btnSettings};
        for (TextView btn : buttons) {
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

    /**
     * 更新时钟 TextView 显示当前日期和时间
     *
     * 格式："h:mm a 'Saturday,' MMMM d"（例如 "8:08 AM Saturday, November 11"）
     * 首次在 onCreate 中调用，之后通过 Handler 每 10 秒循环刷新
     */
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
