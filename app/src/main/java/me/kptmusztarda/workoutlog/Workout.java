package me.kptmusztarda.workoutlog;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Workout extends Activity {

    private ImageButton addset, weightp, weightm, repsp, repsm, cancel, delete;
    private EditText weight, reps;
    private LinearLayout[] scrollLinearLayout;
    private ScrollView[] scrollScrollView;
    private ConstraintLayout constraintLayout;
    private TextView modifiedTView;
    private RadioGroup radioGroup;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean inEditMode = false;
    private int selectedExerciseId;
    private int selectedPosition;
    private int exercisesIds[];
    private float increment = 2.5f;
    private List<Day> days;
    private Set modifiedSet;

    private Locale LOCALE = Locale.getDefault();
    private static final String TAG = MainActivity.class.getName();
    private String DATE_FORMAT = "dd/MM/YYYY";

    private void createDateRow(int position, Calendar date) {
        TextView tView = new TextView(getApplicationContext());
        tView.setText(String.format(LOCALE, "-%dd %s",
                (Calendar.getInstance(LOCALE).getTimeInMillis() - date.getTimeInMillis()) / (24 * 60 * 60 * 1000),
                new SimpleDateFormat(DATE_FORMAT, LOCALE).format(date.getTime())
        ));
        scrollLinearLayout[position].addView(tView, (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)));
    }
    private void createWorkoutRow(int position, final Set s) {
        final TextView tView = new TextView(getApplicationContext());
        int id = s.setId(View.generateViewId());
        tView.setId(id);
        //Log.i(TAG, Integer.toString(tView.getId()));
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
                cancel.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
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
        scrollLinearLayout[position].addView(tView, (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)));
    }
    private void exitEditMode() {
        inEditMode = false;
        Log.i(TAG, "Exit edit mode");
        cancel.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        modifiedTView.setBackgroundColor(getResources().getColor(R.color.secondary));
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
                if (sets.get(i).getExerciseId() == exercisesIds[pos] && x == 0) {
                    x++;
                    createDateRow(pos, day.getDate());
                }
                if (sets.get(i).getExerciseId() == exercisesIds[pos]) {
                    createWorkoutRow(pos, sets.get(i));
                }
            }
        }
    }
    private void updateEditTexts(Set s) {
        if(s != null) {
            weight.setText(Float.toString(s.getWeight()));
            reps.setText(Integer.toString(s.getReps()));
        }
    }
    private Calendar getCurrentDay() {
        final Calendar now = Calendar.getInstance(LOCALE);
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now;
    }
    private boolean[] shouldAddDateRow() {
        Calendar now = getCurrentDay();
        final boolean shouldAddDateRow[] = new boolean[Definitions.getExerciseSize()];
        for (int i=0; i<shouldAddDateRow.length; i++) shouldAddDateRow[i] = true;

        final int index[] = new int[1];
        if (days.size() > 0) {
            index[0] = days.size() - 1;
            if (days.get(index[0]).getDate().before(now)) {
                days.add(new Day(now, true));
                index[0]++;
            } else {
                List<Set> sets = days.get(index[0]).getSets();
                for (int i=0; i< sets.size(); i++) {
                    shouldAddDateRow[sets.get(i).getExerciseId()] = false;
                }
            }
        } else {
            days.add(new Day(now, true));
            index[0] = 0;
        }
        return shouldAddDateRow;
    }
    private void playAnimation(ImageButton b) {
        Drawable drawable = b.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);

        Data.loadData();
        days = Data.getDays();
        exercisesIds = getIntent().getIntArrayExtra("ids");

        scrollLinearLayout = new LinearLayout[Definitions.getExerciseSize()];
        scrollScrollView = new ScrollView[Definitions.getExerciseSize()];
        constraintLayout = findViewById(R.id.constraintlayout_workout);
        tabLayout = findViewById(R.id.dots);
        for (int i = 0; i < Definitions.getExerciseSize(); i++) {
            tabLayout.addTab(tabLayout.newTab());
        }

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(exercisesIds.length - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG, "Scrolled: " + Integer.toString(position));
                selectedExerciseId = exercisesIds[position];
                selectedPosition = position;
                updateEditTexts(Data.getLastSet(exercisesIds[position]));
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
                return exercisesIds.length;
            }
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object == view;
            }
            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                Log.i(TAG, "instantiateItem");

                ConstraintLayout layout = new ConstraintLayout(viewPager.getContext());
                layout.setId(View.generateViewId());
                container.addView(layout);

                ImageView iView = new ImageView(container.getContext());
                iView.setImageDrawable(Definitions.getExerciseDrawable(exercisesIds[position]));
                iView.setId(View.generateViewId());
                iView.setAdjustViewBounds(true);

                TextView tView = new TextView(container.getContext());
                tView.setText(Definitions.getExerciseName(exercisesIds[position]));
                tView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                tView.setId(View.generateViewId());

                scrollScrollView[position] = new ScrollView(container.getContext());
                scrollScrollView[position].setId(View.generateViewId());
                scrollScrollView[position].setBackgroundResource(R.color.secondary);
                scrollLinearLayout[position] = new LinearLayout(container.getContext());
                scrollLinearLayout[position].setOrientation(LinearLayout.VERTICAL);
                scrollScrollView[position].addView(scrollLinearLayout[position]);

                layout.addView(iView, 0);
                layout.addView(tView, 1);
                layout.addView(scrollScrollView[position], 2);
                ConstraintSet set = new ConstraintSet();
                set.clone(layout);

                set.constrainDefaultHeight(layout.getId(), container.getHeight());
                set.constrainDefaultWidth(layout.getId(), container.getWidth());

                float scale = getResources().getDisplayMetrics().density;


                set.connect(iView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
                set.connect(iView.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
                set.connect(iView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
                set.connect(iView.getId(), ConstraintSet.RIGHT, scrollScrollView[position].getId(), ConstraintSet.LEFT);
                set.constrainWidth(iView.getId(), (int)(160 * scale + 0.5f));
                set.setHorizontalBias(iView.getId(), 0.3f);
                set.setVerticalBias(iView.getId(), 0.4f);

                set.connect(tView.getId(), ConstraintSet.TOP, iView.getId(), ConstraintSet.BOTTOM,32);
                set.connect(tView.getId(), ConstraintSet.LEFT, iView.getId(), ConstraintSet.LEFT);
                set.connect(tView.getId(), ConstraintSet.RIGHT, iView.getId(), ConstraintSet.RIGHT);

                set.constrainDefaultWidth(scrollScrollView[position].getId(), container.getWidth() / 2);
                set.connect(scrollScrollView[position].getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 8);
                set.connect(scrollScrollView[position].getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT, 8);
                set.setHorizontalBias(scrollScrollView[position].getId(), 1f);

                set.applyTo(layout);

                createRows(position);
                scrollLinearLayout[position].post(new Runnable() {
                    @Override
                    public void run() {
                        scrollScrollView[position].fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

                return layout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Log.i(TAG, "DestroyItem");
                container.removeView((ConstraintLayout) object);
            }
        });
        tabLayout.setupWithViewPager(viewPager, true); // <- magic here

        final boolean shouldAddDateRow[] = shouldAddDateRow();

        addset = findViewById(R.id.addset);
        addset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float weightF = Float.valueOf(weight.getText().toString());
                int repsI = Integer.valueOf(reps.getText().toString());
                Set set = new Set(selectedExerciseId, weightF, repsI);

                if(!inEditMode) {
                    int ind = 0;
                    if (shouldAddDateRow[selectedExerciseId]) {
                        createDateRow(selectedPosition, getCurrentDay());
                        shouldAddDateRow[selectedExerciseId] = false;

                    }
                    createWorkoutRow(selectedPosition, set);
                    days.get(days.size() - 1).addSet(set, true);
                } else {
                    modifiedSet.setReps(repsI);
                    modifiedSet.setWeight(weightF);
                    modifiedTView.setText(String.format(LOCALE, "%sx%d",
                            Data.trimZeros(weightF),
                            repsI));
                    Data.rewriteFile();
                    exitEditMode();
                }

                playAnimation(addset);
                scrollLinearLayout[selectedPosition].post(new Runnable() {
                    @Override
                    public void run() {
                        scrollScrollView[selectedPosition].fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEditMode();
            }
        });

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEditMode();
                int id = modifiedSet.getId();
                Data.deleteSet(id);
                Log.i(TAG, Integer.toString(modifiedSet.getId()));
                scrollLinearLayout[selectedPosition].removeView(findViewById(id));
                Data.rewriteFile();
            }
        });

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
                playAnimation(weightp);
            }
        });
        weightm = findViewById(R.id.weightminus);
        weightm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weight.setText(Float.toString(Float.valueOf(weight.getText().toString()) - increment));
                playAnimation(weightm);
            }
        });
        repsp = findViewById(R.id.repsplus);
        repsp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reps.setText(Integer.toString(Integer.valueOf(reps.getText().toString()) + 1));
                playAnimation(repsp);
            }
        });
        repsm = findViewById(R.id.repsminus);
        repsm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reps.setText(Integer.toString(Integer.valueOf(reps.getText().toString()) - 1));
                playAnimation(repsm);
            }
        });
    }
}
