package com.sendkoin.customer.Payment;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * Created by warefhaque on 6/24/17.
 */

public class TextDrawable extends Drawable {

  private final String text;
  private final Paint paint;

  public TextDrawable(String text, Activity activity) {
    this.text = text;
    this.paint = new Paint();
    paint.setColor(Color.parseColor("#757575"));
    int spSize = 70;
    float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        spSize, activity.getResources().getDisplayMetrics());
    paint.setTextSize(scaledSizeInPixels);
    paint.setAntiAlias(true);
    paint.setTextAlign(Paint.Align.LEFT);
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawText(text, 0, 6, paint);
  }

  @Override
  public void setAlpha(int alpha) {
    paint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    paint.setColorFilter(cf);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }
}