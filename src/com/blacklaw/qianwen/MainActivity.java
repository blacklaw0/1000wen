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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.blacklaw.net.SyncNetCallable;
import com.blacklaw.netcaller.NetCaller;
import com.blacklaw.widget.CustomAddDialog;
import com.blacklaw.widget.CustomUpdateDialog;

public class MainActivity extends Activity {
	
	Button but;
//	EditText edit;
	ListView listView;

	public NetCaller nc = null;
	PopupWindow pw = null;
	View popupView = null;
	List<Map<String, Object>> list = null;
	SimpleAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemID,
					long arg3) {
				// TODO Auto-generated method stub
				Log.v("ItemSelected", "" + itemID);
				update(itemID);
			}
			
		});
		this.registerForContextMenu(listView);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				retrieve();
			}
		});
		this.nc = initNetCaller();
		retrieve();
	}
		
	public void retrieve(){
		nc.call(new SyncNetCallable(this.MyHandler){

			@Override
			public void arrive(String str) {
				// TODO Auto-generated method stub
				try {
					list = getData(str);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter = new SimpleAdapter(MainActivity.this, 
						list, 
						R.layout.inline_result, 
						new String[]{"title", "content", "time", "tags"}, 
						new int[]{R.id.result_title, R.id.result_content, R.id.result_time, R.id.tags});
				listView.setAdapter(adapter);
			}
			
		}, 
				"retrieve");
	}
	private List<Map<String, Object>> getData(String jStr) throws JSONException{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray jArr = new JSONArray(jStr);
		for(int i = 0; i < jArr.length(); i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			JSONObject jObj = jArr.getJSONObject(i);
			for (String tag : new String[] {"id", "tags", "time", "title", "content"})
				map.put(tag, jObj.get(tag));
			list.add(map);
		}
		return list;
	}
	
	public Handler MyHandler = new Handler(){
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
		nc.call(new SyncNetCallable(MyHandler) {
			@Override
			public void arrive(String str) {
				log(str);
			}
		}, "retrieve");
		return "Loading...";
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		return super.onPrepareOptionsMenu(menu);
	}

	long lastBackPressTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v("keyCode", "" + keyCode);
		switch (keyCode){
			case KeyEvent.KEYCODE_MENU: {
				add();
			}; break;
			case KeyEvent.KEYCODE_BACK: {
				long now = System.currentTimeMillis();
				Log.v("Delta time", "" + (now - lastBackPressTime));
				if (now - lastBackPressTime > 800){
					Toast.makeText(this, this.getString(R.string.back_press_info), Toast.LENGTH_SHORT).show();
					lastBackPressTime = now;
					return false;
				}
				else
					System.exit(0);
				
			}
			default: break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return false;
	}

	private void log(String str) {
		//edit.append(str + '\n');
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		String action = item.getTitle().toString();
		int id = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		if(action.equals("Delete"))
			delete(id);
		
		if(action.equals("Retrieve"))
			retrieve();
		
		if(action.equals("Add"))
			add();
		
		if(action.equals("Update"))
			update(id);
		//Toast.makeText(this,  menuInfo.position, Toast.LENGTH_SHORT).show();
		return super.onContextItemSelected(item);
	}
	
	public void add(){
		CustomAddDialog dialog = new CustomAddDialog(MainActivity.this, R.layout.widget_dialog_result, R.style.MyDialog);
		dialog.show();
	}
	public void delete(int itemID){
		/*first delete in server*/
		Map<String, Object> map = list.get(itemID);
		int id = (Integer) map.get("id");
		nc.call(new SyncNetCallable(this.MyHandler){

			@Override
			public void arrive(String str) {
				// TODO Auto-generated method stub
				Log.v("Delete result", str);
			}
			
		}, "delete", id);
		/*then delete the data in view*/
		list.remove(itemID);
		adapter.notifyDataSetChanged();
	}
	
	public void update(int itemID){
		Map<String, Object> map = this.list.get(itemID);
		new CustomUpdateDialog(
				this, 
				R.layout.widget_dialog_result,
				R.style.MyDialog)
		.init((String) map.get("tags"),
				(String) map.get("title"), 
				(String) map.get("content"), 
				(Integer) map.get("id")
				).show();
		
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle("Actions");
		menu.add(0, v.getId(), 0, "Add");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Update");
		menu.add(0, v.getId(), 0, "Retrieve");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

}
