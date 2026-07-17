package com.example.tvlauncher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ReflectionHelper {

    public static void applyReflection(ImageView imageView, float reflectionHeight) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) return;

        Bitmap originalBitmap = drawableToBitmap(drawable);
        if (originalBitmap == null) return;

        Bitmap reflectionBitmap = createReflectionBitmap(originalBitmap, reflectionHeight);
        imageView.setImageBitmap(reflectionBitmap);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            width = 200;
            height = 200;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap createReflectionBitmap(Bitmap original, float reflectionHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        int reflectionHeightPx = (int) (height * reflectionHeight);
        if (reflectionHeightPx <= 0) return original;

        Bitmap reflection = Bitmap.createBitmap(width, reflectionHeightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(reflection);

        Matrix matrix = new Matrix();
        matrix.preScale(1f, -1f);
        matrix.postTranslate(0, height);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(original, matrix, paint);

        LinearGradient gradient = new LinearGradient(
                0, 0, 0, reflectionHeightPx,
                0xFF000000, 0x00000000,
                Shader.TileMode.CLAMP
        );

        Paint maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        maskPaint.setShader(gradient);

        canvas.drawRect(0, 0, width, reflectionHeightPx, maskPaint);

        Bitmap result = Bitmap.createBitmap(width, height + reflectionHeightPx, Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(result);
        resultCanvas.drawBitmap(original, 0, 0, null);
        resultCanvas.drawBitmap(reflection, 0, height, null);

        return result;
    }

    public static Bitmap createReflectionBitmap(Drawable drawable, float reflectionHeight, int width, int height) {
        if (drawable == null) return null;

        Bitmap original = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(original);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return createReflectionBitmap(original, reflectionHeight);
    }
}
