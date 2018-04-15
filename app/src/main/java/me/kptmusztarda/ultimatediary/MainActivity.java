package me.kptmusztarda.ultimatediary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {

    private TabHost host;
    private Resources res;
    private Button button;
    private Data data;
    private Activity activity = this;


    private static final String TAG = MainActivity.class.getName();
    private String PATH = "";
    private String FILENAME = "WorkoutLog.txt";


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission1 != 0 || permission2 != 0) ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 2137);


        host = findViewById(R.id.tabHost);
        host.setup();

        res = getResources();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator(res.getString(R.string.tab1));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator(res.getString(R.string.tab2));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator(res.getString(R.string.tab3));
        host.addTab(spec);

        data = new Data(PATH,FILENAME);
        data.loadData();

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, Workout.class);
                intent.putExtra("Data", data);
                startActivity(intent);
            }
        });

    }

}
