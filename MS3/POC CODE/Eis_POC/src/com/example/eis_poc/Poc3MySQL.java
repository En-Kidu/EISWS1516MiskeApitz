package com.example.eis_poc;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.example.eis_poc_rabbitmq.R;
import com.google.gson.Gson;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Poc3MySQL extends Activity {

	private EditText mEditText;

	private Button mButtonAdd;

	private TextView mTextId;

	private TextView mTextVname;

	private Button mButtonRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_poc3);

		mButtonRefresh = (Button) findViewById(R.id.button_refresh);

		mEditText = (EditText) findViewById(R.id.editText_neue_verordnung);

		mButtonAdd = (Button) findViewById(R.id.button_add);

		mTextId = (TextView) findViewById(R.id.textView_id);

		mTextVname = (TextView) findViewById(R.id.textView_name);

		mButtonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String vname = mEditText.getText().toString();

				new postToNode().execute(vname);

			}
		});

		mButtonRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				refreshTable();

			}

		});

		refreshTable();

	}

	private class requestToNode extends AsyncTask<String, Void, List<Verordnung>> {

		@Override
		protected List<Verordnung> doInBackground(String... url) {

			InputStream inputStream = null;

			List<Verordnung> response = null;

			URL urlstring = null;

			try {

				urlstring = new URL("http://192.168.2.118:3001/verordnung");

			} catch (MalformedURLException e) {

				e.printStackTrace();

			}

			HttpURLConnection urlConnection = null;

			try {

				urlConnection = (HttpURLConnection) urlstring.openConnection();

			} catch (IOException e) {

				e.printStackTrace();

			}

			try {

				InputStream in = null;

				try {

					in = new BufferedInputStream(urlConnection.getInputStream());

				} catch (IOException e) {

					e.printStackTrace();

				}

				Gson gson = new Gson();

				Reader reader = new InputStreamReader(in);

				Verordnung[] res = gson.fromJson(reader, Verordnung[].class);

				response = Arrays.asList(res);

				return response;

			}

			finally {

				urlConnection.disconnect();

			}

		}

	}

	private class postToNode extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... name) {

			try {

				URL urlstring = new URL("http://192.168.2.118:3001/verordnung");

				HttpURLConnection conn = (HttpURLConnection) urlstring.openConnection();

				conn.setRequestMethod("POST");

				conn.setDoInput(true);

				conn.setDoOutput(true);

				String namestring = "";

				for (int i = 0; i < name.length; i++) {

					namestring += name[i];

				}

				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("Name", namestring));

				OutputStream os = conn.getOutputStream();

				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

				writer.write(getQuery(params));

				writer.flush();

				writer.close();

				os.close();

				conn.connect();

			} catch (MalformedURLException e) {

				e.printStackTrace();

			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}

			return null;

		}

	}

	public void refreshTable() {

		try {

			List<Verordnung> vlist = new requestToNode().execute("http://192.168.2.118:3001/test").get();

			mTextId.setText("");

			mTextVname.setText("");

			for (Verordnung verordnung : vlist) {

				mTextId.append("\n" + verordnung.getId());

				mTextVname.append("\n" + verordnung.getName());
			}

		} catch (InterruptedException e) {

			e.printStackTrace();

		} catch (ExecutionException e) {

			e.printStackTrace();

		}

	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {

		StringBuilder result = new StringBuilder();

		boolean first = true;

		for (NameValuePair pair : params) {

			if (first) {

				first = false;

			} else {

				result.append("&");

			}

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));

			result.append("=");

			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();

	}

	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	protected void onPause() {

		super.onPause();

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

	}

}
