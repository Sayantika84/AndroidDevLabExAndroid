## Special Note :
if dependencies need 37 instead of 36 - this type of error occurs, do the following.
gradle properties -> build.gradle.kts[module:app] -> if sdk is 36, then replace everywhere with 37
in this repo, kept everything with 37.
for all apps, MainActivity.kt contains all the code


## Experiment 4: SQLite Database Application
Where the Database is Saved
The database is saved in your application's private internal storage space. Specifically, it is stored in the databases folder associated with your app's package name.

Based on your project structure, the exact file path on the Android device or emulator is:
/data/data/com.example.sqlitedbapp/databases/Student.db

Security (Sandboxing): Because this path is in internal storage, it is strictly private. Other apps on the device cannot access this file, and normal users cannot see it using standard file manager apps unless the device is rooted.

### How to view it as a developer: 
While running your app in Android Studio by going to View > Tool Windows > App Inspection and selecting the Database Inspector tab.

## Experiment 6: GPS-Based Location Application
Open your AndroidManifest.xml and add the following code outside the <application> tag

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <queries>
        <package android:name="com.google.android.apps.maps" />
    </queries>

## Experiment 7: Message Alert Application
Open your AndroidManifest.xml and add the following code outside the <application> tag

    <uses-permission android:name="android.permission.RECEIVE_SMS" />
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />