package com.example.tvlauncher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class ReflectionHelper {

    public static Bitmap createReflectionBitmap(Bitmap original, float reflectionRatio, String bgColorHex) {
        if (original == null) return null;
        int width = original.getWidth();
        int height = original.getHeight();

        int reflectionHeightPx = (int) (height * reflectionRatio);
        if (reflectionHeightPx <= 0) return original;

        Bitmap resultBitmap = Bitmap.createBitmap(width, height + reflectionHeightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // 绘制原图
        canvas.drawBitmap(original, 0, 0, null);

        // 绘制反射（翻转）
        Matrix matrix = new Matrix();
        matrix.setScale(1f, -1f);
        matrix.postTranslate(0, 2 * height);
        Paint reflectPaint = new Paint();
        reflectPaint.setAntiAlias(true);
        canvas.drawBitmap(original, matrix, reflectPaint);

        // 应用渐变遮罩（DST_IN）仅作用于反射区域
        int saveId = canvas.save();
        canvas.clipRect(0, height, width, height + reflectionHeightPx);

        Paint maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        LinearGradient gradient = new LinearGradient(
                0, height,
                0, height + reflectionHeightPx,
                new int[]{0xFF000000, 0x00000000},  // 顶部纯黑不透明，底部完全透明
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
        );
        maskPaint.setShader(gradient);
        canvas.drawRect(0, height, width, height + reflectionHeightPx, maskPaint);

        canvas.restoreToCount(saveId);

        return resultBitmap;
    }

    public static Bitmap createReflectionBitmap(Drawable drawable, float reflectionHeight, int width, int height) {
        if (drawable == null) return null;
        Bitmap original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(original);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return createReflectionBitmap(original, reflectionHeight);
    }

    public static Bitmap createReflectionBitmap(Bitmap original, float reflectionRatio) {
        return createReflectionBitmap(original, reflectionRatio, null);
    }
}
