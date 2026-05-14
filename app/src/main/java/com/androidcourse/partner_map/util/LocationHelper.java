package com.androidcourse.partner_map.util;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class LocationHelper {
    private static final String AUTH_FAIL_MARKER = "INVALID_USER_SCODE";

    public interface LocationCallback {
        void onLocationResult(double lat, double lng);
        void onLocationError(String error);
    }

    private final Context context;
    private AMapLocationClient locationClient;

    public LocationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public void getCurrentLocation(LocationCallback callback) {
        try {
            locationClient = new AMapLocationClient(context);
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
            option.setNeedAddress(true);
            locationClient.setLocationOption(option);
            locationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation location) {
                    if (location != null && location.getErrorCode() == 0) {
                        callback.onLocationResult(location.getLatitude(), location.getLongitude());
                    } else {
                        String error = location != null ? location.getLocationDetail() : "定位失败";
                        callback.onLocationError(normalizeErrorMessage(error));
                    }
                    stopLocation();
                }
            });
            locationClient.startLocation();
        } catch (Exception e) {
            callback.onLocationError(normalizeErrorMessage(e.getMessage()));
        }
    }

    public void stopLocation() {
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    private String normalizeErrorMessage(String rawError) {
        if (rawError == null || rawError.trim().isEmpty()) {
            return "定位失败，请稍后重试";
        }
        if (rawError.contains(AUTH_FAIL_MARKER)) {
            return "定位失败：当前安装包的高德地图签名未授权，请检查应用包名和 SHA1 配置";
        }
        return rawError;
    }
}
