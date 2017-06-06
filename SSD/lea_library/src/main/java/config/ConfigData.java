package config;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Koo on 2017-04-22.
 */
public class ConfigData implements Serializable{
    private boolean auto_update = true; //자동 업데이트
    private boolean lock = false; //2차보안

    public void setAuto_update(boolean auto_update) {
        this.auto_update = auto_update;
    }
    public boolean isAuto_update() {
        return auto_update;
    }
    public boolean isLock() {
        return lock;
    }
    public void setLock(boolean lock) {
        this.lock = lock;
    }
}