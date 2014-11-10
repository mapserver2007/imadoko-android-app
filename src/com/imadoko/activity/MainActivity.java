package com.imadoko.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpStatus;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.imadoko.app.R;
import com.imadoko.entity.GeofenceParcelable;
import com.imadoko.entity.GeofenceStatusEntity;
import com.imadoko.entity.HttpRequestEntity;
import com.imadoko.entity.HttpResponseEntity;
import com.imadoko.network.AsyncHttpTaskLoader;
import com.imadoko.receiver.ConnectionReceiver;
import com.imadoko.service.ConnectionService;
import com.imadoko.util.AppConstants;
import com.imadoko.util.AppConstants.CONNECTION;
import com.imadoko.util.AppUtils;

/**
 * MainActivity
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class MainActivity extends FragmentActivity {

    private ConnectionReceiver _receiver;
    private TextView _connectionStatus;
    private TextView _debugLog;
    private TextView _geofenceLog;
    private ImageView _connectionImage;
    private Drawable _connectedImage;
    private Drawable _disconnectImage;
    private Drawable[] _connectingImages;
    private int _drawableIndex;
    private Timer _connectingTimer;
    private Queue<String> _debugLogQueue;
    private Queue<String> _geofenceLogQueue;
    private String _authKey;
    private ArrayList<GeofenceParcelable> _geofenceList;
    private String _userName;
    private int _prevTransitionTypeState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        _authKey = intent.getStringExtra(AppConstants.PARAM_AUTH_KEY);
        _userName = intent.getStringExtra(AppConstants.PARAM_USERNAME);
        _geofenceList = bundle.getParcelableArrayList(AppConstants.PARAM_GEOFENCE_ENTITY);
        _connectionImage = (ImageView) findViewById(R.id.connection_image);
        _connectionStatus = (TextView) findViewById(R.id.connection_status);
        _debugLog = (TextView) findViewById(R.id.debug_log);
        _geofenceLog = (TextView) findViewById(R.id.geofence_log);
        _debugLogQueue = new ConcurrentLinkedQueue<String>();
        _geofenceLogQueue = new ConcurrentLinkedQueue<String>();
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
                res.getDrawable(R.drawable.connecting8) };

        findViewById(R.id.start_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDebugLog(CONNECTION.APPLICATION_CREATE.toString());
                        startService(CONNECTION.APPLICATION_CREATE.toString());
                    }
                });

        findViewById(R.id.stop_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDebugLog(CONNECTION.APPLICATION_STOP.toString());
                        stopService(CONNECTION.APPLICATION_STOP.toString());
                    }
                });

        startService(CONNECTION.APPLICATION_CREATE.toString());
        showDebugLog(CONNECTION.APPLICATION_CREATE.toString());
        setButtonEvent();
    }

    private void setButtonEvent() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDialogFragment dialog = new SettingsDialogFragment();
                View layout = inflater.inflate(R.layout.dialog_username, (ViewGroup) findViewById(R.id.dialog_edittext));
                dialog.setLayout(layout);
                dialog.setUserName(_userName);
                dialog.show(getFragmentManager(), AppConstants.DIALOG_SETTINGS);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(CONNECTION.APPLICATION_DESTROY.toString());
        endConnectingImage();
    }

    @Override
    public void onStart() {
        super.onStart();
        showDebugLog(CONNECTION.APPLICATION_START.toString());
        if (!isServiceRunning(this, ConnectionService.class)) {
            showDebugLog(CONNECTION.SERVICE_DEAD.toString());
            startService(CONNECTION.RECONNECT.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        showDebugLog(CONNECTION.APPLICATION_STOP.toString());
    }

    public void onConnected(String message) {
        connectSuccessImage();
        changeButton(CONNECTION.CONNECTED);
        _connectionStatus.setText(message);
    }

    public void onConnectionError(String message) {
        if (stopService(message)) {
            showDialog("imadokoサーバとの接続が切断されました。");
        }
        connectFailureImage();
        changeButton(CONNECTION.DISCONNECT);
        _connectionStatus.setText(message);
    }

    public void onReConnect(String message) {
        onReConnecting(message);
        startService(message);
    }

    public void onReConnecting(String message) {
        connectFailureImage();
        changeButton(CONNECTION.CONNECTING);
        _connectionStatus.setText(message);
    }

    public void onConnecting(String message) {
        startConnectingImage();
        changeButton(CONNECTION.CONNECTING);
        _connectionStatus.setText(message);
    }

    public void onGeofence(final int nextPlaceId, final int nextTransitionType) {
        HttpRequestEntity entity = new HttpRequestEntity();
        entity.setUrl(AppConstants.GEOFENCE_STATUS_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
        params.put(AppConstants.PARAM_TRANSITION_TYPE, String.valueOf(nextTransitionType));
        entity.setParams(params);

        AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
            @Override
            public void deliverResult(HttpResponseEntity response) {
                if (response.getStatusCode() == HttpStatus.SC_OK) {
                    GeofenceStatusEntity entity = JSON.decode(response.getResponseBody(), GeofenceStatusEntity.class);
                    entity.setNextPlaceId(nextPlaceId);
                    entity.setNextTransitionType(nextTransitionType);
                    entity.setPrevTransitionPatternId(_prevTransitionTypeState);
                    entity.setNextTransitionPatternId(entity.getPrevTransitionType() * 10 + entity.getNextTransitionType());

                    boolean isNotified = AppUtils.isGeofenceNotification(entity);

                    // 通知可能な移動ステータスの遷移
                    if (isNotified) {
                        showDebugLog("通知可能なGeofence遷移:" + String.valueOf(entity.getNextTransitionPatternId()));
                        // 通知許可なら通知実行
                        if ((entity.getNextTransitionType() == Geofence.GEOFENCE_TRANSITION_ENTER && entity.getIn() == AppConstants.GEOFENCE_NOTIFICATION_OK) ||
                            (entity.getNextTransitionType() == Geofence.GEOFENCE_TRANSITION_EXIT && entity.getOut() == AppConstants.GEOFENCE_NOTIFICATION_OK) ||
                            (entity.getNextTransitionType() == Geofence.GEOFENCE_TRANSITION_DWELL && entity.getStay() == AppConstants.GEOFENCE_NOTIFICATION_OK)) {
                            showDebugLog("Geofence通知あり");

                            // TODO
                            showDialog("判定処理つくらなー");
                        } else {
                            showDebugLog("Geofence通知設定なし");
                        }
                    } else {
                        showDebugLog("通知不許可なGeofence遷移:" + String.valueOf(entity.getNextTransitionPatternId()));
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append(getLandMarkName(entity.getPrevPlaceId()))
                        .append("(").append(AppUtils.getGeofenceStatus(entity.getPrevTransitionType())).append(")")
                        .append("→")
                        .append(getLandMarkName(nextPlaceId))
                        .append("(").append(AppUtils.getGeofenceStatus(entity.getNextTransitionType())).append(")")
                        .append(" ").append(isNotified ? "OKパターン" : "NGパターン");

                    showGeofenceLog(sb.toString());
                    writeGeofenceLog(nextPlaceId, nextTransitionType);
                    _prevTransitionTypeState = entity.getNextTransitionPatternId();
                }
            }
        };
        loader.get();
    }

    private void writeGeofenceLog(int placeId, int transitionType) {
        HttpRequestEntity entity = new HttpRequestEntity();
        entity.setUrl(AppConstants.GEOFENCE_LOG_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
        params.put(AppConstants.PARAM_PLACE_ID, String.valueOf(placeId));
        params.put(AppConstants.PARAM_TRANSITION_TYPE, String.valueOf(transitionType));
        entity.setParams(params);

        AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
            @Override
            public void deliverResult(HttpResponseEntity response) {
                String message = response.getStatusCode() == 200 ? "Geofenceログ保存成功" : "Geofenceログ保存失敗";
                showDebugLog(message);
            }
        };
        loader.post();
    }

    public String getLandMarkName(int placeId) {
        String landmarkName = "不明";
        for (GeofenceParcelable geofence : _geofenceList) {
            if (placeId == geofence.getId()) {
                landmarkName = geofence.getLandmark();
                break;
            }
        }

        return landmarkName;
    }

    private void changeButton(CONNECTION status) {
        Button startButton = (Button) findViewById(R.id.start_button);
        Button stopButton = (Button) findViewById(R.id.stop_button);

        Log.d(AppConstants.TAG_APPLICATION, String.valueOf(status));

        switch (status) {
        case CONNECTING:
            startButton.setEnabled(false);
            startButton.setBackgroundColor(Color.rgb(60, 61, 61));
            stopButton.setEnabled(false);
            stopButton.setBackgroundColor(Color.rgb(60, 61, 61));
            break;
        case CONNECTED:
            startButton.setEnabled(false);
            startButton.setBackgroundColor(Color.rgb(60, 61, 61));
            stopButton.setEnabled(true);
            stopButton.setBackgroundColor(Color.rgb(255, 168, 34));
            break;
        case DISCONNECT:
            startButton.setEnabled(true);
            startButton.setBackgroundColor(Color.rgb(255, 168, 34));
            stopButton.setEnabled(false);
            stopButton.setBackgroundColor(Color.rgb(60, 61, 61));
            break;
        default:
            break;
        }
    }

    public boolean startService(String message) {
        if (_receiver != null) {
            unregisterReceiver(_receiver);
        }
        _receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION);
        registerReceiver(_receiver, filter);

        if (!isServiceRunning(this, ConnectionService.class)) {
            final Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(AppConstants.PARAM_GEOFENCE_ENTITY, _geofenceList);
            Intent intent = new Intent(this, ConnectionService.class);
            intent.putExtra(AppConstants.PARAM_AUTH_KEY, _authKey);
            intent.putExtras(bundle);
            startService(intent);
            onConnecting(message);
            Log.d(AppConstants.TAG_APPLICATION, message);

            return true;
        }

        return false;
    }

    public boolean stopService(String message) {
        if (isServiceRunning(this, ConnectionService.class)) {
            stopService(new Intent(this, ConnectionService.class));
            unregisterReceiver(_receiver);
            _receiver = null;
            onConnectionError(message);
            endConnectingImage();
            Log.d(AppConstants.TAG_APPLICATION, message);

            return true;
        }

        return false;
    }

    private void showDialog(String message) {
        Intent dialogIntent = new Intent(this, AlertDialogActivity.class);
        dialogIntent.putExtra(AppConstants.PARAM_DIALOG_MESSAGE, message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, dialogIntent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException ignore) {
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

    private void showGeofenceLog(String message) {
        String datetime = String.valueOf(DateFormat.format("yyyy/MM/dd kk:mm:ss", System.currentTimeMillis()));
        if (_geofenceLogQueue.size() >= AppConstants.GEOFENCE_LOG_MAX_SIZE) {
            _geofenceLogQueue.poll();
        }
        _geofenceLogQueue.add(datetime + ": " + message);
        _geofenceLog.setText(TextUtils.join("\n", _geofenceLogQueue));
    }

    public void onRegisterUserNameResult(int statusCode) {
        if (statusCode == HttpStatus.SC_OK) {
            showDebugLog(CONNECTION.USERNAME_REGISTER_OK.toString());
            Toast.makeText(this, CONNECTION.USERNAME_REGISTER_OK.toString(), Toast.LENGTH_LONG).show();
        } else {
            showDebugLog(CONNECTION.USERNAME_REGISTER_NG.toString());
            Toast.makeText(this, CONNECTION.USERNAME_REGISTER_NG.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void registerUserName(String userName) {
        _userName = userName;
        HttpRequestEntity entity = new HttpRequestEntity();
        entity.setUrl(AppConstants.REGISTER_USERNAME_URL);
        Map<String, String> params = new HashMap<String, String>();
        params.put(AppConstants.PARAM_AUTH_KEY, _authKey);
        params.put(AppConstants.PARAM_USERNAME, _userName);
        entity.setParams(params);

        AsyncHttpTaskLoader loader = new AsyncHttpTaskLoader(this, entity) {
            @Override
            public void deliverResult(HttpResponseEntity response) {
                onRegisterUserNameResult(response.getStatusCode());
            }
        };
        loader.post();
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
        public void handleMessage(Message msg) {
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

    /**
     * プロセスが生存しているか
     * @param context コンテキスト
     * @param cls クラスオブジェクト
     * @return
     */
    private boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningService = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo info : runningService) {
            if (cls.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
