package de.saxsys.android.projectiler.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BootCompleteReceiver", "" + intent.getAction() + " " + context.getPackageName());

        if(intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) || intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Uri data = intent.getData();
            String pkgName = data.getEncodedSchemeSpecificPart();
            Log.v("BootCompleteReceiver", "" + intent.getAction() + " " + pkgName);

            if(pkgName.equals(context.getPackageName())){


                Log.v("BootCompleteReceiver", "" + intent.getAction() + " " + context.getPackageName());
                Intent service = new Intent(context, NotificationService.class);

                context.stopService(service);

                context.startService(service);


            }

        }

    }

}
