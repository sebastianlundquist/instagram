package com.sebastianlundquist.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

	public void getPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.share_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.share) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
			}
			else {
				getPhoto();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getPhoto();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri selectedImage = data.getData();
		if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] bytes = stream.toByteArray();
				ParseFile file = new ParseFile("image.png", bytes, "image/png");
				ParseObject object = new ParseObject("Image");
				object.put("image", file);
				object.put("username", ParseUser.getCurrentUser().getUsername());
				object.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							Toast.makeText(UserListActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(UserListActivity.this, "There was an issue uploading your image.", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);

		final ListView userList = findViewById(R.id.userList);
		final ArrayList<String> usernames = new ArrayList<>();
		final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

		userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
				intent.putExtra("username", usernames.get(i));
				startActivity(intent);
			}
		});

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
