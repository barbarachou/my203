package com.barbara.my203;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainPhoneActivity extends Activity {
	private PhoneAdapter adapter;
	private ListView listView;
	private static ArrayList<Phone> nums = null;
	private ProgressDialog progressDialog;
	private static String file_url = "http://0.203class.duapp.com/phone.py?pw=0800220308";
	private String filePath = Environment.getExternalStorageDirectory()
			.toString() + "/my203/phone.dat";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		setContentView(R.layout.activity_main);

		getExtras();
		initView();
		initData();
		initViewData();

		progressDialog = ProgressDialog.show(MainPhoneActivity.this,
				getText(R.string.update_title), getText(R.string.update_body),
				true, true);
		new DownloadFileFromURL().execute(file_url);

		listView = (ListView) findViewById(R.id.listView1);
		nums = getHotel(filePath);
		adapter = new PhoneAdapter(nums);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parents, View view,
					int postion, long id) {
				Phone pn = nums.get(postion);
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ pn.getNum()));
				startActivity(intent);
			}
		});

		UMFeedbackService.enableNewReplyNotification(this,
				NotificationType.AlertDialog);
		UmengUpdateAgent.setOnDownloadListener(new UmengDownloadListener() {
			@Override
			public void OnDownloadEnd(int result) {
				Toast.makeText(getApplicationContext(),
						"download result : " + result, Toast.LENGTH_SHORT)
						.show();
			}
		});

	}

	private void getExtras() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		}
	}

	private void initView() {

	}

	private void initData() {

	}

	private void initViewData() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_settings:
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;
		}
		return true;
	}

//	@SuppressLint("SdCardPath")
//	public boolean fileIsExists() {
//		File f = new File("/sdcard/my203/phone.dat");
//		if (!f.exists()) {
//			return false;
//		}
//		return true;
//	}

	public void gotofb() {
		UMFeedbackService.openUmengFeedbackSDK(this);
	}

	private String openFile(String fileName) {
		FileInputStream fin;
		String res = "";
		int length;
		try {
			File file = new File(fileName);
			fin = new FileInputStream(file);
			length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (IOException e) {
			Toast.makeText(this, "can't find the file", Toast.LENGTH_SHORT)
					.show();
		}

		return res;
	}

	private ArrayList<Phone> getHotel(String fileName) {
		String res = openFile(fileName);
		System.out.print("res ok");
		ArrayList<Phone> list = new ArrayList<Phone>();
		String name;
		String num;
		String city;
		JSONObject phone;
		try {
			JSONObject jsonObject = new JSONObject(res);
			JSONArray jsonArray = (JSONArray) jsonObject.get("all");
			for (int i = 0; i < jsonArray.length(); i++) {
				phone = jsonArray.getJSONObject(i);
				name = phone.getString("name");
				num = phone.getString("phone");
				city = phone.getString("city");
				list.add(new Phone(name, num, city));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (list.isEmpty()) {
			list.add(new Phone("name", "phone", "city"));
		}
		return list;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView num;
		public ImageButton dia;
		public ImageButton msm;
	}

	class PhoneAdapter extends BaseAdapter {

		private ArrayList<Phone> datas;

		public PhoneAdapter(ArrayList<Phone> datas) {
			this.datas = datas;
		}

		@Override
		public int getCount() {
			try {
				return datas.size();
			} catch (Exception e) {
				Toast.makeText(MainPhoneActivity.this,
						"read data failure,please check the network",
						Toast.LENGTH_LONG).show();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater mInflater = getLayoutInflater();
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.num = (TextView) convertView
						.findViewById(R.id.textView2);
				holder.dia = (ImageButton) convertView.findViewById(R.id.dia);
				holder.msm = (ImageButton) convertView.findViewById(R.id.msm);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Phone phone = datas.get(position);
			holder.name.setText(phone.toString());
			holder.num.setText(phone.getNum());
			holder.dia.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + phone.getNum()));
					startActivity(intent);
				}
			});
			holder.msm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse("smsto:" + phone.getNum()));
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	class DownloadFileFromURL extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();

				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				File sdcardDir = Environment.getExternalStorageDirectory();
				String path = sdcardDir.getPath() + "/my203";
				File pathNew = new File(path);
				if (!pathNew.exists()) {
					pathNew.mkdirs();
				}
				OutputStream output = new FileOutputStream(
						path+"/phone.dat");

				byte data[] = new byte[1024];

				while ((count = input.read(data)) != -1) {
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			progressDialog.dismiss();
			nums = getHotel(filePath);
			adapter = new PhoneAdapter(nums);
			listView.setAdapter(adapter);
		}

	}

	// private void showProgressDialog() {
	// progressDialog = ProgressDialog.show(MainPhoneActivity.this,
	// getText(R.string.update_title), getText(R.string.update_body),
	// true, true);
	// }

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
