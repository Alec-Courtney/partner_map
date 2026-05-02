package com.androidcourse.partner_map.app;

import android.app.Application;

import com.amap.api.location.AMapLocationClient;

public class PartnerMapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
