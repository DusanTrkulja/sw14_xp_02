package at.sw_xp_02.whisper;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;


public class SlidingMenuFragment extends ListFragment implements OnItemClickListener {
	ListView listView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String profileId = getActivity().getIntent().getStringExtra(Common.PROFILE_ID);
		Cursor c = getActivity().getContentResolver().query(DataProvider.CONTENT_URI_PROFILE,null,DataProvider.COL_ID + " != ?",new String[] { profileId },null);
		ContactCursorAdapter adapter = new ContactCursorAdapter(getActivity(),getActivity().getBaseContext(),c);
		getListView().setOnItemClickListener(this);
		setListAdapter(adapter);

	}


	public class ContactCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		public ContactCursorAdapter(Activity activity,Context context, Cursor c) {
			super(context, c, 0);
			this.mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override public int getCount() {
			return getCursor() == null ? 0 : super.getCount();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View itemLayout = mInflater.inflate(R.layout.row, parent, false);
			ViewHolder holder = new ViewHolder();
			itemLayout.setTag(holder);
			holder.icon = (ImageView) itemLayout.findViewById(R.id.row_icon);
			holder.title = (TextView) itemLayout.findViewById(R.id.row_title);
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			if(settings.getBoolean("contactmenu_inverted_color", false))  {
				holder.title.setBackgroundColor(Color.DKGRAY);
				holder.title.setTextColor(Color.WHITE);
				holder.icon.setBackgroundColor(Color.DKGRAY);
			} 
			return itemLayout;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.icon.setImageResource(R.drawable.ic_contact_picture);
			holder.title.setText(cursor.getString(cursor.getColumnIndex(DataProvider.COL_NAME)));
			int count = cursor.getInt(cursor.getColumnIndex(DataProvider.COL_COUNT));
			if(count > 0) {
				
				holder.badge = new BadgeView(getActivity(), holder.icon);
				holder.badge.setTextColor(Color.BLACK);
				holder.badge.setText(""+count);

				holder.badge.show();
			}
		}
	}

	private static class ViewHolder {
		ImageView icon;
		TextView title;
		BadgeView badge;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(Common.PROFILE_ID, String.valueOf(arg3));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}

}