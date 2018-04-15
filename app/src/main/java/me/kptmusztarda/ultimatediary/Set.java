package me.kptmusztarda.ultimatediary;

import android.os.Parcel;
import android.os.Parcelable;

public class Set implements Parcelable {

    private float weight;
    private int reps;
    private int id;

    protected Set(Parcel in) {
        weight = in.readFloat();
        reps = in.readInt();
        id = in.readInt();
    }

    protected Set(int id, float weight, int reps) {
        this.weight = weight;
        this.reps = reps;
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(weight);
        dest.writeInt(reps);
        dest.writeInt(id);
    }

    public int describeContents() {return 0;}

    public static final Parcelable.Creator<Set> CREATOR = new Parcelable.Creator<Set>(){
        public Set createFromParcel(Parcel in) {
            return new Set(in);
        }

        public Set[] newArray(int size) {
            return new Set[size];
        }
    };

    protected int getReps() {return reps;}
    protected float getWeight() {return weight;}
    protected void setReps(int reps) {this.reps = reps;}
    protected void setWeight(float weight) {this.weight = weight;}
    protected int getExerciseId(){return  id;}

}
