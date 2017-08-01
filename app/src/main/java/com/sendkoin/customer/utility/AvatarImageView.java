package com.sendkoin.customer.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;

import java.util.StringTokenizer;

/**
 * Created by warefhaque on 7/31/17.
 */

public class AvatarImageView extends android.support.v7.widget.AppCompatImageView {

  private int borderWidth;
  private int canvasSize;
  private String name;
  private Bitmap image;
  private Paint paint;
  private Paint paintBackground;
  private Paint paintBorder;
  private TextPaint textPaint;

  private int textColor;
  private int backgroundColor;

  public AvatarImageView(Context context) {
    this(context, null);
  }

  public AvatarImageView(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.avatarImageViewStyle);
  }

  public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    paint = new Paint();
    paint.setAntiAlias(true);

    paintBorder = new Paint();
    paintBorder.setAntiAlias(true);

    paintBackground = new Paint();
    paintBackground.setAntiAlias(true);

    textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    textColor = Color.WHITE;
//        backgroundColor=Color.rgb(34, 175, 236);
    TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, defStyle, 0);
//        if(attributes.getBoolean(R.styleable.AvatarImageView_border_new, true)) {
//            int defaultBorderSize = (int) (4 * getContext().getResources().getDisplayMetrics().density + 0.5f);
//            setBorderWidth(attributes.getDimensionPixelOffset(R.styleable.AvatarImageView_border_width_new, defaultBorderSize));
//            setBorderColor(attributes.getColor(R.styleable.AvatarImageView_border_color_new, Color.GRAY));
//            setName(attributes.getString(R.styleable.AvatarImageView_name));
//            setBackgroundColor(attributes.getColor(R.styleable.AvatarImageView_background_color_new,Color.rgb(34,175,236)));
//            setTextColor(attributes.getColor(R.styleable.AvatarImageView_text_color_new, Color.GRAY));
//        }
  }

  public static String getInitialByName(String name) {
    StringBuffer buffer = new StringBuffer();
    if (name != null) {
      StringTokenizer t = new StringTokenizer(name, " ");
      System.out.println(t.countTokens());
      if (t.countTokens() > 0) {
        String firstWord = t.nextToken();
        if (firstWord.length() > 0) {
          buffer.append(firstWord.substring(0, 1));
        }
        if (t.countTokens() >= 1) {
          String lastWord = t.nextToken();
          if (lastWord.length() > 0) {
            buffer.append(lastWord.substring(0, 1));
          }
        }

      }
    }
    String result = buffer.toString();
    return result.toUpperCase();
  }

  public void setBorderWidth(int borderWidth) {
    this.borderWidth = borderWidth;
    this.requestLayout();
    this.invalidate();
  }

  public void setName(String name) {
    this.name = name;
    this.requestLayout();
    this.invalidate();
  }

  public void setBorderColor(int borderColor) {
    if (paintBorder != null)
      paintBorder.setColor(borderColor);
    this.invalidate();
  }

  public void setBackgroundColor(int bgColor) {
    this.backgroundColor = bgColor;
    this.requestLayout();
    this.invalidate();
  }

  public void setTextColor(int textColor) {
    this.textColor = textColor;
    this.requestLayout();
    this.invalidate();
  }

  @Override
  public void onDraw(Canvas canvas) {
    image = drawableToBitmap(getDrawable());

    canvasSize = canvas.getWidth();
    if (canvas.getHeight() < canvasSize)
      canvasSize = canvas.getHeight();

    int circleCenter = (canvasSize - (borderWidth * 2)) / 2;

    canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);


    paintBackground.setColor(backgroundColor);
    canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paintBackground);

    if (image != null) {
      BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      paint.setShader(shader);
      canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);

    }


    if (name != null) {
      Typeface nunitoRegularFont = Typeface.createFromAsset(
          getContext().getAssets(),
          KoinApplication.DEFAULT_FONT);
      textPaint.setTypeface(nunitoRegularFont);
      String text = getInitialByName(name);
      textPaint.setColor(textColor);
      Rect textBound = new Rect();
      textPaint.setTextSize(70);
      textPaint.getTextBounds(getTextForSize(text), 0, text.length(), textBound);
      canvas.drawText(text, circleCenter - textBound.width() / 2, circleCenter + textBound.height() / 2, textPaint);
    }

  }

  private String getTextForSize(String input) {
    StringBuffer buffer = new StringBuffer();
    if (input.length() == 1) {
      buffer.append(input);
      buffer.append(input);
    } else {
      buffer.append(input.substring(0, 2));
    }
    return buffer.toString();
  }

  private float getTextSizeToFit(int maxWidth, int maxHeight, String input, TextPaint textPaint) {
    float textSize = textPaint.getTextSize();
    String text = getTextForSize(input);
    if (text.length() == 0)
      return textSize;


    Rect textBound = new Rect();
    textPaint.getTextBounds(text, 0, text.length(), textBound);
    float width = textBound.width();
    float height = textBound.height();
    float adjustX = maxWidth / width;
    float adjustY = maxHeight / height;
    textSize = textSize * (adjustY < adjustX ? adjustY : adjustX);


    return textSize;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = measureWidth(widthMeasureSpec);
    int height = measureHeight(heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  private int measureWidth(int measureSpec) {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else if (specMode == MeasureSpec.AT_MOST) {
      result = specSize;
    } else {
      result = canvasSize;
    }
    return result;
  }

  private int measureHeight(int measureSpecHeight) {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpecHeight);
    int specSize = MeasureSpec.getSize(measureSpecHeight);

    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else if (specMode == MeasureSpec.AT_MOST) {
      result = specSize;
    } else {
      result = canvasSize;
    }
    return (result + 2);
  }

  public Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable == null) {
      return null;
    } else if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }

}
