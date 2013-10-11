/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blacklaw.netcaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.thoughtworks.xstream.XStream;

/**
 * netcaller 
 * @author blacklaw
 */
public class NetCaller {

    String ormFilePath = "";
    NetCallerProperties prop = new NetCallerProperties();
    HashMap<String, CallerRequest> requests = new HashMap<String, CallerRequest>();

    void log(String l) {
        System.out.println("" + l);
    }

    public NetCaller() {
    }
        
    public NetCaller(String filepath) {
        try {
            ormFilePath = filepath;
            parseORM(new FileInputStream(new File(ormFilePath)));
        } catch (Exception ex) {
            Logger.getLogger(NetCaller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public NetCaller(InputStream is){
    	try {
			parseORM(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

    /**
     * parse netcaller properties to request args
     * @param f
     * @throws Exception
     */
    private void parseORM(InputStream is) throws Exception {
        XStream stream = new XStream();
        stream.autodetectAnnotations(true);
        NetCallerProperties ncp = new NetCallerProperties();
        stream.processAnnotations(new Class[]{
            NetCallerProperties.class, 
            CallerRequest.class});
        prop = (NetCallerProperties) stream.fromXML(is);
        for(CallerRequest request : (CallerRequest[]) 
                (prop.requests.toArray(new CallerRequest[0]))){
        	/*XStream get different result in different environment,maybe null or ""*/
        	if (request.name == null || request.name.equals("")) 
        		request.name = request.action;
//        	log(request.name + "||" + request.action);
            this.requests.put(request.name, request);
        }
    }
    /**
     * call remote request
     * @param callable
     * @param actionName
     * @param args
     */
    public void call(NetCallerCallable callable, 
            String actionName, Object ...args){
    	log("ActionName:" + actionName);
        CallerRequest request = this.requests.get(actionName);
        String action = request.action;
        String type = request.type;
        String argsTail = request.args;
        for (int i = 0; i < args.length; i++){
            argsTail = argsTail.replace(String.format("$%d", i + 1), 
                    URLEncoder.encode(String.valueOf(args[i])));
        }
        String url = this.prop.base + action + "?" + argsTail;
        log(url);
        //callable.call(urlRead(url));
        new NetCallThread(callable, url).start();
        
    }
    
   
    
    class NetCallThread extends Thread{
    	NetCallerCallable callable;
    	String url = "";
    	NetCallThread(NetCallerCallable nc, String u){
    		this.callable = nc;
    		this.url = u;
    	}
    	@Override
    	public void run(){
        	String res = "";
        	HttpClient hc = new DefaultHttpClient();
            HttpGet get = null;
    			get = new HttpGet(this.url);
            try {
            	String st = "";
    			HttpResponse resp = hc.execute(get);
    			res = readContent(resp.getEntity().getContent());
    		} catch (ClientProtocolException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            this.callable.call(res);
    	}
    	
    }
    
    /**
     * send request, default type is GET
     * @param urlRead
     * @return
     */
    private String urlRead(String url){
    	String res = "";
    	HttpClient hc = new DefaultHttpClient();
        HttpGet get = null;
//		try {
			get = new HttpGet(url);//URLEncoder.encode(url, "GBK"));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        try {
        	String st = "";
			HttpResponse resp = hc.execute(get);
			res = readContent(resp.getEntity().getContent());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return res;
    }
    
    /**
     * read content from net inputstream
     * @param is
     * @return
     */
    private String readContent(InputStream is){
    	StringBuilder res = new StringBuilder();
    	String  tStr = "";
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    	try {
			while((tStr = reader.readLine()) != null){
				res.append(tStr + '\n');
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return res.toString();
    }
    
    
}
