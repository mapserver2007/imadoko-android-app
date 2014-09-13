package com.imadoko.app;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.imadoko.service.ConnectionReceiver;
import com.imadoko.service.ConnectionService;


public class MainActivity extends Activity {
    
    private ConnectionReceiver _receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
    
    public void onMessage(String message) {
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
}
