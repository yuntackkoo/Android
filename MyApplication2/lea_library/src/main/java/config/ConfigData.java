package config;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Koo on 2017-04-22.
 */
public class ConfigData implements Serializable{
    private boolean auto_update = true;
    private int angle = 45;
    private ArrayList<String> User = new ArrayList<>();
    private ArrayList<String> Device = new ArrayList<>();

    public void setAuto_update(boolean auto_update) {
        this.auto_update = auto_update;
    }
    public void setAngle(int angle) {
        this.angle = angle;
    }
    public boolean isAuto_update() {
        return auto_update;
    }
    public int getAngle() {
        return angle;
    }
    public ArrayList<String> getUser() {
        return User;
    }

    public ArrayList<String> getDevice() {
        return Device;
    }
}