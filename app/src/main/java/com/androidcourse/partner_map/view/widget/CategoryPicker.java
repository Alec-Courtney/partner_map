package com.androidcourse.partner_map.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Category;

public class CategoryPicker extends LinearLayout {
    private RadioGroup radioGroup;
    private OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(int code);
    }

    public CategoryPicker(Context context) {
        super(context);
        init();
    }

    public CategoryPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(HORIZONTAL);

        for (Category c : Category.values()) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(c.label);
            rb.setTag(c.code);
            radioGroup.addView(rb);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (rb != null && listener != null) {
                listener.onCategorySelected((Integer) rb.getTag());
            }
        });

        addView(radioGroup);
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedCategory(int categoryCode) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(i);
            if (((Integer) button.getTag()) == categoryCode) {
                button.setChecked(true);
                return;
            }
        }
    }
}
