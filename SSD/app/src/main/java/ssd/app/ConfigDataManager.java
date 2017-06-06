package ssd.app;

import android.content.Context;
import android.content.SharedPreferences;

import config.ConfigData;

/**
 * Created by Koo on 2017-04-23.
 */

public class ConfigDataManager {
    private static final ConfigDataManager ourInstance = new ConfigDataManager();
    private static Context context = null;
    private SharedPreferences share;
    private SharedPreferences.Editor edit;

    public static ConfigDataManager getInstance(Context c) {
        context = c.getApplicationContext();
        return ourInstance;
    }

    private ConfigDataManager() {
    }

    public ConfigData getData(){
        ConfigData d = new ConfigData();
        share = context.getApplicationContext().getSharedPreferences("data",Context.MODE_PRIVATE);
        d.setAuto_update(share.getBoolean("Auto_Update",true));
        d.setLock(share.getBoolean("LOCK",false));
        return d;
    }

    public void saveData(ConfigData d){
        edit = context.getApplicationContext().getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.putBoolean("Auto_Update",d.isAuto_update());
        edit.putBoolean("LOCK",d.isLock());
        edit.commit();
    }
}