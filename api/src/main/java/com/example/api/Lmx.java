package com.example.api;

import android.app.Activity;


/**
 * Created by Administrator on 2018/4/10 0010.
 */

public class Lmx {
    public static void bind(Activity activity) {
        try {
            String className = activity.getClass().getName() + "_InjectExtra";
            Class<?> Class = java.lang.Class.forName(className);
            InjectExtra injectExtra = (InjectExtra) Class.newInstance();
            injectExtra.inject(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
