package ssd.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    Spinner device = null;
    TextView date = null;
    TextView devicedata = null;
    TextView datedata = null;
    SsdDB db = null;
    ArrayAdapter<String> arrad_d = null;
    ArrayList<String> dlist = null;
    Map<String,String> map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        device = (Spinner)findViewById(R.id.device);
        date = (TextView)findViewById(R.id.date);
        devicedata = (TextView)findViewById(R.id.devicedata);
        datedata = (TextView)findViewById(R.id.datedata);
        db = new SsdDB(this.getApplicationContext(),SsdDB.DBNAME,null,1);
        dlist = db.getDeviceList();
        dlist.add(0,"기기");

        device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tmp = dlist.get(position);
                if(tmp.equals("기기")){
                    map = db.read();
                } else {
                    map = db.read(tmp);
                }
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                map = db.read();
                onResume();
            }
        });

        arrad_d = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dlist);
        device.setAdapter(arrad_d);
        map = db.read();

        dataset();
    }

    @Override
    protected void onResume() {
        dataset();
        super.onResume();
    }

    public void dataset(){
        devicedata.setText(map.get("deviceid"));
        Integer a = map.get("deviceid").length();
        Log.e(a.toString(),a.toString());
        datedata.setText(map.get("date"));
    }
}