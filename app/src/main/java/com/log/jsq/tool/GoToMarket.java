package com.log.jsq.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class GoToMarket {
    public static void openApplicationMarket(String appPackageName, String marketPackageName, Context context) {
        try {
            String url = "market://details?id=" + appPackageName;
            Intent localIntent = new Intent(Intent.ACTION_VIEW);

            if (marketPackageName != null) {
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setPackage(marketPackageName);
            }

            localIntent.setData(Uri.parse(url));
            context.startActivity(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
            openLinkBySystem(appPackageName, context);
        }
    }

    public static void openLinkBySystem(String packageName, Context context) {
        String url = "http://www.coolapk.com/apk/" + packageName;
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
