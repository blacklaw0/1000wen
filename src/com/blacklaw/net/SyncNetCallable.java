package com.blacklaw.net;

import android.os.Handler;

import com.blacklaw.netcaller.NetCallerCallable;

public abstract class SyncNetCallable implements NetCallerCallable{
	/*Asynchronous call*/
	String content = "";
	Handler UIHandler = null;
	public SyncNetCallable(Handler h){
		this.UIHandler = h;
	}
	@Override
	public void call(String str) {
		// TODO Auto-generated method stub
		this.content = str;
		UIHandler.obtainMessage(0, this).sendToTarget();
	}
	
	/*Synchronization call*/
	public void syncCall() {
		this.arrive(this.content);
	}
	
	/*Synchronization arrive*/
	abstract public void arrive(String str);
}
