package com.imadoko.app;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _sideDrawer = new SimpleSideDrawer(this);
        _sideDrawer.setLeftBehindContentView(R.layout.activity_behind_left_simple);
        ListView sideMenu = (ListView) _sideDrawer.findViewById(R.id.listview);
//        sideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView,
//                    View view, int pos, long id) {
//
//                ListView listView = (ListView) adapterView;
//                String item = (String) listView.getItemAtPosition(pos);
//
//                Log.d(AppConstants.TAG_APPLICATION, item);
//
//            }
//
//        });


        _gestureDetector = new GestureDetector(this, getGestureListener());

        _receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionService.ACTION);
        registerReceiver(_receiver, filter);
        startService(new Intent(this, ConnectionService.class));
    }

    protected void onDestory() {
        super.onDestroy();
        stopService(new Intent(this, ConnectionService.class));
        unregisterReceiver(_receiver);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return _gestureDetector.onTouchEvent(event);
    }

    public void onMessage(String message) {
//        Log.d(AppConstants.TAG_APPLICATION, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onServiceStop(String message) {
        stopService(new Intent(this, ConnectionService.class));
        Log.d(AppConstants.TAG_APPLICATION, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    /**
     * ジェスチャーリスナを返却する
     * @return ジェスチャーリスナオブジェクト
     */
    private SimpleOnGestureListener getGestureListener() {
        return new SimpleOnGestureListener() {
            private boolean isSideMenuOpend = false;

            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                Log.v(AppConstants.TAG_APPLICATION, "onFling");
                try {
                    if (Math.abs(event1.getY() - event2.getY()) > AppConstants.SWIPE_MAX_OFF_PATH) {
                        return false;
                    }
                    if (event1.getX() - event2.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
                        if (isSideMenuOpend) {
                            _sideDrawer.toggleLeftDrawer();
                            isSideMenuOpend = false;
                        }
                    } else if (event2.getX() - event1.getX() > AppConstants.SWIPE_MIN_DISTANCE && Math.abs(velocityX) > AppConstants.SWIPE_THRESHOLD_VELOCITY) {
                        if (!isSideMenuOpend) {
                            _sideDrawer.toggleLeftDrawer();
                            isSideMenuOpend = true;
                        }
                    }
                } catch (Exception e) {
                    Log.d(AppConstants.TAG_APPLICATION, e.getMessage());
                }
                return false;
            }
        };
    }
}
