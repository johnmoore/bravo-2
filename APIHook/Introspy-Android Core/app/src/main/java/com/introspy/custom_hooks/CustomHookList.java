package com.introspy.custom_hooks;

import android.content.Intent;

import com.introspy.core.HookConfig;

public class CustomHookList {
	
	static public HookConfig[] getHookList() {
		return _hookList;
	}
	
	static private HookConfig[] _hookList = new HookConfig[] {	
		
		new HookConfig(false, // set to true to enable the hook
			"android.content.ContextWrapper", "startService", new Class<?>[]{Intent.class},
			// class, 						   method name, 				 arguments
			new HookExampleImpl(), 
			// instance of the implementation extending IntroHook (here in HookExampleInpl.java)
			"StartService with an intent as argument"),
			// notes (optional)


            new HookConfig(true, // set to true to enable the hook
                    "android.location.LocationManager", "getLastKnownLocation", new Class<?>[]{String.class},
                    // class, 						   method name, 				 arguments
                    new intro_LOCATION_LASTKNOW(),
                    // instance of the implementation extending IntroHook (here in HookExampleInpl.java)
                    "Get last location"),
            // notes (optional)
	};
}

