package id.kenshiro.app.panri.helper;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryImageCache {
    public static class AssetsMemoryImage {
        private LruCache<Integer, Bitmap> cachedMemory;
        //private
    }
}
