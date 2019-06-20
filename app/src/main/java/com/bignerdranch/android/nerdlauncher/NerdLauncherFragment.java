package com.bignerdranch.android.nerdlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {
    public static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.app_recycler_view);
        int mNoOfColumns = calculateNoOfColumns(getActivity().getApplicationContext(), 160);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mNoOfColumns));

        setupAdapter();
        return v;
    }

    public int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
    }

    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString());
            }
        });
        Log.i(TAG, "Found " + activities.size() + " activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder {
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;
        private ImageView mIconImageView;

        public ActivityHolder(View itemView) {
            super(itemView);
            final MediaPlayer click = MediaPlayer.create(getActivity(), R.raw.click_sound_effect);
            mNameTextView = itemView.findViewById(R.id.app_name);
            mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    click.start();

                    ActivityInfo activityInfo = mResolveInfo.activityInfo;

                    Intent i = new Intent(Intent.ACTION_MAIN)
                            .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
                }
            });
            mIconImageView = itemView.findViewById(R.id.app_icon);
            mIconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    click.start();

                    ActivityInfo activityInfo = mResolveInfo.activityInfo;

                    Intent i = new Intent(Intent.ACTION_MAIN)
                            .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
                }
            });
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            mNameTextView.setText(appName);
            Drawable icon = getAppIconByPackageName(appName);
            mIconImageView.setImageDrawable(icon);
        }


    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.recyclerview_item, parent, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

    public Drawable getAppIconByPackageName(String ApkTempPackageName){
        Drawable drawable;

        try {
            drawable = getActivity().getPackageManager().getApplicationIcon(ApkTempPackageName);

        } catch (PackageManager.NameNotFoundException e) {
            drawable = ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher);
        }

        return drawable;
    }
}
