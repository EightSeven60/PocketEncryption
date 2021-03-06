package com.example.offlinepasswordmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.offlinepasswordmanager.PocketEncryptionApp;
import com.example.offlinepasswordmanager.R;
import com.example.offlinepasswordmanager.storage.EncryptedStorageController;
import com.example.offlinepasswordmanager.ui.custom.LoadingView;

import java.io.IOException;

public class LoginFragment extends Fragment {
	private static final String TAG = "LoginFragment";

	public LoginFragment() {
		// Required empty public constructor
	}

	public static LoginFragment newInstance() {
		return new LoginFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FragmentActivity activity = requireActivity();
		AppCompatButton enterB = activity.findViewById(R.id.logEnterB);
		AppCompatEditText editText = activity.findViewById(R.id.logPwdET);
		AppCompatImageButton helpB = activity.findViewById(R.id.logHelpB);
		ConstraintLayout layout = activity.findViewById(R.id.logLayout);

		PocketEncryptionApp pocketEncryptionApp = PocketEncryptionApp.getInstance();
		EncryptedStorageController encryptedStorageController = EncryptedStorageController.getInstance(activity);

		enterB.setOnClickListener(myView -> {
			LoadingView loadingView = new LoadingView(layout, activity, getString(R.string.checking_password), enterB, false).show();
			String inputPass = editText.getText().toString();
			pocketEncryptionApp.getExecutorService().execute(() -> {
				try {
					if (encryptedStorageController.checkMasterPassword(inputPass)) {
						pocketEncryptionApp.getMainThreadHandler().post(() -> {
							loadingView.terminate();
							Toast.makeText(activity, R.string.password_accepted, Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(activity, PrimaryActivity.class);
							startActivity(intent);
						});
					} else {
						pocketEncryptionApp.getMainThreadHandler().post(() -> {
							loadingView.terminate();
							Toast.makeText(activity, R.string.password_rejected, Toast.LENGTH_SHORT).show();
						});
					}
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
					pocketEncryptionApp.getMainThreadHandler().post(() -> {
						loadingView.terminate();
						Toast.makeText(activity, R.string.could_not_find_the_file_containing_your_set_password, Toast.LENGTH_LONG).show();
					});
				}
			});
		});

		helpB.setOnClickListener(myView -> {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder
					.setMessage(R.string.login_error_advice)
					.setCancelable(true).setPositiveButton(R.string.understood, (dialogInterface, i) -> dialogInterface.dismiss())
					.show();
		});
	}
}