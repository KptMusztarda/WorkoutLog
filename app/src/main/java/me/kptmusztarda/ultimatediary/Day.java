package me.kptmusztarda.ultimatediary;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Day implements Parcelable{

    private Calendar date;
    private List<Set> sets;
    private float bodyWeight;
    private Locale LOCALE = Locale.getDefault();

    protected Day(Parcel in) {
        date = Calendar.getInstance();
        date.setTimeInMillis(in.readLong());
        sets = new ArrayList<>();
        in.readList(sets, Set.class.getClassLoader());
        bodyWeight = in.readFloat();
    }

    protected Day(Calendar date, boolean append) {
        sets = new ArrayList<>();
        this.date = date;
        if (append) {
            Data.appendToFile(0, new SimpleDateFormat("YYYY/MM/dd", LOCALE).format(date.getTime()));
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date.getTimeInMillis());
        dest.writeList(sets);
        dest.writeFloat(bodyWeight);
    }

    public int describeContents() {return 0;}

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>(){
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

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
    protected void modifySet(int i, Set set) {
        sets.set(i, set);
    }
    protected void setBodyWeight(float bodyWeight, boolean append) {
        this.bodyWeight = bodyWeight;
        if(append) Data.appendToFile(2, Data.trimZeros(bodyWeight));
    }
    protected float getBodyWeight() {
        return bodyWeight;
    }
}
