package com.example.offlinepasswordmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.offlinepasswordmanager.PocketEncryptionApp;
import com.example.offlinepasswordmanager.R;
import com.example.offlinepasswordmanager.storage.EncryptedStorageController;
import com.example.offlinepasswordmanager.ui.custom.LoadingView;

import java.io.IOException;

public class WelcomeFragment extends Fragment {
	private static final String TAG = "WelcomeFragment";

	public WelcomeFragment() {
		// Required empty public constructor
	}

	public static WelcomeFragment newInstance() {
		return new WelcomeFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_welcome, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FragmentActivity activity = requireActivity();
		AppCompatButton setPwdB = activity.findViewById(R.id.welSetPwdB);
		AppCompatEditText editText = activity.findViewById(R.id.welPwdET);
		AppCompatEditText editTextConfirm = activity.findViewById(R.id.welPwdETConfirm);
		AppCompatButton alreadySetPwdB = activity.findViewById(R.id.welAlreadyUsedB);
		AppCompatButton whySetPwdB = activity.findViewById(R.id.welWhySetPassB);
		LinearLayoutCompat pwdLayout = activity.findViewById(R.id.welPwdLayout);

		PocketEncryptionApp pocketEncryptionApp = PocketEncryptionApp.getInstance();
		EncryptedStorageController encryptedStorageController = EncryptedStorageController.getInstance(activity);

		setPwdB.setOnClickListener(myView -> {
			String inputPass = editText.getText().toString();
			String inputPassConfirm = editTextConfirm.getText().toString();
			if (inputPass.hashCode() != inputPassConfirm.hashCode()) {
				Toast.makeText(activity, getString(R.string.please_enter_the_same_password_twice), Toast.LENGTH_LONG).show();
				return;
			}
			LoadingView loadingView = new LoadingView(pwdLayout, activity, getString(R.string.saving), setPwdB, false).show();
			pocketEncryptionApp.getExecutorService().execute(() -> {
				try {
					encryptedStorageController.setMasterPassword(activity, inputPass);
					pocketEncryptionApp.getMainThreadHandler().post(() -> {
						loadingView.terminate();
						Toast.makeText(activity, R.string.password_set, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(activity, PrimaryActivity.class);
						startActivity(intent);
					});
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
					pocketEncryptionApp.getMainThreadHandler().post(() -> {
						loadingView.terminate();
						Toast.makeText(activity, R.string.could_not_set_your_password, Toast.LENGTH_LONG).show();
					});
				}
			});
		});

		alreadySetPwdB.setOnClickListener(myView -> {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder
					.setTitle(R.string.this_is_a_problem)
					.setMessage(R.string.i_ve_already_set_my_password_advice)
					.setPositiveButton(R.string.understood, (dialogInterface, i) -> dialogInterface.dismiss())
					.setCancelable(true)
					.show();
		});

		whySetPwdB.setOnClickListener(myView -> {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
			alertDialogBuilder
					.setTitle(R.string.to_secure_your_data)
					.setMessage(R.string.why_set_a_password_advice)
					.setPositiveButton(R.string.understood, (dialogInterface, i) -> dialogInterface.dismiss())
					.setCancelable(true)
					.show();
		});
	}
}