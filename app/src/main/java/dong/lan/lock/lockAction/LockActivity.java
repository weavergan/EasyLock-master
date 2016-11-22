
package dong.lan.lock.lockAction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.webkit.WebView;

import dong.lan.lock.BaseActivity;
import dong.lan.lock.Global;
import dong.lan.lock.R;
import dong.lan.lock.lockConfig.SettingPresenter;
import dong.lan.lock.utils.RequestService;

import android.annotation.SuppressLint;
import android.webkit.JavascriptInterface;

/*
 * 锁频页面
 */

public class LockActivity extends BaseActivity implements SettingPresenter.ConfigChange, LockViewManager2.LockStatusListener {

    private LockViewManager2 viewManager = null;

    private static WebView webView = null;

    private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    // 声明一个Handler对象
    private static Handler handler = new Handler();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onCreate(savedInstanceState);

        Global.initIntentApps(this);
        setupView(R.layout.activity_lock);

        webView = (WebView) findViewById(R.id.webview);//android内置浏览器对象
        webView.getSettings().setJavaScriptEnabled(true);//启用javascript支持
        //添加一个js交互接口，方便html布局文件中的javascript代码能与后台java代码直接交互访问
        webView.addJavascriptInterface(new NotificationBindObject(), "NotificationBind");//new类名，交互访问时使用的别名
//        webView.loadUrl("file:///android_asset/slide.html");//加载本地的html布局文件
        webView.loadUrl("file:///android_asset/index.html");//加载本地的html布局文件


//        viewManager = LockViewManager2.getInstance(LockActivity.this);
//        viewManager.setLockStatusListener(this);
//        viewManager.setLockView(LayoutInflater.from(LockActivity.this).inflate(R.layout.view_lock2,null));
//        viewManager.updateActivity(LockActivity.this);

        SettingPresenter.addConfigChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        try {
//            viewManager.Lock();
//        } catch (NoLockStatusListenerException e) {
//            e.printStackTrace();
//            Show(e.getMessage());
//        }
    }

    @Override
    protected void onDestroy() {
//        viewManager.updateActivity(LockActivity.this);
//        viewManager.unLock();
//        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
        super.onDestroy();

    }

    //设置页面设置密码模型的回调，保证下次锁频时使用的时最新的密码模型
    @Override
    public void onPatterChange(String patter) {
        viewManager.updatePatter(patter);
    }

    @Override
    public void onLockItemColorChange(int color) {
        viewManager.updateLockItemColor(color);
    }

    @Override
    public void onLockTextColorChange(int color) {
        viewManager.updateLockTextColor(color);
    }

    //锁频页面屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        int key = event.getKeyCode();
        return key == KeyEvent.KEYCODE_BACK || key == KeyEvent.KEYCODE_HOME || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocked() {

    }

    @Override
    public void onUnlock() {
        finish();
    }

    private final class NotificationBindObject{

        @JavascriptInterface
        public void showNotification(String picId, String result) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            });
            System.out.println("########################" + picId + ":" + result);
            int resultInt = 0;
            if(result.trim().equalsIgnoreCase("yes")) {
                resultInt = 1;
            }
            // 获取deviceId
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();

            System.out.println(RequestService.SendPost(picId, resultInt, deviceId));

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }


    }
}
