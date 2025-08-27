package com.aquirozc.updater.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.aquirozc.updater.data.LocalPackageInfo;
import java.io.File;
import java.sql.Date;

public class PackageHelper{

    private static final LocalPackageInfo DEFAULT_INFO = new LocalPackageInfo("N/A");

    public static LocalPackageInfo getLocalPackageInfo(Context ctx, String packageName){
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(packageName,0);

            String name = info.packageName;
            String size = info.applicationInfo != null ? new File(info.applicationInfo.sourceDir).length()/(1024*1024)+"MB" : "N/A";
            String version = info.versionName;
            String date = new Date(info.lastUpdateTime).toString();

            return new LocalPackageInfo(name,size,version,date);
        } catch (PackageManager.NameNotFoundException e) {
            return DEFAULT_INFO;
        }
    }

    public static void uninstall(Context ctx, String url){
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse(url)); //package:com.spotify.music
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
