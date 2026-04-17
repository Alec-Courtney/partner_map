package com.androidcourse.partner_map.util;

import android.graphics.Color;

import com.androidcourse.partner_map.model.Category;

public class CategoryHelper {
    public static int getColor(int categoryCode) {
        try {
            return Color.parseColor(Category.fromCode(categoryCode).color);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

    public static String getLabel(int categoryCode) {
        return Category.fromCode(categoryCode).label;
    }

    public static int getIconRes(int categoryCode) {
        return Category.fromCode(categoryCode).iconRes;
    }

    public static String getStatusLabel(int status) {
        switch (status) {
            case 0: return "招募中";
            case 1: return "已满员";
            case 2: return "已结束";
            default: return "未知";
        }
    }
}
