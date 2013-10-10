package com.blacklaw.qianwen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.blacklaw.net.SyncNetCallable;
import com.blacklaw.netcaller.NetCaller;

public class MainActivity extends Activity {
	
	Button but;
//	EditText edit;
	ListView listView;
	NetCaller nc = null;
	PopupWindow pw = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		edit = (EditText) findViewById(R.id.editText1);
		listView = (ListView) findViewById(R.id.listView1);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refreshRetrieve();
				
			}
			
		});
		this.nc = initNetCaller();
		refreshRetrieve();
	}
	private void refreshRetrieve(){
		nc.call(new SyncNetCallable(this.MyHandler){

			@Override
			public void arrive(String str) {
				// TODO Auto-generated method stub
				try {
					MainActivity.this.listView.setAdapter(
							new SimpleAdapter(MainActivity.this, 
								getData(str), 
								R.layout.inline_result, 
								new String[]{"title", "content", "time"}, 
								new int[]{R.id.result_title, R.id.result_content, R.id.result_time}));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}, 
				"retrieve");
	}
	private List<Map<String, Object>> getData(String jStr) throws JSONException{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
//		for(int i = 0;i < 10; i++){
		JSONArray jArr = new JSONArray(jStr);
		for(int i = 0; i < jArr.length(); i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			JSONObject jObj = jArr.getJSONObject(i);
			map.put("title", jObj.get("title"));
			map.put("content", jObj.get("content"));
			map.put("time", jObj.get("time"));
			list.add(map);
		}
		return list;
	}
	
	Handler MyHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			((SyncNetCallable) msg.obj).syncCall();
		}
		
	};
	
	private NetCaller initNetCaller(){
		InputStream is = null;
		try {
			is = this.getAssets().open("prop.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new NetCaller(is);
		
	}
	private String test(){
		

/*		nc.call(new SyncNetCallable(MyHandler) {

			@Override
			public void arrive(String str) {
				// TODO Auto-generated method stub
				log(str);
			}
		}, "add", "english", "howtospellwords", "spell");*/

		nc.call(new SyncNetCallable(MyHandler) {
			@Override
			public void arrive(String str) {
				log(str);
			}
		}, "retrieve");
		return "Loading...";
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		View v = MainActivity.this.getLayoutInflater().inflate(R.layout.popup_result, null);
		pw = new PopupWindow(v, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, false);
		pw.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		pw.showAsDropDown(v);
		final EditText title = (EditText) v.findViewById(R.id.editText1);
		final EditText content = (EditText) v.findViewById(R.id.editText2);
		v.findViewById(R.id.close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				nc.call(new SyncNetCallable(MainActivity.this.MyHandler){

					@Override
					public void arrive(String str) {
						// TODO Auto-generated method stub
						Toast.makeText(MainActivity.this, "Add Success!!", 
								Toast.LENGTH_SHORT).show();
					}
					
				}, 
						"add", "", title.getText().toString(), content.getText().toString());
				pw.dismiss();
			}
			
		});
		return false;
	}

	private void log(String str) {
		//edit.append(str + '\n');
	}

}
