package com.imadoko.app;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imadoko.service.ConnectionReceiver;
import com.imadoko.service.ConnectionService;
import com.navdrawer.SimpleSideDrawer;

/**
 * アクティビティクラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class MainActivity extends FragmentActivity {
    private SimpleSideDrawer _sideDrawer;
    private GestureDetector _gestureDetector;
    private ConnectionReceiver _receiver;

    private TextView _connectionStatus;
    private ImageView _connectionImage;
    private Drawable _connectedImage;
    private Drawable _disconnectImage;
    private Drawable[] _connectingImages;
    private int _drawableIndex;
    private Timer _connectingTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _connectionImage = (ImageView) findViewById(R.id.connection_image);
        Resources res = this.getResources();

        _connectionStatus = (TextView) findViewById(R.id.connection_status);
        _connectedImage = res.getDrawable(R.drawable.connected);
        _disconnectImage = res.getDrawable(R.drawable.disconnect);
        _connectingImages = new Drawable[] {
            res.getDrawable(R.drawable.connecting1),
            res.getDrawable(R.drawable.connecting2),
            res.getDrawable(R.drawable.connecting3),
            res.getDrawable(R.drawable.connecting4),
            res.getDrawable(R.drawable.connecting5),
            res.getDrawable(R.drawable.connecting6),
            res.getDrawable(R.drawable.connecting7),
            res.getDrawable(R.drawable.connecting8)
        };

        // ボタン
        findViewById(R.id.restart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceRunning(view.getContext(), ConnectionService.class)) {
                    stopService("アプリケーション停止");
                }
                startService("アプリケーション起動");
            }
        });

        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService("アプリケーション停止");
            }
        });

        // サイドメニュー
        // TODO クリックイベントもdispatchEventしてしまうため解決方法がわからん
        // そもそもフリックによるメニュー表示っていらん？
//        _sideDrawer = new SimpleSideDrawer(this);
//        _sideDrawer.setLeftBehindContentView(R.layout.activity_behind_left_simple);
//        _gestureDetector = new GestureDetector(this, getGestureListener());


        startService("アプリケーション起動");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ConnectionService.class));
        unregisterReceiver(_receiver);
        endConnectingImage();
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        return _gestureDetector.onTouchEvent(event);
//    }

    public void onConnected(String message) {
        connectSuccessImage();
        _connectionStatus.setText(message);
    }

    public void onConnectionError(String message) {
        connectFailureImage();
        _connectionStatus.setText(message);
    }

    public void onConnecting(String message) {
        startConnectingImage();
        _connectionStatus.setText(message);
    }

    public void startService(String message) {
        IntentFilter filter = new IntentFilter();
        _receiver = new ConnectionReceiver();
        filter.addAction(ConnectionService.ACTION);
        registerReceiver(_receiver, filter);
        startService(new Intent(this, ConnectionService.class));
        onConnecting(message);
        Button button = (Button) findViewById(R.id.stop_button);
        button.setEnabled(true);
        button.setBackgroundColor(Color.rgb(255, 140, 0));
        Log.d(AppConstants.TAG_APPLICATION, message);
    }

    public void stopService(String message) {
        stopService(new Intent(this, ConnectionService.class));
        unregisterReceiver(_receiver);
        onConnectionError(message);
        endConnectingImage();
        Button button = (Button) findViewById(R.id.stop_button);
        button.setEnabled(false);
        button.setBackgroundColor(Color.rgb(105, 105, 105));
        Log.d(AppConstants.TAG_APPLICATION, message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Drawable[] getConnectionImages() {
        return _connectingImages;
    }

    public ImageView getConnectionImageView() {
        return _connectionImage;
    }

    private static class UIHandler extends Handler {

        private WeakReference<MainActivity> _activity;

        public UIHandler(MainActivity activity) {
            _activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            MainActivity activity = _activity.get();
            if (activity != null) {
                activity.getConnectionImageView().setImageDrawable(activity.getConnectionImages()[msg.what]);
            }
        }
    }

    private void startConnectingImage() {
        final UIHandler handler = new UIHandler(this);
        _connectingTimer = new Timer();
        _connectingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (_drawableIndex < _connectingImages.length - 1) {
                    _drawableIndex++;
                } else {
                    _drawableIndex = 0;
                }
                Message msg = new Message();
                msg.what = _drawableIndex;
                handler.sendMessage(msg);
            }
        }, 0, 300);
    }

    private void endConnectingImage() {
        if (_connectingTimer != null) {
            _connectingTimer.cancel();
        }
        _connectingTimer = null;
    }

    private void connectSuccessImage() {
        endConnectingImage();
        getConnectionImageView().setImageDrawable(_connectedImage);
    }

    private void connectFailureImage() {
        endConnectingImage();
        getConnectionImageView().setImageDrawable(_disconnectImage);
    }

    public boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningService = am.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo i : runningService) {
            if (cls.getName().equals(i.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    /**
//     * ジェスチャーリスナを返却する
//     * @return ジェスチャーリスナオブジェクト
//     */
//    private SimpleOnGestureListener getGestureListener() {
//        return new SimpleOnGestureListener() {
//            private boolean isSideMenuOpend = false;
//
//            @Override
//            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
//                Log.v(AppConstants.TAG_APPLICATION, "onFling");
//                try {
//                    if (Math.abs(event1.getY() - event2.getY()) > AppConstants.SWIPE_MAX_OFF_PATH) {
//                        return false;
//                    }
//                    if (event1.getX() - event2.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
//                        if (isSideMenuOpend) {
//                            _sideDrawer.toggleLeftDrawer();
//                            isSideMenuOpend = false;
//                        }
//                    } else if (event2.getX() - event1.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
//                        if (!isSideMenuOpend) {
//                            _sideDrawer.toggleLeftDrawer();
//                            isSideMenuOpend = true;
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d(AppConstants.TAG_APPLICATION, e.getMessage());
//                }
//                return false;
//            }
//        };
//    }
}
