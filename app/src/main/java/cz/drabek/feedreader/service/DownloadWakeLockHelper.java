package cz.drabek.feedreader.service;

import android.content.Context;
import android.os.PowerManager;

public class DownloadWakeLockHelper {

    private static final String LOCK_NAME = "cz.drabek.feedreader";
    private static PowerManager.WakeLock sWakeLock = null;
    private static final long LOCK_TIMEOUT = 10 * 60 * 1000;

    public static synchronized void acquire(Context context) {
        if (sWakeLock == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            sWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME);
        }
        sWakeLock.acquire(LOCK_TIMEOUT);
    }

    public static synchronized void release() {
        if (sWakeLock != null) {
            sWakeLock.release();
        }
    }
}
