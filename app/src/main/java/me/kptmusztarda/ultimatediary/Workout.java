package me.kptmusztarda.ultimatediary;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class Workout extends Activity {

    private Button addset, weightp, weightm, repsp, repsm, cancel;
    private EditText weight, reps;
    private LinearLayout[] scroll;
    private ConstraintLayout constraintLayout;
    private TextView modifiedTView;
    private RadioGroup radioGroup;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean inEditMode = false;
    private int selectedExerciseId;
    private float increment = 2.5f;
    private List<Day> days;
    private Set modifiedSet;

    private Locale LOCALE = Locale.getDefault();
    private static final String TAG = MainActivity.class.getName();
    private String DATE_FORMAT = "dd/MM/YYYY";

    private void createDateRow(int pos, Calendar date) {
        TextView tView = new TextView(getApplicationContext());
        tView.setText(String.format(LOCALE, "-%dd %s",
                (Calendar.getInstance(LOCALE).getTimeInMillis() - date.getTimeInMillis()) / (24 * 60 * 60 * 1000),
                new SimpleDateFormat(DATE_FORMAT, LOCALE).format(date.getTime())
        ));
        scroll[pos].addView(tView, (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)));
    }
    private void createWorkoutRow(int pos, final Set s) {
        final TextView tView = new TextView(getApplicationContext());
        tView.setTag(s.getId());
        tView.setText(String.format(LOCALE, "%sx%d",
                Data.trimZeros(s.getWeight()),
                s.getReps()
        ));
        tView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                inEditMode = true;
                Log.i(TAG, "set = " + s.getWeight() + "x" + s.getReps());
                tView.setBackgroundColor(getResources().getColor(R.color.accent));
                weight.setText(Float.toString(s.getWeight()));
                reps.setText(Integer.toString(s.getReps()));
                addset.setText(getResources().getText(R.string.edit_set));
                cancel.setVisibility(View.VISIBLE);
                updateEditTexts(s);
                modifiedSet = s;
                if(modifiedTView != null) modifiedTView.setBackgroundColor(getResources().getColor(R.color.secondary));
                modifiedTView = tView;

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.clear(addset.getId(), ConstraintSet.LEFT);
                constraintSet.connect(cancel.getId(), ConstraintSet.LEFT, repsm.getId(), ConstraintSet.LEFT);
                constraintSet.applyTo(constraintLayout);

                return false;
            }
        });
        float scale = getResources().getDisplayMetrics().density;
        tView.setPadding( (int)(20 * scale + 0.5f), (int)(1 * scale + 0.5f), 0, (int)(1 * scale + 0.5f) );
        scroll[pos].addView(tView, (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)));
    }
    private void exitEditMode() {
        inEditMode = false;
        Log.i(TAG, "Exit edit mode");
        cancel.setVisibility(View.INVISIBLE);
        modifiedTView.setBackgroundColor(getResources().getColor(R.color.secondary));
        addset.setText(getResources().getText(R.string.add_set));
        updateEditTexts(Data.getLastSet(selectedExerciseId));

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(addset.getId(), ConstraintSet.LEFT, repsm.getId(), ConstraintSet.LEFT);
        constraintSet.applyTo(constraintLayout);
    }
    private void createRows(int pos) {
        for (Day day : Data.getDays()) {
            List<Set> sets = day.getSets();
            int x = 0;
            for (int i = 0; i < sets.size(); i++) {
                if (sets.get(i).getExerciseId() == pos && x == 0) {
                    x++;
                    createDateRow(pos, day.getDate());
                }
                if (sets.get(i).getExerciseId() == pos) {
                    createWorkoutRow(pos, sets.get(i));
                }
            }
            /*

            body weight

             */
        }
    }
    private void updateEditTexts(Set s) {
        if(s != null) {
            weight.setText(Float.toString(s.getWeight()));
            reps.setText(Integer.toString(s.getReps()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);

        Data.loadData();
        days = Data.getDays();

        scroll = new LinearLayout[Exercises.getCount()];
        constraintLayout = findViewById(R.id.constraintlayout);

        tabLayout = findViewById(R.id.dots);
        for (int i = 0; i < Exercises.getCount(); i++) {
            tabLayout.addTab(tabLayout.newTab());
        }

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(Exercises.getCount() - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG, "Scrolled: " + Integer.toString(position));
                selectedExerciseId = position;
                updateEditTexts(Data.getLastSet(position));
            }
            @Override
            public void onPageSelected(int position) {
                //Log.i(TAG, "Selected: " + Integer.toString(position));
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.i(TAG, "ScrollStateChanged: " + Integer.toString(state));
            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Exercises.getCount();
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object == view;
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.i(TAG, "instantiateItem");

                ConstraintLayout layout = new ConstraintLayout(viewPager.getContext());
                layout.setId(View.generateViewId());
                container.addView(layout);

                ImageView iView = new ImageView(container.getContext());
                iView.setImageDrawable(Exercises.getDrawablee(position));
                iView.setId(View.generateViewId());
                iView.setAdjustViewBounds(true);

                TextView tView = new TextView(container.getContext());
                tView.setText(Exercises.getName(position));
                tView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                tView.setId(View.generateViewId());

                ScrollView sView = new ScrollView(container.getContext());
                sView.setId(View.generateViewId());
                sView.setBackgroundResource(R.color.secondary);
                scroll[position] = new LinearLayout(container.getContext());
                scroll[position].setOrientation(LinearLayout.VERTICAL);
                sView.addView(scroll[position]);

                layout.addView(iView, 0);
                layout.addView(tView, 1);
                layout.addView(sView, 2);
                ConstraintSet set = new ConstraintSet();
                set.clone(layout);

                set.constrainDefaultHeight(layout.getId(), container.getHeight());
                set.constrainDefaultWidth(layout.getId(), container.getWidth());

                float scale = getResources().getDisplayMetrics().density;


                set.connect(iView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
                set.connect(iView.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
                set.connect(iView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
                set.connect(iView.getId(), ConstraintSet.RIGHT, sView.getId(), ConstraintSet.LEFT);
                set.constrainWidth(iView.getId(), (int)(160 * scale + 0.5f));
                set.setHorizontalBias(iView.getId(), 0.3f);
                set.setVerticalBias(iView.getId(), 0.4f);

                set.connect(tView.getId(), ConstraintSet.TOP, iView.getId(), ConstraintSet.BOTTOM,32);
                set.connect(tView.getId(), ConstraintSet.LEFT, iView.getId(), ConstraintSet.LEFT);
                set.connect(tView.getId(), ConstraintSet.RIGHT, iView.getId(), ConstraintSet.RIGHT);

                set.constrainDefaultWidth(sView.getId(), container.getWidth() / 2);
                set.connect(sView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 8);
                set.connect(sView.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 8);
                set.setHorizontalBias(sView.getId(), 1f);

                set.applyTo(layout);

                createRows(position);

                return layout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Log.i(TAG, "DestroyItem");
                container.removeView((ConstraintLayout) object);
            }
        });
        tabLayout.setupWithViewPager(viewPager, true); // <- magic here

        final Calendar now = Calendar.getInstance(LOCALE);
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        final boolean shouldAddDateRow[] = new boolean[Exercises.getCount()];
        for (int i=0; i<shouldAddDateRow.length-1; i++) shouldAddDateRow[i] = true;

        final int index[] = new int[1];
        if (days.size() > 0) {
            index[0] = days.size() - 1;
            if (days.get(index[0]).getDate().before(now)) {
                days.add(new Day(now, true));
                index[0]++;
            } else {
                List<Set> sets = days.get(index[0]).getSets();
                for (int i = 0; i< sets.size() - 1; i++) {
                    shouldAddDateRow[sets.get(i).getExerciseId()] = false;
                }
            }
        } else {
            days.add(new Day(now, true));
            index[0] = 0;
        }

        addset = findViewById(R.id.addset);
        addset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                float weightF = Float.valueOf(weight.getText().toString());
                int repsI = Integer.valueOf(reps.getText().toString());
                Set set = new Set(selectedExerciseId, weightF, repsI);

                if(!inEditMode) {
                    Log.i(TAG, Boolean.toString(shouldAddDateRow[selectedExerciseId]));
                    if (shouldAddDateRow[selectedExerciseId]) {
                        createDateRow(selectedExerciseId, now);
                        shouldAddDateRow[selectedExerciseId] = false;
                    }
                    createWorkoutRow(selectedExerciseId, set);
                    days.get(index[0]).addSet(set, true);
                } else {
                    modifiedSet.setReps(repsI);
                    modifiedSet.setWeight(weightF);
                    modifiedTView.setText(String.format(LOCALE, "%sx%d",
                            Data.trimZeros(weightF),
                            repsI));
                    Data.rewriteFile();
                    exitEditMode();
                }
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEditMode();
            }
        });

        //weight and reps setting

        radioGroup = findViewById(R.id.incements);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.increment0) increment = 1.25f;
                else if (i == R.id.increment1) increment = 2.5f;
                else if (i == R.id.increment2) increment = 5f;
                else if (i == R.id.increment3) increment = 10f;
            }
        });

        weight = findViewById(R.id.weight);
        weight.setText("50.0");
        reps = findViewById(R.id.reps);
        reps.setText("8");

        weightp = findViewById(R.id.weightplus);
        weightp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weight.setText(Float.toString(Float.valueOf(weight.getText().toString()) + increment));
            }
        });
        weightm = findViewById(R.id.weightminus);
        weightm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weight.setText(Float.toString(Float.valueOf(weight.getText().toString()) - increment));
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
