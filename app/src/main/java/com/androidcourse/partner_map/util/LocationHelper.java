package com.androidcourse.partner_map.util;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class LocationHelper {
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
                        callback.onLocationError(error);
                    }
                    stopLocation();
                }
            });
            locationClient.startLocation();
        } catch (Exception e) {
            callback.onLocationError(e.getMessage());
        }
    }

    public void stopLocation() {
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationClient = null;
        }
    }
}
