package me.kptmusztarda.workoutlog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Day{

    private Calendar date;
    private List<Set> sets;
    private float bodyWeight;
    private Locale LOCALE = Locale.getDefault();

    protected Day(Calendar date, boolean append) {
        sets = new ArrayList<>();
        this.date = date;
        if (append) {
            Data.appendToFile(0, new SimpleDateFormat("YYYY/MM/dd", LOCALE).format(date.getTime()));
        }
    }

    protected Calendar getDate() {
        return date;
    }
    protected void addSet(Set set, boolean append) {
        sets.add(set);
        if(append) Data.appendToFile(1,set.getExerciseId() + ";" + set.getWeight() + "x" + set.getReps());
    }
    protected List<Set> getSets() {
        return sets;
    }
    protected Set getLastSet(int exerciseId) {
        return sets.get(exerciseId);
    }
    protected void setBodyWeight(float bodyWeight, boolean append) {
        this.bodyWeight = bodyWeight;
        if(append) Data.appendToFile(2, Data.trimZeros(bodyWeight));
    }
    protected float getBodyWeight() {
        return bodyWeight;
    }
    protected void deleteSet(int id) {
        sets.remove(id);
    }
}
