package ssd.app;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import ssd.app.databinding.ActivityBlelistBinding;

public class BLElist extends Activity {

    ActivityBlelistBinding binding_BLElist;
    AsyncTask scanTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding_BLElist = DataBindingUtil.setContentView(this, R.layout.activity_blelist);
        this.setFinishOnTouchOutside(false); // 다이얼로그 액티비티 외부 터치시 finish false

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClick_btnblecancle(View v) {
        this.finish();
    }

}