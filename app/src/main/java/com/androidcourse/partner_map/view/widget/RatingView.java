package com.androidcourse.partner_map.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RatingView extends View {
    private float percentage = 0f;
    private final Paint bgPaint;
    private final Paint fgPaint;
    private final RectF rectF;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(0xFFE0E0E0);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(12);

        fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fgPaint.setColor(0xFF4CAF50);
        fgPaint.setStyle(Paint.Style.STROKE);
        fgPaint.setStrokeWidth(12);
        fgPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    public void setPercentage(float percentage) {
        this.percentage = Math.max(0, Math.min(1, percentage));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = 12;
        float size = Math.min(getWidth(), getHeight()) - padding * 2;
        rectF.set(padding, padding, padding + size, padding + size);

        canvas.drawArc(rectF, 0, 360, false, bgPaint);
        float sweep = 360 * percentage;
        canvas.drawArc(rectF, -90, sweep, false, fgPaint);
    }
}
