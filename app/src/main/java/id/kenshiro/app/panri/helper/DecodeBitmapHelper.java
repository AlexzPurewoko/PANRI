package id.kenshiro.app.panri.helper;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Px;

import java.io.IOException;
import java.io.InputStream;

public class DecodeBitmapHelper {
    public static Bitmap decodeAndResizeBitmapsResources(@NonNull Resources res, @DrawableRes int resImageDrawable, @Px int reqHeight, @Px int reqWidth) {
        BitmapFactory.Options optionBitmaps = new BitmapFactory.Options();
        optionBitmaps.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resImageDrawable, optionBitmaps);

        optionBitmaps.inSampleSize = calculateImageInSampleSize(optionBitmaps, reqHeight, reqWidth);
        optionBitmaps.inJustDecodeBounds = false;
        System.gc();
        return BitmapFactory.decodeResource(res, resImageDrawable, optionBitmaps);
    }

    public static Bitmap decodeAndResizeBitmapsAssets(@NonNull AssetManager assets, @NonNull String pathImage, @Px int reqHeight, @Px int reqWidth) throws IOException {
        BitmapFactory.Options optionBitmaps = new BitmapFactory.Options();
        optionBitmaps.inJustDecodeBounds = true;

        // load image in inputstream
        InputStream imageStream = assets.open(pathImage);
        BitmapFactory.decodeStream(imageStream, null, optionBitmaps);
        imageStream.reset();
        optionBitmaps.inSampleSize = calculateImageInSampleSize(optionBitmaps, reqHeight, reqWidth);
        optionBitmaps.inJustDecodeBounds = false;
        Bitmap results = BitmapFactory.decodeStream(imageStream, null, optionBitmaps);
        imageStream.close();
        System.gc();
        return results;
    }

    private static int calculateImageInSampleSize(BitmapFactory.Options optionBitmaps, int reqHeight, int reqWidth) {
        final int height = optionBitmaps.outHeight;
        final int width = optionBitmaps.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            while ((halfHeight / inSampleSize >= reqHeight) && (halfWidth / inSampleSize >= reqWidth)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
