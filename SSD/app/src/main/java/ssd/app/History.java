package ssd.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class History extends AppCompatActivity {
    Spinner device = null;
    Spinner user = null;
    TextView date = null;
    TextView devicedata = null;
    TextView userdata = null;
    TextView datedata = null;
    SsdDB db = null;
    ArrayAdapter<String> arrad_d = null;
    ArrayAdapter<String> arrad_u = null;
    ArrayList<String> dlist = null;
    ArrayList<String> ulist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        device = (Spinner)findViewById(R.id.device);
        user = (Spinner)findViewById(R.id.user);
        date = (TextView)findViewById(R.id.date);
        devicedata = (TextView)findViewById(R.id.devicedata);
        userdata = (TextView)findViewById(R.id.userdata);
        datedata = (TextView)findViewById(R.id.datedata);
        db = new SsdDB(this.getApplicationContext(),SsdDB.DBNAME,null,1);
        dlist = db.getDeviceList();
        ulist = db.getUserList();

        dlist.add(0,"기기");
        ulist.add(0,"유저");

        arrad_d = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dlist);
        arrad_u = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ulist);
        device.setAdapter(arrad_d);
        user.setAdapter(arrad_u);
    }

    @Override
    protected void onResume() {
        Map<String,String> map = db.read();
        devicedata.setText(map.get("deviceid"));
        Integer a = map.get("deviceid").length();
        Log.e(a.toString(),a.toString());
        userdata.setText(map.get("userid"));
        datedata.setText(map.get("date"));
        super.onResume();
    }
}