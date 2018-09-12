package elshab7.engineering.ratethisapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Date;

class SharedPrefUtil {
    private static final String APP_PREFS = "elshab7RateAppDialog_preferences";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private final Context mContext;

    SharedPrefUtil(Context context) {
        mContext=context;
        this.mSharedPreferences = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    void setInstallDate() {
        Date installDate = new Date();
        try {
            PackageInfo pkgInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            installDate = new Date(pkgInfo.firstInstallTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mEditor = mSharedPreferences.edit();
        mEditor.putLong("InstallDate", installDate.getTime());
        mEditor.apply();
    }
    Long getInstallDate() {
        return mSharedPreferences.getLong("InstallDate",0L);
    }

    void setAskLaterDate() {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong("AskLaterDate", System.currentTimeMillis());
        mEditor.apply();
    }
    Long getAskLaterDate() {
        return mSharedPreferences.getLong("AskLaterDate",0L);
    }

    void setLaunchTimes(int launchTimes) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt("LaunchTimes", launchTimes);
        mEditor.apply();
    }
    int getLaunchTimes() {
        return mSharedPreferences.getInt("LaunchTimes",0);
    }

    void setRateOptionOut(boolean rateOptionOut) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("RateOptionOut", rateOptionOut);
        mEditor.apply();
    }
    Boolean getRateOptionOut() {
        return mSharedPreferences.getBoolean("RateOptionOut",false);
    }

    void clearSharedPreferences(){
        mEditor = mSharedPreferences.edit();
        mEditor.remove("LaunchTimes");
        mEditor.remove("AskLaterDate");
        mEditor.remove("RateOptionOut");
        mEditor.apply();
    }
}
