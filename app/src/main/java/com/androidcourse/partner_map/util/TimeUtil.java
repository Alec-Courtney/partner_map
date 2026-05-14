package com.androidcourse.partner_map.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM月dd日", Locale.CHINA);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private static final SimpleDateFormat API_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private static final SimpleDateFormat CHAT_DATE_FORMAT = new SimpleDateFormat("M月d日", Locale.CHINA);

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatTime(long timestamp) {
        return TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        return DATETIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatChatDate(long timestamp) {
        return CHAT_DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatRelative(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / 60000;
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        long hours = minutes / 60;
        if (hours < 24) return hours + "小时前";
        long days = hours / 24;
        if (days < 30) return days + "天前";
        return formatDate(timestamp);
    }

    public static String formatScheduledRelative(long timestamp) {
        long diff = timestamp - System.currentTimeMillis();
        if (diff <= 0) {
            return "已开始";
        }
        long minutes = diff / 60000;
        if (minutes < 60) {
            return minutes + "分钟后";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "小时后";
        }
        long days = hours / 24;
        if (days < 30) {
            return days + "天后";
        }
        return formatDate(timestamp);
    }

    public static String toApiDateTime(long timestamp) {
        return API_DATETIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1) {
            return String.format(Locale.CHINA, "%.0fm", distanceKm * 1000);
        }
        return String.format(Locale.CHINA, "%.1fkm", distanceKm);
    }

    public static String formatDistanceMeters(float distanceMeters) {
        if (distanceMeters < 1000F) {
            return String.format(Locale.CHINA, "%.0fm", distanceMeters);
        }
        return String.format(Locale.CHINA, "%.1fkm", distanceMeters / 1000F);
    }
}
