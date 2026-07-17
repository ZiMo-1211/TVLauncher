package com.example.tvlauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

        Drawable icon = appInfo.getIcon();
        if (icon != null) {
            int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
            Bitmap reflectionBitmap = ReflectionHelper.createReflectionBitmap(
                    icon, 0.5f, iconSize, iconSize);
            if (reflectionBitmap != null) {
                holder.appIcon.setImageBitmap(reflectionBitmap);
            } else {
                holder.appIcon.setImageDrawable(icon);
            }
        } else {
            try {
                Drawable defaultIcon = context.getPackageManager().getApplicationIcon(
                        context.getPackageName());
                int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
                Bitmap reflectionBitmap = ReflectionHelper.createReflectionBitmap(
                        defaultIcon, 0.5f, iconSize, iconSize);
                if (reflectionBitmap != null) {
                    holder.appIcon.setImageBitmap(reflectionBitmap);
                } else {
                    holder.appIcon.setImageDrawable(defaultIcon);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setBackgroundResource(R.drawable.bg_focus);
                v.setScaleX(1.08f);
                v.setScaleY(1.08f);
            } else {
                v.setBackgroundResource(0);
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
        ImageView appIcon;
        TextView appName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
        }
    }
}
