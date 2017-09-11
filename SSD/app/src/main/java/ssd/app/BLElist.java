package ssd.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import ssd.app.databinding.ActivityBlelistBinding;

public class BLElist extends AppCompatActivity {

    ActivityBlelistBinding binding_BLElist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding_BLElist = DataBindingUtil.setContentView(this, R.layout.activity_blelist);
        this.setFinishOnTouchOutside(false); // 다이얼로그 액티비티 외부 터치시 finish false


    }
}
