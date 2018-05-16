package me.kptmusztarda.workoutlog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {

    private TabHost host;
    private Resources res;
    private Button button;
    private ConstraintLayout constraintLayoutWorkout;
    private ConstraintLayout[] addedConstraintLayouts;
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

        host.setCurrentTab(1);

        Data.initializeFile(PATH, FILENAME);
        Data.loadData();

        constraintLayoutWorkout = findViewById(R.id.constraint_layout_main_workout);
        addedConstraintLayouts = new ConstraintLayout[Definitions.getTrainingsSize()];
        float scale = getResources().getDisplayMetrics().density;
        for(int i=0; i<Definitions.getTrainingsSize(); i++) {

            Log.i(TAG, "Training: " + i);

            addedConstraintLayouts[i] = new ConstraintLayout(this);
            addedConstraintLayouts[i].setId(View.generateViewId());
            addedConstraintLayouts[i].setBackgroundColor(getResources().getColor(R.color.accent));
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams((int)(320 * scale + 0.5f), ViewGroup.LayoutParams.WRAP_CONTENT);
            addedConstraintLayouts[i].setLayoutParams(lp);

            TextView name = new TextView(this);
            name.setText(Definitions.getTrainingName(i));
            name.setId(View.generateViewId());
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

            Button button = new Button(this);
            button.setText("Start");
            button.setId(View.generateViewId());
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            final int id = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Workout.class);
                    List<Integer> list = Definitions.getTraining(id);
                    int[] array = new int[list.size()];
                    for (int j=0; j<list.size(); j++) {
                        array[j] = list.get(j);
                    }
                    intent.putExtra("ids", array);
                    startActivity(intent);
                }
            });

            addedConstraintLayouts[i].addView(name, 0);
            addedConstraintLayouts[i].addView(button, 1);

            ConstraintSet set = new ConstraintSet();
            set.clone(addedConstraintLayouts[i]);

            set.centerHorizontally(name.getId(), addedConstraintLayouts[i].getId());
            set.setHorizontalBias(name.getId(), 0.2f);
            set.centerVertically(name.getId(), addedConstraintLayouts[i].getId());
            set.setVerticalBias(name.getId(), 0.1f);
            set.centerHorizontally(button.getId(), addedConstraintLayouts[i].getId());
            set.setHorizontalBias(button.getId(), 0.8f);
            set.centerVertically(button.getId(), addedConstraintLayouts[i].getId());
            set.setVerticalBias(button.getId(), 0.1f);

            set.applyTo(addedConstraintLayouts[i]);

            constraintLayoutWorkout.addView(addedConstraintLayouts[i], 0);
            set.clone(constraintLayoutWorkout);

            set.centerHorizontally(addedConstraintLayouts[i].getId(), constraintLayoutWorkout.getId());
            if (i==0) set.connect(addedConstraintLayouts[i].getId(), ConstraintSet.TOP, constraintLayoutWorkout.getId(),ConstraintSet.TOP,48);
            else set.connect(addedConstraintLayouts[i].getId(), ConstraintSet.TOP, addedConstraintLayouts[i-1].getId(), ConstraintSet.BOTTOM,48);

            set.applyTo(constraintLayoutWorkout);
        }
    }

}
