package com.sebastianlundquist.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

	LinearLayout photoLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feed);

		photoLayout = findViewById(R.id.photoLayout);
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		setTitle("Instagran: " + username + "'s Photos");

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
		query.whereEqualTo("username", username);
		query.orderByDescending("objectId");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null && objects.size() > 0) {
					for (ParseObject object : objects) {
						ParseFile file = (ParseFile)object.get("image");
						file.getDataInBackground(new GetDataCallback() {
							@Override
							public void done(byte[] data, ParseException e) {
								if (e == null && data != null) {
									Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
									ImageView imageView = new ImageView(getApplicationContext());
									imageView.setLayoutParams(new ViewGroup.LayoutParams(
											ViewGroup.LayoutParams.MATCH_PARENT,
											ViewGroup.LayoutParams.WRAP_CONTENT
									));
									imageView.setImageBitmap(bitmap);
									imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
									imageView.setAdjustViewBounds(true);
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.MATCH_PARENT,
											LinearLayout.LayoutParams.WRAP_CONTENT
									);
									Resources r = getApplicationContext().getResources();
									int px = (int) TypedValue.applyDimension(
											TypedValue.COMPLEX_UNIT_DIP,
											16,
											r.getDisplayMetrics()
									);
									params.setMargins(0, 0, 0, px);
									imageView.setLayoutParams(params);
									photoLayout.addView(imageView);
								}
							}
						});
					}
				}
			}
		});
	}
}
