package me.kptmusztarda.workoutlog;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Data {

    private static List<Day> days = new ArrayList<>();
    private static String path;
    private static String name;

    private static Locale LOCALE = Locale.getDefault();
    private static final String TAG = MainActivity.class.getName();
    private static File file;
    private static final int DATE = 0;
    private static final int WORKOUT_DATA = 1;
    private static final int BODY_WEIGHT = 2;

    protected static void appendToFile(int type, String data){
        try {
            FileOutputStream f = new FileOutputStream(file,true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(type + ";" + data);
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
    protected static void rewriteFile() {
        if (file.exists()) {
            try {
                file.delete();
                file.createNewFile();
                FileOutputStream f = new FileOutputStream(file,true);
                PrintWriter pw = new PrintWriter(f);
                for(Day day : days) {
                    List<Set> sets= day.getSets();
                    if(sets.size() > 0) pw.println(DATE + ";" + new SimpleDateFormat("YYYY/MM/dd", LOCALE).format(day.getDate().getTime()));
                    if (day.getBodyWeight() != 0.0f) pw.println(BODY_WEIGHT + ";" + day.getBodyWeight());
                    for(Set set : sets) {
                        if(set.getReps() > 0 && set != null) pw.println(WORKOUT_DATA + ";" + set.getExerciseId() + ";" + set.getWeight() + "x" + set.getReps());
                    }
                }
                pw.flush();
                pw.close();
                f.close();
                Log.i(TAG, "File rewrited");
            } catch (IOException e) {
                Log.w(TAG, "Coś się spierdoliło");
            }
        }
    }
    protected static void loadData() {
        if (days != null) days.clear();
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr, 8192);
            try {
                String str;
                int currDay = -1;
                while ((str = br.readLine()) != null){
                    Log.i(TAG,"Something's in the file");
                    Log.i(TAG, str);

                    int ind = str.indexOf(";");

                    switch (Integer.valueOf(str.substring(0,ind))) {
                        case DATE:
                            Log.i(TAG, "case DATE");

                            int year = Integer.parseInt(str.substring(ind = ind+1,ind = str.indexOf("/",ind)));
                            int month = Integer.parseInt(str.substring(ind+1,ind = str.indexOf("/",ind + 1)));
                            int day = Integer.parseInt(str.substring(ind+1,str.length()));

                            Log.i(TAG, Integer.toString(year));
                            Log.i(TAG, Integer.toString(month));
                            Log.i(TAG, Integer.toString(day));

                            Calendar cal = Calendar.getInstance(LOCALE);
                            cal.set(year, month-1, day, 0, 0, 0);
                            cal.set(Calendar.HOUR, 0);
                            cal.set(Calendar.MILLISECOND, 0);

                            days.add(new Day(cal, false));
                            currDay++;

                            break;
                        case WORKOUT_DATA:
                            Log.i(TAG, "case WORKOUT_DATA");

                            int id = Integer.parseInt(str.substring(ind+1, ind = str.indexOf(";",ind+1)));
                            float weight = Float.parseFloat(str.substring(ind+1,ind = str.indexOf("x",ind+1)));
                            int reps = Integer.parseInt(str.substring(ind+1, str.length()));

                            days.get(currDay).addSet(new Set(id, weight, reps), false);

                            break;
                        case BODY_WEIGHT:
                            Log.i(TAG, "case BODY_WEIGHT");

                            days.get(currDay).setBodyWeight(Float.parseFloat(str.substring(ind+1,str.length())), false);

                            break;
                    }
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
    protected static void initializeFile(String path, String name) {
        File root = android.os.Environment.getExternalStorageDirectory();
        System.out.println("External file system root: " + root);

        File dir = new File(root.getAbsolutePath());
        dir.mkdirs();
        file = new File(root + path, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.i(TAG, "File created");
            } catch (IOException e) {
                Log.w(TAG, "Coś się spierdoliło");
            }
        }
    }
    protected static String trimZeros(float f) {
        if(f == (long) f)
            return String.format(LOCALE,"%d",(long)f);
        else
            return String.format("%s",f);
    }

    protected static List<Day> getDays() {return days;}
    protected static Set getLastSet(int id) {
        Set toReturn = null;
        outerLoop:
        for(int i=days.size()-1; i>=0; i--) {
            List<Set> sets = days.get(i).getSets();
            for (int j = sets.size()-1; j>=0; j--) if(sets.get(j).getExerciseId() == id) {
                toReturn = sets.get(j);
                break outerLoop;
            }
        }
        return toReturn;
    }
    protected static void deleteSet(int id) {
        outerLoop:
        for(int i=days.size()-1; i>=0; i--) {
            List<Set> sets = days.get(i).getSets();
            for (int j = sets.size()-1; j>=0; j--) if(sets.get(j).getId() == id) {
                sets.remove(j);
                break outerLoop;
            }
        }
    }
}
