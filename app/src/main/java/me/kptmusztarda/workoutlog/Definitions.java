package me.kptmusztarda.workoutlog;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Definitions extends Application{

    private static List<String> exerciseName;
    private static List<Drawable> ExerciseDrawables;
    private static List<String> trainingName;
    private static List<List> trainingExercise;
    private Context context;
    private Resources res;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        res = context.getResources();
        exerciseName = new ArrayList<String>() {{
            add(res.getString(R.string.exercise0));
            add(res.getString(R.string.exercise1));
            add(res.getString(R.string.exercise2));
            add(res.getString(R.string.exercise3));
            add(res.getString(R.string.exercise4));
            add(res.getString(R.string.exercise5));
            add(res.getString(R.string.exercise6));
            add(res.getString(R.string.exercise7));
            add(res.getString(R.string.exercise8));
        }};
        ExerciseDrawables = new ArrayList<Drawable>() {{
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.deadlift));
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.default_dot));
            add(res.getDrawable(R.drawable.default_dot));
        }};
        trainingName = new ArrayList<String>() {{
            add(res.getString(R.string.training_a));
            add(res.getString(R.string.training_b));
        }};
        trainingExercise = new ArrayList<List>() {{
            add(new ArrayList<Integer>() {{
                add(0);
                add(2);
                add(4);
                add(7);
                add(8);
            }});
            add(new ArrayList<Integer>() {{
                add(1);
                add(3);
                add(5);
                add(6);
                add(8);
            }});
        }};
    }

    protected static int getExerciseSize() {
        return exerciseName.size();
    }
    protected static String getExerciseName(int id) {
        return exerciseName.get(id);
    }
    protected static Drawable getExerciseDrawable(int id) {
        return ExerciseDrawables.get(id);
    }
    protected static int getTrainingsSize() {
        return trainingExercise.size();
    }
    protected static List getTraining(int id) {
        return trainingExercise.get(id);
    }
    protected static String getTrainingName(int id) {
        return trainingName.get(id);
    }
}
