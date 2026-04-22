package com.androidcourse.partner_map.model;

import com.androidcourse.partner_map.R;

public enum Category {
    STUDY(0, "学习", "#2196F3", R.drawable.ic_category_study),
    SPORT(1, "运动", "#4CAF50", R.drawable.ic_category_sport),
    FOOD(2, "美食", "#FF9800", R.drawable.ic_category_food),
    TRAVEL(3, "出行", "#9C27B0", R.drawable.ic_category_travel),
    FUN(4, "娱乐", "#E91E63", R.drawable.ic_category_fun),
    SHOPPING(5, "购物", "#FFC107", R.drawable.ic_category_shopping);

    public final int code;
    public final String label;
    public final String color;
    public final int iconRes;

    Category(int code, String label, String color, int iconRes) {
        this.code = code;
        this.label = label;
        this.color = color;
        this.iconRes = iconRes;
    }

    public static Category fromCode(int code) {
        for (Category c : values()) {
            if (c.code == code) return c;
        }
        return STUDY;
    }
}
