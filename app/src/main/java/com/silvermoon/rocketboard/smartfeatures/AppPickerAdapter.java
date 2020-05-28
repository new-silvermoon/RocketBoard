package com.silvermoon.rocketboard.smartfeatures;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.silvermoon.rocketboard.R;
import java.util.List;

/**
 * Created by Sagar_Das01 on 9/18/2017.
 */
public class AppPickerAdapter extends RecyclerView.Adapter<AppPickerAdapter.ViewHolder> {

    public List<PackageInfo> packageList;
    PackageInfo packageInfo;
    Drawable appIcon;
    String appName;
    PackageManager packageManager;

    public interface OnItemClickListener {
        public void onItemClick(PackageInfo item);
    }

    private OnItemClickListener listener;

    public AppPickerAdapter(List<PackageInfo> packageList, PackageManager packageManager, OnItemClickListener listener) {
        this.packageList = packageList;
        this.packageManager = packageManager;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvAppName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAppName = (TextView)itemView.findViewById(R.id.tvAppName);
        }

        public void bind(final PackageInfo item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }


    }

    @Override
    public AppPickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_activities_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        packageInfo = (PackageInfo)packageList.get(position);
        appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        appIcon.setBounds(0,0,90,90);


        holder.tvAppName.setCompoundDrawables(appIcon,null,null,null);
        holder.tvAppName.setCompoundDrawablePadding(15);
        holder.tvAppName.setText(appName);
        holder.tvAppName.setTextSize(20.0f);

        holder.bind(packageList.get(position), listener);


    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }
}




