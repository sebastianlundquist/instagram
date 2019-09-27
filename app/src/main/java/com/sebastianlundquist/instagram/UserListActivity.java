package com.sebastianlundquist.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);

		final ListView userList = findViewById(R.id.userList);
		final ArrayList<String> usernames = new ArrayList<>();
		final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
		query.addAscendingOrder("username".toLowerCase());
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				if (e == null) {
					if (objects.size() > 0) {
						for (ParseUser user : objects) {
							usernames.add(user.getUsername());
						}
						userList.setAdapter(arrayAdapter);
					}
				}
				else {
					e.printStackTrace();
				}
			}
		});
	}
}
