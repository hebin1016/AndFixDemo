package com.example.andfixdemo;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	EditText mEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_gettxt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, getEditText(), Toast.LENGTH_LONG).show();
			}
		});

		findViewById(R.id.btn_fixbug).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fix();
			}
		});
	}

	private String getEditText() {
		return "999";
	}

	private void fix() {

		try {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/out.apatch";
			PatchManager patchManager = new PatchManager(this);
			patchManager.init("1.0");
			Log.i(TAG, "inited.");
			patchManager.loadPatch();

			Log.i(TAG, "apatch loaded.");

			patchManager.addPatch(path);
			Log.i(TAG, "apatch:" + path + " added.");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
