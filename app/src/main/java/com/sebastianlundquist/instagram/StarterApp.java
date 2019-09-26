package com.sebastianlundquist.instagram;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class StarterApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);

		// Add your initialization code here
		Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
				.applicationId("2b93ad3b1c4ff1b9a5d1182c97b3e7cde104e0ec")
				.clientKey("71a08cc271e3ce7b24b30802352bf27d39e15b91")
				.server("http://13.53.200.191:80/parse/")
				.build()
		);

		ParseObject object = new ParseObject("InstagramObject");
		object.put("mycoolnumber", "123");
		object.put("mycoolstring", "zebra");

		object.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.i("Parse Result", "Success!");
				}
				else {
					Log.w("Parse Result", "Failed: " + e.toString());
					e.printStackTrace();
				}
			}
		});

		ParseUser.enableAutomaticUser();

		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
	}
}
