package me.kptmusztarda.workoutlog;

public class Set {

    private float weight;
    private int reps;
    private int exerciseId;
    private int id;

    protected Set(int exerciseId, float weight, int reps) {
        this.weight = weight;
        this.reps = reps;
        this.exerciseId = exerciseId;
    }

    protected int getReps() {return reps;}
    protected float getWeight() {return weight;}
    protected void setReps(int reps) {this.reps = reps;}
    protected void setWeight(float weight) {this.weight = weight;}
    protected int getExerciseId(){return exerciseId;}
    protected int getId(){return id;}
    protected int setId(int i){return id = i;}
}
