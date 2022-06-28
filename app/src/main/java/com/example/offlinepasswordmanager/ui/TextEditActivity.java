package com.example.offlinepasswordmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.offlinepasswordmanager.R;
import com.example.offlinepasswordmanager.storage.EncryptedStorageController;

import java.io.File;
import java.io.IOException;

public class TextEditActivity extends AppCompatActivity {
	private static final String TAG = "TextEditActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_edit);

		Intent intent = getIntent();
		String currentPath = intent.getStringExtra("CurrentPath");

		File file = new File(intent.getStringExtra("FilePath"));

		EditText contentET = findViewById(R.id.editContentET);
		EditText fileNameET = findViewById(R.id.editFileNameET);
		Button saveB = findViewById(R.id.editSaveB);
		Button exitB = findViewById(R.id.editExitButton);

		EncryptedStorageController encryptedStorageController = EncryptedStorageController.getInstance(this);

		try {
			contentET.setText(encryptedStorageController.get(file.getName(), currentPath));
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			Toast.makeText(this, R.string.failed_to_open_file_to_read_from, Toast.LENGTH_LONG).show();
			finish();
		}

		fileNameET.setText(file.getName());

		saveB.setOnClickListener(view -> {
			file.delete();

			try {
				encryptedStorageController.add(fileNameET.getText().toString(), contentET.getText().toString(), currentPath);
				Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				Toast.makeText(this, R.string.could_not_save_your_file, Toast.LENGTH_LONG).show();
			}
		});

		exitB.setOnClickListener(view ->finish());

	}
}