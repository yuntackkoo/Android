package com.example.koo.myapplication;

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
        share = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        d.setAngle(share.getInt("Angle",45));
        d.setAuto_update(share.getBoolean("Auto_Update",true));
        for(int i=0;i<share.getInt("User_Size",0);i++){
            d.getUser().add(share.getString("User"+i,null));
        }
        for(int i=0;i<share.getInt("Device_Size",0);i++){
            d.getDevice().add(share.getString("Device"+i,null));
        }
        return d;
    }

    public void saveData(ConfigData d){
        edit = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        edit.clear();
        edit.putInt("Angle",d.getAngle());
        edit.putBoolean("Auto_Update",d.isAuto_update());
        edit.putInt("User_Size",d.getUser().size());
        edit.putInt("Device_Size",d.getDevice().size());
        for(int i=0;i<d.getUser().size();i++){
            edit.putString("User"+i,d.getUser().get(i));
        }
        for(int i=0;i<d.getDevice().size();i++){
            edit.putString("Device"+i,d.getDevice().get(i));
        }
        edit.commit();
    }
}
