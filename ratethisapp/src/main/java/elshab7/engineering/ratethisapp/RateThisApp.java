package elshab7.engineering.ratethisapp;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * RateThisApp<br>
 * A library to Show the app rate dialog
 * @author Keisuke Kobayashi (k.kobayashi.122@gmail.com)
 *
 */
public class RateThisApp {

    private static final String TAG = RateThisApp.class.getSimpleName();

    private int mCriteriaInstallDays=7;
    private int mCriteriaLaunchTimes=10;

    private static Date mInstallDate = new Date();
    private static int mLaunchTimes = 0;
    private static boolean mOptOut = false;
    private static Date mAskLaterDate = new Date();

    private static RateAppCallback sCallback = null;
    // Weak ref to avoid leaking the context
    private static WeakReference<AlertDialog> sDialogRef = null;
    private SharedPrefUtil mSharedPrefUtil;
    private Context mContext;
    private String mTitle,mMessage;
    private String mPositiveBtnTxt,mNegativeBtnTxt,mNeutralBtnTxt;
    private int mIcon=0;
    private boolean isNegativeIgnored;

    private RateThisApp(Context context) {
        mContext=context;
        mSharedPrefUtil=new SharedPrefUtil(mContext);
        // If it is the first launch, save the date in shared preference.
        if (mSharedPrefUtil.getInstallDate() == 0L) {
            mSharedPrefUtil.setInstallDate();
        }
        // Increment launch times
        mSharedPrefUtil.setLaunchTimes(mSharedPrefUtil.getLaunchTimes()+1);

        mInstallDate = new Date(mSharedPrefUtil.getInstallDate());
        mLaunchTimes = mSharedPrefUtil.getLaunchTimes();
        mOptOut = mSharedPrefUtil.getRateOptionOut();
        mAskLaterDate = new Date(mSharedPrefUtil.getAskLaterDate());

        mTitle="Rate This App";
        mMessage="Please rate this app to encourage us to improve this app.";
        mPositiveBtnTxt="Rate Now";
        mNegativeBtnTxt="No, Thanks";
        mNeutralBtnTxt="Cancel";
        isNegativeIgnored=true;
    }

    public static RateThisApp Initialize(Context context) {
        return new RateThisApp(context);
    }

    public RateThisApp setCallback(RateAppCallback rateAppCallback) {
        sCallback = rateAppCallback;
        return this;
    }

    public RateThisApp setTitle(String title) {
        mTitle=title;
        return this;
    }

    public RateThisApp setMessage(String message) {
        mMessage=message;
        return this;
    }

    public RateThisApp setPositiveBtnTxt(String positiveBtnTxt) {
        mPositiveBtnTxt=positiveBtnTxt;
        return this;
    }

    public RateThisApp setNegativeBtnTxt(String negativeBtnTxt) {
        mNegativeBtnTxt=negativeBtnTxt;
        return this;
    }

    public RateThisApp setNeutralBtnTxt(String neutralBtnTxt) {
        mNeutralBtnTxt=neutralBtnTxt;
        return this;
    }

    public RateThisApp setIcon(int icon) {
        mIcon=icon;
        return this;
    }

    public RateThisApp setNegativeIgnored(boolean negativeIgnored) {
        isNegativeIgnored =negativeIgnored;
        return this;
    }

    public RateThisApp setThresholdLaunchTimes(int times) {
        mCriteriaLaunchTimes=times;
        return this;
    }

    public RateThisApp setThresholdInstallDays(int days) {
        mCriteriaInstallDays=days;
        return this;
    }

    public static void showDefaultDialog(final Context context){
        Initialize(context)
                .setCallback(new RateAppCallback() {
                    @Override
                    public void onYesClicked() {
                        String appPackage = context.getPackageName();
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage)));
                        } catch (ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackage)));
                        }
                    }

                    @Override
                    public void onNoClicked() {

                    }

                    @Override
                    public void onCancelClicked() {

                    }
                })
                .showRateDialogIfNeeded();
    }

    public void showRateDialogIfNeeded() {
        if (shouldShowRateDialog()) {
            showRateDialog();
        }
    }

    public void forceShowRateDialog() {
        showRateDialog();
    }


    private boolean shouldShowRateDialog() {
        if (mOptOut) {
            return false;
        } else {
            if (mLaunchTimes >= mCriteriaLaunchTimes) {
                return true;
            }
            long threshold = TimeUnit.DAYS.toMillis(mCriteriaInstallDays);   // msec

            return new Date().getTime() - mInstallDate.getTime() >= threshold
                           && new Date().getTime() - mAskLaterDate.getTime() >= threshold;
        }
    }

    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        showRateDialog(builder);
    }

    private void showRateDialog(AlertDialog.Builder builder) {
        if (sDialogRef != null && sDialogRef.get() != null) {
            // Dialog is already present
            return;
        }
        builder.setTitle(mTitle);
        builder.setMessage(mMessage);
        builder.setCancelable(false);
        if(mIcon!=0)builder.setIcon(mIcon);
        builder.setPositiveButton(mPositiveBtnTxt, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sCallback != null) {
                    sCallback.onYesClicked();
                    mSharedPrefUtil.setAskLaterDate();
                    mSharedPrefUtil.setRateOptionOut(true);
                }
            }
        });
        builder.setNeutralButton(mNeutralBtnTxt, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sCallback != null) {
                    sCallback.onCancelClicked();
                    mSharedPrefUtil.clearSharedPreferences();
                    mSharedPrefUtil.setAskLaterDate();
                    mSharedPrefUtil.setRateOptionOut(false);
                }
            }
        });
        builder.setNegativeButton(mNegativeBtnTxt, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sCallback != null) {
                    sCallback.onNoClicked();
                    mSharedPrefUtil.clearSharedPreferences();
                    mSharedPrefUtil.setAskLaterDate();
                    if(!isNegativeIgnored)mSharedPrefUtil.setRateOptionOut(true);
                    else mSharedPrefUtil.setRateOptionOut(false);
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sDialogRef.clear();
            }
        });
        sDialogRef = new WeakReference<>(builder.show());
    }

    public void printStatus() {
        Log.e(TAG,"*** RateThisApp Status ***");
        Log.e(TAG,"Install Date: " + new Date(mSharedPrefUtil.getInstallDate()));
        Log.e(TAG,"Launch Times: " + mSharedPrefUtil.getLaunchTimes());
        Log.e(TAG,"Opt out: " + mSharedPrefUtil.getRateOptionOut());
    }

    public void clearCashedRateData() {
        mSharedPrefUtil.clearSharedPreferences();
    }


    /**
     * RateAppCallback of dialog click event
     */
    public interface RateAppCallback {

        /**
         * "Rate now" event
         */
        void onYesClicked();

        /**
         * "No, thanks" event
         */
        void onNoClicked();

        /**
         * "Later" event
         */
        void onCancelClicked();
    }
}
