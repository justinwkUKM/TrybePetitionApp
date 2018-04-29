package com.trybe.project.petitionapp.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.trybe.project.petitionapp.BuildConfig;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public final static  int PREFS_NORMAL_RUN = 0;
    public final static  int PREFS_FIRST_RUN = 1;
    public final static  int PREFS_UPGRADE_RUN = 2;

    public void checkFirstRun(Context context) {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            FirebaseCrash.log("First Run");
            Toast.makeText(context, "First Run, Can Show OnBoarding Screen Here", Toast.LENGTH_LONG).show();
            // TODO This is a new install (or the user cleared the shared preferences)

        } else if (currentVersionCode > savedVersionCode) {
            Toast.makeText(context, "upgrade Run", Toast.LENGTH_SHORT).show();

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
}
