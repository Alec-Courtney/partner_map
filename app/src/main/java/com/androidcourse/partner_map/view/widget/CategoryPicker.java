package com.androidcourse.partner_map.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Category;

public class CategoryPicker extends LinearLayout {
    private ChipGroup chipGroup;
    private OnCategorySelectedListener listener;
    private int selectedCategory = -1;

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

    public CategoryPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        chipGroup = new ChipGroup(getContext());
        chipGroup.setSingleSelection(true);

        String[] labels = getContext().getResources().getStringArray(R.array.category_labels);
        int[] colors = {
                R.color.category_study,
                R.color.category_sport,
                R.color.category_food,
                R.color.category_travel,
                R.color.category_fun,
                R.color.category_shopping
        };

        for (int i = 0; i < Category.values().length; i++) {
            Category c = Category.values()[i];
            Chip chip = new Chip(getContext());
            chip.setText(c.label);
            chip.setTag(c.code);
            chip.setCheckable(true);
            chip.setId(c.code + 1000);
            chipGroup.addView(chip);
        }

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip checkedChip = group.findViewById(checkedId);
                if (checkedChip != null && listener != null) {
                    selectedCategory = (Integer) checkedChip.getTag();
                    listener.onCategorySelected(selectedCategory);
                }
            }
        });

        addView(chipGroup);
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedCategory(int categoryCode) {
        this.selectedCategory = categoryCode;
        Chip chip = chipGroup.findViewById(categoryCode + 1000);
        if (chip != null) {
            chip.setChecked(true);
        }
    }
}