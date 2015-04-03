/*
 * Copyright (C) 2012 Jorrit "Chainfire" Jongma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.chainfire.TCPDumpModule;

import eu.chainfire.libsuperuser.Application;
import eu.chainfire.libsuperuser.Shell;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
Background intent service designed to start up on device boot
 This was taken from Chainfire's libsuperuser example scripts
 and modified to run only the background services.

 This service was then extended to use tcpdump to collect packets
 Further implementation needs to be done to move the dumps to a central server
 Also, make the dumps continuous and more targeted
 */
public class BackgroundIntentService extends IntentService {
    // you could provide more options here, should you need them
    public static final String ACTION_BOOT_COMPLETE 		= "boot_complete";

    public static void performAction(Context context, String action) {
        performAction(context, action, null);		
    }

    public static void performAction(Context context, String action, Bundle extras) {
        // this is utility call to easy starting the service and performing a task
        // pass parameters in an bundle to be added to the intent as extras
        // See BootCompleteReceiver.java

        if ((context == null) || (action == null) || action.equals("")) return;

        Intent svc = new Intent(context, BackgroundIntentService.class);
        svc.setAction(action);
        if (extras != null)	svc.putExtras(extras);
        context.startService(svc);
    }

    public BackgroundIntentService() {
        // If you forget this one, the app will crash
        super("BackgroundIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();		
        if ((action == null) || (action.equals(""))) return;

        if (action.equals(ACTION_BOOT_COMPLETE)) {
            onBootComplete();
        }
        // you can define more options here... pass parameters through the "extra" values
    }

    protected void onBootComplete() {
        // Opens a superuser shell to run tcpdump and collect packets
        // and saves them in dump.txt on the sdcard
        Shell.SU.run("tcpdump -i wlan0 -s 1514 -c 1000 -nSvX port 80 >> /sdcard/dump.txt");
        Application.toast(this, "1000 http packets saved!");
    }	
}
