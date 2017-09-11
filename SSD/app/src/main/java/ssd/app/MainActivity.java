package ssd.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import config.ConfigData;

public class MainActivity extends AppCompatActivity {

    public AsyncTask btTask;
    DrawerLayout mDrawer;
    Switch sw_pw;
    Switch sw_auto;
    ConfigData config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = ConfigDataManager.getInstance(this).getData();

        sw_pw = (Switch) findViewById(R.id.switch_pw);
        sw_auto = (Switch) findViewById(R.id.switch_autoupdate);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);

        sw_pw.setChecked(config.isLock());
        sw_auto.setChecked(config.isAuto_update());

        sw_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final AlertDialog.Builder dialog_SetPW = new AlertDialog.Builder(MainActivity.this);

                dialog_SetPW.setTitle("보안 설정");
                final EditText et_PW = new EditText(MainActivity.this);
                dialog_SetPW.setView(et_PW);

                dialog_SetPW.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog_SetPW.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog_SetPW.show();
            }
        });

        Intent service = new Intent(this,Connection.class);
        startService(service);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        //mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //스와이프 락
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
                    mDrawer.closeDrawers();
                } else {
                    mDrawer.openDrawer(Gravity.LEFT);
                }
        }
        return true;
    }

    public void onClick_btunlock(View view) {
        btTask = new BtTask();
        btTask.execute();
        //startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)); 블루투스 설정 화면으로 이동
    }

    public void onClick_devlist(View view) {
        Intent intent_devlist = new Intent(this, Devlist.class);
        startActivity(intent_devlist);
    }

    public void onClick_history(View view) {
        Intent intent_history = new Intent(this, History.class);
        startActivity(intent_history);
    }

    public void onClick_logdel(View view) {
        SsdDB db = new SsdDB(this,SsdDB.DBNAME,null,1);
        db.logDel();
    }

    private class BtTask extends AsyncTask {

         protected void onPreExecute() {
             Log.d("SSD","Async Test 0");
             super.onPreExecute();
         }
         @Override
         protected Object doInBackground(Object[] params) {
             try {
                 Thread.sleep(1000);
                 // doInBackground 안에서 UI 처리를 하게되면 Fatal Error
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             return true;
         }
         @Override
         protected void onPostExecute(Object o) {
             super.onPostExecute(o);
             BTService.checkBluetooth(MainActivity.this);
         }
     }

    @Override
    protected void onDestroy() {
        try {
            if (btTask.getStatus() == AsyncTask.Status.RUNNING) {
                btTask.cancel(true);
            }
            else { }

        }
        catch (Exception e) { }

        if(config != null){
            config.setAuto_update(sw_auto.isChecked());
            config.setLock(sw_pw.isChecked());
            ConfigDataManager.getInstance(this).saveData(config);
        }
        if(!config.isAuto_update()) {
            Intent service = new Intent(this, Connection.class);
            stopService(service);
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(config != null){
            config.setAuto_update(sw_auto.isChecked());
            config.setLock(sw_pw.isChecked());
            ConfigDataManager.getInstance(this).saveData(config);
        }
        super.onPause();
    }
}