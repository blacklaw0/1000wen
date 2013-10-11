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

public class CustomAddDialog extends Dialog{
	Activity root;
	View v;
	int layout, theme;
	
	public CustomAddDialog(Activity context, int l, int t) {
		super(context, t);
		// TODO Auto-generated constructor stub
		root = context;
		theme = t;
		v = root.getLayoutInflater().inflate(l, null);
		
	
		final EditText title = (EditText) v.findViewById(R.id.editText1);
		final EditText content = (EditText) v.findViewById(R.id.editText2);
		final EditText tags = (EditText) v.findViewById(R.id.tags);
		final Button but = (Button) v.findViewById(R.id.close);
		
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
						CustomAddDialog.this.dismiss();
					}
					
				}, 
						"add", tags.getText(), title.getText().toString(), content.getText().toString());
				
			}

			
			
		});
		this.setContentView(v);
//		v.findViewById(R.id.close).setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
			
//			}
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
	}
	
	
	
}
