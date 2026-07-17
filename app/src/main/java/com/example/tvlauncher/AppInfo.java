package com.example.tvlauncher;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isSystemApp;
    private String webUrl;

    public AppInfo(String name, String packageName, Drawable icon, boolean isSystemApp) {
        this(name, packageName, icon, isSystemApp, null);
    }

    public AppInfo(String name, String packageName, Drawable icon, boolean isSystemApp, String webUrl) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.isSystemApp = isSystemApp;
        this.webUrl = webUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
