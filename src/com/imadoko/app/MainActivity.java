package com.imadoko.app;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.imadoko.app.AppConstants.CONNECTION;
import com.imadoko.service.ConnectionReceiver;
import com.imadoko.service.ConnectionService;

/**
 * アクティビティクラス
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class MainActivity extends FragmentActivity {

    private ConnectionReceiver _receiver;
    private TextView _connectionStatus;
    private TextView _debugLog;
    private ImageView _connectionImage;
    private Drawable _connectedImage;
    private Drawable _disconnectImage;
    private Drawable[] _connectingImages;
    private int _drawableIndex;
    private Timer _connectingTimer;
    private Queue<String> _debugLogQueue;

//    private ConnectionService _service; // TODO ActivityからServiceのメソッドを叩くときに使う
//    private ServiceConnection _serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            _service = ((ConnectionService.LocalBinder) service).getService();
////            IntentFilter filter = new IntentFilter();
////            _receiver = new ConnectionReceiver();
////            filter.addAction(ConnectionService.ACTION);
////            _service.registerReceiver(_receiver, filter);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName className) {
//            unbindService(_serviceConnection);
//            _service = null;
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _connectionImage = (ImageView) findViewById(R.id.connection_image);
        _connectionStatus = (TextView) findViewById(R.id.connection_status);
        _debugLog = (TextView) findViewById(R.id.debug_log);
        _debugLogQueue = new ConcurrentLinkedQueue<String>();
        Resources res = this.getResources();
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
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDebugLog(AppConstants.CONNECTION.APPLICATION_CREATE.toString());
                startService(AppConstants.CONNECTION.APPLICATION_CREATE.toString());
            }
        });

        findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDebugLog(AppConstants.CONNECTION.APPLICATION_STOP.toString());
                stopService(AppConstants.CONNECTION.APPLICATION_STOP.toString()); // 一貫性がない
            }
        });

        startService(AppConstants.CONNECTION.APPLICATION_CREATE.toString());
        showDebugLog(AppConstants.CONNECTION.APPLICATION_CREATE.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(AppConstants.CONNECTION.APPLICATION_DESTROY.toString());
        endConnectingImage();
    }

    @Override
    public void onStart() {
        super.onStart();
        showDebugLog(AppConstants.CONNECTION.APPLICATION_START.toString());

        if (!isServiceRunning(this, ConnectionService.class)) {
            showDebugLog("サービス死んでる");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        showDebugLog(AppConstants.CONNECTION.APPLICATION_STOP.toString());
    }

    @Override
    public void onResume(){
        super.onResume();
        showDebugLog(AppConstants.CONNECTION.APPLICATION_RESUME.toString());
    }

    public void onConnected(String message) {
        connectSuccessImage();
        changeButton(CONNECTION.CONNECTED);
        _connectionStatus.setText(message);
    }

    public void onConnectionError(String message) {
        stopService(message);
        connectFailureImage();
        changeButton(CONNECTION.DISCONNECT);
        _connectionStatus.setText(message);
    }

    public void onReConnect(String message) {
        onConnectionError(message);
    }

    public void onConnecting(String message) {
        startConnectingImage();
        changeButton(CONNECTION.CONNECTING);
        _connectionStatus.setText(message);
    }

    private void changeButton(CONNECTION status) {
        Button startButton = (Button) findViewById(R.id.start_button);
        Button stopButton = (Button) findViewById(R.id.stop_button);

        Log.d(AppConstants.TAG_APPLICATION, String.valueOf(status));

        switch (status) {
        case CONNECTING:
            startButton.setEnabled(false);
            startButton.setBackgroundColor(Color.rgb(105, 105, 105));
            stopButton.setEnabled(false);
            stopButton.setBackgroundColor(Color.rgb(105, 105, 105));
            break;
        case CONNECTED:
            startButton.setEnabled(false);
            startButton.setBackgroundColor(Color.rgb(105, 105, 105));
            stopButton.setEnabled(true);
            stopButton.setBackgroundColor(Color.rgb(255, 140, 0));
            break;
        case DISCONNECT:
            startButton.setEnabled(true);
            startButton.setBackgroundColor(Color.rgb(255, 140, 0));
            stopButton.setEnabled(false);
            stopButton.setBackgroundColor(Color.rgb(105, 105, 105));
            break;
        default:
            break;
        }
    }

    public void startService(String message) {
        if (!isServiceRunning(this, ConnectionService.class)) {
            IntentFilter filter = new IntentFilter();
            _receiver = new ConnectionReceiver();
            filter.addAction(ConnectionService.ACTION);
            registerReceiver(_receiver, filter);
            startService(new Intent(this, ConnectionService.class));
            onConnecting(message);
            Log.d(AppConstants.TAG_APPLICATION, message);
        }
    }

    public void stopService(String message) {
        if (isServiceRunning(this, ConnectionService.class)) {
            stopService(new Intent(this, ConnectionService.class));
            unregisterReceiver(_receiver);
            onConnectionError(message);
            endConnectingImage();
            Log.d(AppConstants.TAG_APPLICATION, message);
        }
    }

    public void showDebugLog(String message) {
        String datetime = String.valueOf(DateFormat.format("yyyy/MM/dd kk:mm:ss", System.currentTimeMillis()));
        if (_debugLogQueue.size() >= AppConstants.DEBUG_LOG_MAX_SIZE) {
            _debugLogQueue.poll();
        }
        _debugLogQueue.add(datetime + ": " + message);
        _debugLog.setText(TextUtils.join("\n", _debugLogQueue));
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
        for (RunningServiceInfo info : runningService) {
            if (cls.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
