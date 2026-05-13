package com.androidcourse.partner_map.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.WebSocketManager;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.view.activity.LoginActivity;

public class PartnerMapApplication extends Application {
    private static final String TAG = "PartnerMap";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
        } catch (Exception e) {
            Log.e(TAG, "AMap privacy update failed", e);
        }

        String token = SharedPreferencesUtil.getInstance(this).getString(Constants.KEY_TOKEN, null);
        if (token != null && !token.isEmpty()) {
            ApiClient.getInstance().setToken(token);
            Log.d(TAG, "Token restored from prefs");
        } else {
            Log.d(TAG, "No saved token found");
        }

        ApiClient.getInstance().setAuthFailureListener(() -> {
            Log.w(TAG, "Auth failure detected, clearing session");
            SharedPreferencesUtil.getInstance(this).clear();
            WebSocketManager.getInstance().disconnect();
            Toast.makeText(this, "登录已过期，请重新登录", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
