package com.example.tvlauncher;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllAppsAdapter extends RecyclerView.Adapter<AllAppsAdapter.ViewHolder> {

    private Context context;
    private List<AppInfo> appList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppInfo appInfo);
    }

    public AllAppsAdapter(Context context, List<AppInfo> appList, OnItemClickListener listener) {
        this.context = context;
        this.appList = appList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.appName.setText(appInfo.getName());
        holder.appPackage.setText(appInfo.getPackageName());

        Drawable icon = appInfo.getIcon();
        if (icon != null) {
            holder.appIcon.setImageDrawable(icon);
        } else {
            try {
                holder.appIcon.setImageDrawable(
                        context.getPackageManager().getApplicationIcon(context.getPackageName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setBackgroundResource(R.drawable.bg_focus);
                v.setScaleX(1.05f);
                v.setScaleY(1.05f);
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
        TextView appPackage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appPackage = itemView.findViewById(R.id.app_package);
        }
    }
}
