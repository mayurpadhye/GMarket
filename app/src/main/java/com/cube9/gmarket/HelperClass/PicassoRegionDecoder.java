package com.cube9.gmarket.HelperClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;

/**
 * Created by Wasim on 19/10/2017.
 */

public class PicassoRegionDecoder implements ImageRegionDecoder {
    @NonNull
    @Override
    public Point init(Context context, @NonNull Uri uri) throws Exception {
        return null;
    }

    @NonNull
    @Override
    public Bitmap decodeRegion(@NonNull Rect sRect, int sampleSize) {
        return null;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void recycle() {

    }
}
