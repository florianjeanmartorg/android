package be.gling.businessApp.model.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import be.gling.businessApp.model.dto.StoredFileDTO;

/**
 * Created by florian on 14/10/15.
 */
public class ImageCache {

    private static final String TEMP_PREFIX = "__TEMP__";

    private static LruCache<String, Bitmap> mMemoryCache = null;

    public static void initialization() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemoryCache(StoredFileDTO storedFile, Bitmap bitmap) {
        if (mMemoryCache == null) {
            initialization();
        }
        if (getBitmapFromMemCache(storedFile) == null) {
            mMemoryCache.put(storedFile.getStoredName(), bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(StoredFileDTO storedFile) {
        if (mMemoryCache == null) {
            initialization();
        }
        return mMemoryCache.get(storedFile.getStoredName());
    }

    public static void addTempToMemoryCache(String name, Bitmap bitmap) {
        if (mMemoryCache == null) {
            initialization();
        }
        if (getTempFromMemCache(name) == null) {
            String key = TEMP_PREFIX+name;
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getTempFromMemCache(String name) {
        if (mMemoryCache == null) {
            initialization();
        }
        String key = TEMP_PREFIX+name;
        return mMemoryCache.get(key);
    }
}
