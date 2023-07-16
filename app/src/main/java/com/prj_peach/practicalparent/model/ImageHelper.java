package com.prj_peach.practicalparent.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * ImageHelper wraps some of the functionality of the 'picasso' library
 * Picasso automatically loads device images into cache, and can set images to imageViews
 */

public class ImageHelper {

    public static void setImageToView(Context context, String path, ImageView imageView, int placeholderID) {
        Picasso.with(context)
                .load(new File(path))
                .placeholder(placeholderID)
                .into(imageView);
    }

    public static void setImageCropLarge(Context context, String path, ImageView view, int placeholderID) {
        setImageCrop(context, path, view, placeholderID,
                128, 128);
    }

    public static void setImageCropSmall(Context context, String path, ImageView view, int placeholderID) {
        setImageCrop(context, path, view, placeholderID,
                64, 64);
    }

    public static void setImageCropTiny(Context context, String path, ImageView view, int placeholderID) {
        setImageCrop(context, path, view, placeholderID,
                32, 32);
    }

    private static void setImageCrop(Context context, String path, ImageView view, int placeholderID,
                                     int width, int height)
    {
        Picasso.with(context)
                .load(new File(path))
                .placeholder(placeholderID)
                .resize(width, height)
                .centerCrop()
                .transform(new RoundedTransformation(width / 2, 0))
                .into(view);
    }

    public static String saveBitmapImage(Context context, Bitmap imageBitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File dir = cw.getDir("images", Context.MODE_PRIVATE);
        File path = new File(dir, "image_" + Calendar.getInstance().getTime().toString() + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path.getAbsolutePath();
    }

    private static class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin; // dp

        // radius is corner radii in dp
        // margin is the board in dp
        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(),
                    source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth()
                    - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }
}


