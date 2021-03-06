package ssd.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import config.ConfigData;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DrawerLayout mDrawer;
    Switch sw_pw;
    Switch sw_auto;
    ConfigData config;
    private long backKeyPressedTime = 0;

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
        Intent intent_BLElist = new Intent(this, BLeListActivity.class);
        startActivity(intent_BLElist);
    }

    public void onClick_devlist(View view) {
        Intent intent_devlist = new Intent(this, DevlistActivity.class);
        startActivity(intent_devlist);
    }

    public void onClick_history(View view) {
        Intent intent_history = new Intent(this, HistoryActivity.class);
        startActivity(intent_history);
    }

    public void onClick_logdel(View view) {
        SsdDB db = new SsdDB(this,SsdDB.DBNAME,null,1);
        db.logDel();
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(MainActivity.this,
                    "종료하시려면 한번 더 누르세요!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    } // 백키 2번 로그아웃

    @Override
    protected void onDestroy() {
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