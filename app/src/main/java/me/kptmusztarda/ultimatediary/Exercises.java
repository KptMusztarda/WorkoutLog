package me.kptmusztarda.ultimatediary;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public class Exercises extends Application{

    private static List<String> names;
    private Context context;
    private Resources res;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        res = context.getResources();
        names = new ArrayList<String>() {{
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
    }


    protected static int getCount() {
        return names.size();
    }

    protected static String getName(int id) {
        return names.get(id);
    }

}
