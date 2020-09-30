package com.example.tiktok_analog.util

import android.graphics.*

import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

class ImageHelper {
    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        val canvas = Canvas(output);

        val color: Long = 0xff424242;
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        val rectF = RectF(rect)

        paint.isAntiAlias = true;
        canvas.drawARGB(0, 0, 0, 0);
        paint.color = color.toInt()
        canvas.drawRoundRect(rectF, pixels.toFloat(), pixels.toFloat(), paint);

        paint.xfermode = PorterDuffXfermode (Mode.SRC_IN);
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}