package dong.lan.lock.lockAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

import dong.lan.lock.Global;
import dong.lan.lock.R;
import dong.lan.lock.utils.RequestService;

/**
 * Created by 梁桂栋 on 2016年08月07日 22:02.
 * Email:760625325@qq.com
 * desc:锁屏页面
 */
public class LockViewManager2 {

    private Activity activity;
    private static View lockView;

    private WeakReference<WindowManager> windowManagerRef;
    public static LockViewManager2 manager = null;
    private static WindowManager.LayoutParams layoutParams;
    public static Vibrator vibrator;
    private volatile boolean isLock = false;

    private LockStatusListener lockStatusListener;

    public static synchronized LockViewManager2 getInstance(Activity activity) {
        if (manager == null)
            manager = new LockViewManager2(activity);
        return manager;
    }

    public LockViewManager2(Activity activity) {
        this.activity = activity;
        vibrator = (Vibrator) activity.getApplication().getApplicationContext()
                .getSystemService(Context.VIBRATOR_SERVICE);
        init();
    }
    public void updateActivity(Activity ac) {
        activity = ac;
    }

    public void updatePatter(String patter) {

    }

    public void updateLockItemColor(int color) {

    }

    public void updateLockTextColor(int color) {

    }

    public void setLockView(View v) {
        lockView = v;
    }

    private WindowManager getWindowManager() {
        WindowManager windowManager = windowManagerRef.get();
        if (windowManager == null) {
            windowManager = activity.getWindowManager();
            windowManagerRef = new WeakReference<WindowManager>(windowManager);
        }
        return windowManager;
    }

    //初始化锁屏view的布局配置
    private void init() {
        isLock = false;
        windowManagerRef = new WeakReference<WindowManager>(activity.getWindowManager());
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.flags = 1280;
        layoutParams.windowAnimations = R.anim.zoom_enter;
    }

    public synchronized void Lock() throws NoLockStatusListenerException {
        if (lockStatusListener == null)
            throw new NoLockStatusListenerException();
        if (lockView != null && !isLock) {
            if (isLock)
                getWindowManager().updateViewLayout(lockView, layoutParams);
            else
                getWindowManager().addView(lockView, layoutParams);
            isLock = true;
            lockStatusListener.onLocked();
        }
    }

    public synchronized void unLock() {
        if (getWindowManager() != null && isLock) {
            getWindowManager().removeView(lockView);
            isLock = false;
            lockStatusListener.onUnlock();
        }
    }




    public void setLockStatusListener(LockStatusListener listener) {
        this.lockStatusListener = listener;
    }

    public interface LockStatusListener {
        void onLocked();

        void onUnlock();
    }

}
