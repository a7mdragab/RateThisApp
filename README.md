# RateThisApp

⚡️A Simple,fast,Reliable library used for prompting user to rate your app. ⚡️

⚡️A Single line of code can add a beautiful Rate App Dialog in your app. ⚡️

<img src="https://github.com/a7mdragab/RateThisApp/blob/master/ratethisapp/src/main/res/drawable/rateapp.png"
     alt=""
     width="400" height="650"
     style="float: left; margin-right: 10px;" />

+ ## Setup
Gradle dependency (recommended)

Add the following to your project level build.gradle:
``` 
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
``` 


Add this to your app build.gradle:
``` 
dependencies {
	implementation 'com.github.a7mdragab:RateThisApp:1.2'
}
```

+ ## Documentation
To use default dialog (Just one line)

//This will show the default dialog with built-in opening the play store page of the app function.
```
RateThisApp.showDefaultDialog(mContext);
```

To create custom dialog with minimum required properties
```
//Just mention the property you want to change only not all
RateThisApp.Initialize(mContext)
                .setCallback(new RateThisApp.RateAppCallback() {
                    @Override
                    public void onYesClicked() {
                        String appPackage = mContext.getPackageName();
                        try {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage)));
                        } catch (ActivityNotFoundException anfe) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackage)));
                        }
                    }

                    @Override
                    public void onNoClicked() {
                        Toast.makeText(context, "You can rate it later...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelClicked() {
                        Toast.makeText(context, "You can rate it later...", Toast.LENGTH_SHORT).show();
                    }
                })
                .showRateDialogIfNeeded();
```

To create custom dialog with full properties
```
//Just mention the property you want to change only not all
RateThisApp.Initialize(mContext)
                .setTitle("Rate This App")
                .setMessage("Please rate this app to encourage us to improve this app.")
                .setPositiveBtnTxt("Rate Now")
                .setNegativeBtnTxt("No, Thanks")
                .setNeutralBtnTxt("Later")
                .setThresholdLaunchTimes(10) //after 10 launches it will be shown again if cancelled
		.setThresholdInstallDays(7)//after 7 days it will be shown even if not opened the thresholdLauchTimes
                .setNegativeIgnored(true) //if is ignored: when user click no it will be shown again after the thereshold
		//otherwise:it won't be shown again
                .setCallback(new RateThisApp.RateAppCallback() {
                    @Override
                    public void onYesClicked() {
                        String appPackage = mContext.getPackageName();
                        try {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage)));
                        } catch (ActivityNotFoundException anfe) {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackage)));
                        }
                    }

                    @Override
                    public void onNoClicked() {
                        Toast.makeText(context, "You can rate it later...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelClicked() {
                        Toast.makeText(context, "You can rate it later...", Toast.LENGTH_SHORT).show();
                    }
                })
                .showRateDialogIfNeeded();
```

To clear cached data manually
```
RateThisApp.Initialize(this).clearCashedRateData();
```
