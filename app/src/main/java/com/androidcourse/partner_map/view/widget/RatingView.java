package com.androidcourse.partner_map.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.androidcourse.partner_map.R;

public class RatingView extends View {
    private float percentage = 0f;
    private Paint bgPaint;
    private Paint fgPaint;
    private Paint textPaint;
    private RectF rectF;
    private int fgColor;
    private float strokeWidth;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        fgColor = context.getResources().getColor(R.color.md_theme_primary, null);
        strokeWidth = 12f;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatingView);
            fgColor = a.getColor(R.styleable.RatingView_rv_progressColor, fgColor);
            strokeWidth = a.getDimension(R.styleable.RatingView_rv_strokeWidth, strokeWidth);
            a.recycle();
        }

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(0xFFE0E0E0);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);

        fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaint.setColor(fgColor);
        fgPaint.setStyle(Paint.Style.STROKE);
        fgPaint.setStrokeWidth(strokeWidth);
        fgPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(fgColor);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    public void setPercentage(float percentage) {
        this.percentage = Math.max(0, Math.min(1, percentage));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight());
        float padding = strokeWidth;
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        rectF.set(centerX - size / 2f + padding, centerY - size / 2f + padding,
                centerX + size / 2f - padding, centerY + size / 2f - padding);

        canvas.drawArc(rectF, 0, 360, false, bgPaint);
        float sweep = 360 * percentage;
        if (sweep > 0) {
            canvas.drawArc(rectF, -90, sweep, false, fgPaint);
        }

        String text = Math.round(percentage * 100) + "%";
        textPaint.setColor(fgColor);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = centerY - (fm.ascent + fm.descent) / 2;
        canvas.drawText(text, centerX, textY, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        if (size == 0) size = 200;
        setMeasuredDimension(size, size);
    }
}