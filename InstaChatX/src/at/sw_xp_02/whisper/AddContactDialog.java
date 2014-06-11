package at.sw_xp_02.whisper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactDialog extends DialogFragment {
	private AlertDialog alertDialog;
	private EditText et;

	public static AddContactDialog newInstance() {
		AddContactDialog fragment = new AddContactDialog();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		et = new EditText(getActivity());
		et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		et.setHint("abc@example.com");
		alertDialog = new AlertDialog.Builder(getActivity())
		.setTitle("Add Contact").setMessage("Add Contact")
		.setPositiveButton(android.R.string.ok, null)
		.setNegativeButton(android.R.string.cancel, null)
		.setView(et).create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button okBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String email = et.getText().toString();
						SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
						SharedPreferences.Editor editor = settings.edit();
						
						if(email.equals(Common.DEBUG)){
							editor.putBoolean("debug",true);
							editor.commit();
							Toast.makeText(AddContactDialog.this.getActivity(), "DEBUG-MODE Enabled.", Toast.LENGTH_LONG).show();
							alertDialog.dismiss();
							return;
						}
						
						if(email.equals(Common.NODEBUG)){
							editor.putBoolean("debug",false);
							editor.commit();
							Toast.makeText(AddContactDialog.this.getActivity(), "DEBUG-MODE Disabled.", Toast.LENGTH_LONG).show();
							alertDialog.dismiss();
							return;
						}

						if(email.equals(Common.getPreferredEmail()) && !settings.getBoolean("debug", false)) {
							et.setError("You cannot add yourself.");
							return;
						}

						if (!isEmailValid(email)) {
							et.setError("Invalid email!");
							return;
						}
						try {
							ContentValues values = new ContentValues(2);
							values.put(DataProvider.COL_NAME, email.substring(0, email.indexOf('@')));
							values.put(DataProvider.COL_EMAIL, email);
							getActivity().getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values);
						} catch (SQLException sqle) {}
						alertDialog.dismiss();
					}
				});
			}
		});
		return alertDialog;
	}

	private boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}	
}