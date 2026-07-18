package com.example.tvlauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppGridAdapter extends RecyclerView.Adapter<AppGridAdapter.ViewHolder> {

    private Context context;
    private List<AppInfo> appList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppInfo appInfo);
    }

    public AppGridAdapter(Context context, List<AppInfo> appList, OnItemClickListener listener) {
        this.context = context;
        this.appList = appList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.appName.setText(appInfo.getName());

        // 1. 设置应用图标
        Drawable icon = appInfo.getIcon();
        if (icon != null) {
            holder.appIcon.setImageDrawable(icon);
        } else {
            try {
                holder.appIcon.setImageDrawable(
                        context.getPackageManager().getApplicationIcon(context.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // 2. 获取卡片颜色并设置圆角背景
        String pkg = appInfo.getPackageName();
        int cardColor = Color.TRANSPARENT;

        if ("com.netflix.mediaclient".equals(pkg)) {
            cardColor = Color.parseColor("#000000"); // 黑
        } else if ("com.google.android.youtube".equals(pkg)) {
            cardColor = Color.parseColor("#3C3C3C"); // 深灰
        } else if ("com.android.vending".equals(pkg)) {
            cardColor = Color.parseColor("#8BC34A"); // 绿
        } else if ("com.android.chrome".equals(pkg)) {
            cardColor = Color.parseColor("#9C27B0"); // 紫
        }

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(30f);
        shape.setColor(cardColor);
        holder.cardContainer.setBackground(shape);

        // 3. 生成文字倒影 (将背景色铺满卡片大小的画布)
        String text = appInfo.getName();
        if (text != null && !text.isEmpty()) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(holder.appName.getTextSize());
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);

            float textWidth = paint.measureText(text);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float textHeight = fm.descent - fm.ascent;

            // ================= 关键改动 =================
            // 不再让画布紧贴文字大小，而是生成一个大背景块（模拟卡片宽度）
            int mirrorWidth = 400; // 模拟卡片宽度，会被后续的 fitXY 拉伸填满卡片
            int mirrorHeight = 80; // 足够高的纯色背景容纳文字
            Bitmap textBitmap = Bitmap.createBitmap(mirrorWidth, mirrorHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(textBitmap);

            // 绘制全宽的卡片背景色
            canvas.drawColor(cardColor);

            // 将文字居中绘制在这块大背景上
            float x = mirrorWidth / 2f;
            float y = mirrorHeight / 2f - (fm.ascent + fm.descent) / 2f;
            canvas.drawText(text, x, y, paint);
            // ===========================================

            // 调用 ReflectionHelper 生成带有渐变底部的完整图片
            Bitmap fullReflection = ReflectionHelper.createReflectionBitmap(textBitmap, 0.8f);

            // 安全截取翻转的镜像部分 (无闪退逻辑)
            int startY = textBitmap.getHeight();
            int targetHeight = fullReflection.getHeight() - startY;

            if (targetHeight > 0) {
                Bitmap reflectionOnly = Bitmap.createBitmap(fullReflection, 0, startY, fullReflection.getWidth(), targetHeight);
                holder.appNameReflection.setImageBitmap(reflectionOnly);
            } else {
                holder.appNameReflection.setImageBitmap(null);
            }

        } else {
            holder.appNameReflection.setImageBitmap(null);
        }

        // 4. 焦点变化监听
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.cardContainer.setBackgroundResource(R.drawable.bg_focus);
                v.setScaleX(1.08f);
                v.setScaleY(1.08f);
            } else {
                holder.cardContainer.setBackground(shape);
                v.setScaleX(1.0f);
                v.setScaleY(1.0f);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(appInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList != null ? appList.size() : 0;
    }

    public void updateData(List<AppInfo> newList) {
        this.appList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardContainer;
        ImageView appIcon;
        TextView appName;
        ImageView appNameReflection;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.card_container);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appNameReflection = itemView.findViewById(R.id.app_name_reflection);
        }
    }
}
