package me.kptmusztarda.ultimatediary;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TabHost host;
    private Resources res;
    private Button button, weightp, weightm, repsp, repsm;
    private EditText weight, reps;
    private LinearLayout scroll;
    private TextView tView;
    private File file;
    private Locale LOCALE = Locale.getDefault();
    private static final String TAG = MainActivity.class.getName();

    private static final int DATE = 0;
    private static final int WEIGHT_DATA = 1;
    private static final int WORKOUT_DATA = 2;


    public static String trimZeros(float f) {
        if(f == (long) f)
            return String.format("%d",(long)f);
        else
            return String.format("%s",f);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) android.os.Process.killProcess(android.os.Process.myPid());
    }
    public void initializeFile(String path, String name) {
        File root = android.os.Environment.getExternalStorageDirectory();
        System.out.println("External file system root: " + root);

        File dir = new File(root.getAbsolutePath());
        dir.mkdirs();
        file = new File(root + path, name);
    }
    public void appendToFile(String row){
        try {
            FileOutputStream f = new FileOutputStream(file,true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(row);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "File not found. Did you add a WRITE_EXTERNAL_STORAGE permissionGranted to the manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"File written to " + file);
    }
    private void readFromFile(){
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);
            try {
                String string;
                while (true){
                    System.out.println("Something's there");
                    string = br.readLine();
                    if(string == null) break;
                    Log.i(TAG,string.substring(0,1));
                    switch (Integer.valueOf(string.substring(0,1))) {
                        case DATE:
                            Log.i(TAG, "case DATE");
                            Log.i(TAG, string);
                            Calendar cal = Calendar.getInstance(LOCALE);
                            createDateRow(( Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis() ) / ( 1000*60*60*24 ),
                                    new SimpleDateFormat("E", LOCALE).format(cal.getTime()));
                            break;

                        case WORKOUT_DATA:
                            Log.i(TAG, "case WORKOUT_DATA");
                            Log.i(TAG, string);
                            int i;
                            createWorkoutRow(string.substring(string.indexOf(";") + 1, i = string.indexOf("x")) + string.substring(i, string.length()-1));
                    }

//                    Calendar cal =Calendar.getInstance(LOCALE);
//                    cal.setTime(new SimpleDateFormat("MM/dd/yyyy", LOCALE).parse(string.substring(0,10)));
//                    System.out.println(cal.toString());
//                    System.out.println(string.substring(0,10));
//                    int index;
////                    createWorkoutRow(String.format(Locale.ENGLISH,"-%dd %s %s",
////                            ( Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis() ) / ( 1000*60*60*24 ),
////                            new SimpleDateFormat("E", LOCALE).format(cal.getTime()),
////                            String.format(LOCALE,"%12sx%s",
////                                    string.substring(index = string.indexOf(";") + 1,index = string.indexOf(";", index)),
////                                    string.substring(index+=1,string.indexOf(";", index))))
////                    );
                }
                isr.close();
                is.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    private void createDateRow(Long l, String s) {
        tView = new TextView(getApplicationContext());
        tView.setText(String.format(LOCALE, "-%dD %s", l, s));
        scroll.addView(tView, (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)));
    }

    private void createWorkoutRow(String s) {
        tView = new TextView(getApplicationContext());
        tView.setText(s);
        float scale = getResources().getDisplayMetrics().density;
        tView.setPadding( (int)(20*scale + 0.5f),0,0,0);
        scroll.addView(tView, (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)));
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

        //workout history
        scroll = findViewById(R.id.scroll);
//        tViews = new TextView[5];
//        tViewsCount = 0;

        final SharedPreferences sharedPref = getSharedPreferences("diaryPrefs", Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = sharedPref.edit();

        initializeFile("","File.txt");
        readFromFile();

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                createWorkoutRow(String.format("-0d %s %s",
                        new SimpleDateFormat("E", LOCALE).format(Calendar.getInstance(LOCALE).getTime()),
                        String.format("%12sx%s",trimZeros(Float.valueOf(weight.getText().toString())),
                                reps.getText().toString())
                ));

                try {
                    Calendar cal =Calendar.getInstance(LOCALE);
                    cal.setTime(new SimpleDateFormat("MM/dd/yyyy", LOCALE).parse(sharedPref.getString("date","01/01/1970")));

                    if(( Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis() ) / ( 1000*60*60*24 ) > 0) {
                        Log.w(TAG, "New day");
                        String s;
                        appendToFile(String.format(LOCALE, "%d;%s",
                                DATE,
                                s = new SimpleDateFormat("MM/dd/yyyy", LOCALE).format(Calendar.getInstance(LOCALE).getTime())
                        ));
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        Log.i(TAG, s);
                        editor.putString("date", s);
                        editor.commit();
                    }
                } catch (ParseException e) {e.printStackTrace();}

                appendToFile(String.format(LOCALE, "%d;%sx%s;",
                        WORKOUT_DATA,
                        trimZeros(Float.valueOf(weight.getText().toString())),
                        reps.getText().toString()
                ));
            }
        });

        //weight and reps setting
        weight = findViewById(R.id.weight);
        weight.setText("50.0");
        reps = findViewById(R.id.reps);
        reps.setText("8");

        weightp = findViewById(R.id.weightplus);
        weightp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weight.setText(Float.toString(Float.valueOf(weight.getText().toString()) + 5));
            }
        });
        weightm = findViewById(R.id.weightminus);
        weightm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weight.setText(Float.toString(Float.valueOf(weight.getText().toString()) - 5));
            }
        });

        repsp = findViewById(R.id.repsplus);
        repsp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reps.setText(Integer.toString(Integer.valueOf(reps.getText().toString()) + 1));
            }
        });
        repsm = findViewById(R.id.repsminus);
        repsm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reps.setText(Integer.toString(Integer.valueOf(reps.getText().toString()) - 1));
            }
        });


    }

}
