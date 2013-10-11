package com.blacklaw.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blacklaw.net.SyncNetCallable;
import com.blacklaw.qianwen.MainActivity;
import com.blacklaw.qianwen.R;

public class CustomUpdateDialog extends Dialog{
	Activity root;
	View v;
	int layout, theme;
	EditText title, content, tags;
	Button but;
	int id;
	
	public CustomUpdateDialog(Activity context, int l, int t) {
		super(context, t);
		// TODO Auto-generated constructor stub
		root = context;
		theme = t;
		v = root.getLayoutInflater().inflate(l, null);
		
		title = (EditText) v.findViewById(R.id.editText1);
		content = (EditText) v.findViewById(R.id.editText2);
		tags = (EditText) v.findViewById(R.id.tags);
		but = (Button) v.findViewById(R.id.close);
		
		but.setText("Update");
		but.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)root).nc.call(new SyncNetCallable(((MainActivity) root).MyHandler){

					@Override
					public void arrive(String str) {
						// TODO Auto-generated method stub
						Toast.makeText(root, "Add Success!!", 
								Toast.LENGTH_SHORT).show();
						((MainActivity) root).retrieve();
						CustomUpdateDialog.this.dismiss();
					}
					
				}, 
						"update", CustomUpdateDialog.this.id, tags.getText(), title.getText().toString(), content.getText().toString());
				
			}

			
			
		});
		this.setContentView(v);
	}
	
	public CustomUpdateDialog init(String tags, String title, String content, int id) {
		this.tags.setText(tags);
		this.title.setText(title);
		this.content.setText(content);
		this.id = id;
		return this;
	}
	
	
}
