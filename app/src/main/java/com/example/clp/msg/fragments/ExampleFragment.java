/*
 * Copyright (C) 2014 Bluetooth Connection Template
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.clp.msg.fragments;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.clp.msg.R;
import com.example.clp.msg.Timethread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExampleFragment extends Fragment implements View.OnClickListener {

	private Context mContext = null;
	private IFragmentListener mFragmentListener = null;
	private Handler mActivityHandler = null;
	
	TextView mTextChat;
	EditText mEditChat;
	Button mBtnSend;

	public ExampleFragment(Context c, IFragmentListener l, Handler h) {
		mContext = c;
		mFragmentListener = l;
		mActivityHandler = h;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
		
		mTextChat = (TextView) rootView.findViewById(R.id.text_chat);
		mTextChat.setMaxLines(1000);
		mTextChat.setVerticalScrollBarEnabled(true);
		mTextChat.setMovementMethod(new ScrollingMovementMethod());
		
		mEditChat = (EditText) rootView.findViewById(R.id.edit_chat);
		mEditChat.setOnEditorActionListener(mWriteListener);
		
		mBtnSend = (Button) rootView.findViewById(R.id.button_send);
		mBtnSend.setOnClickListener(this);
		
		return rootView;
	}
	
	@Override
	public void onClick(View v) {//전송버튼!!
		Log.v("button","Clicked!!!");
		switch(v.getId()) {
		case R.id.button_send:
			Log.v("button","Clicked!!!");
            String filename = mEditChat.getText().toString();
            if(filename != null && filename.length() > 0){
				Timethread t = new Timethread(filename);
				t.start();

			}

			break;
		}
	}
	class Timethread extends Thread {
		String SdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		long Synctime;
		String filename;
		String file;
		String filediv[];
		int prevtime;
		public Timethread(String filename){
			Synctime  = System.currentTimeMillis();//Synctime-System.currentTimeMillis() : 시간차
			this.filename = filename;
			this.file = readfile(SdcardPath+"/"+filename);
			Log.v("parsed maxnum",file);
			filediv = file.replaceAll("\n","").replaceAll("\r","").split("SYNC");//sync태그 찾기/나누기
			Log.v("parsed maxnum",""+filediv.length);
		}
		public void run(){
			sendMessage("Started!!!");
			for(int i=0;i<=filediv.length;i++){
				Log.i("parsing smi", filediv[i]);
				StringBuffer messagem = new StringBuffer("");
				messagem.delete(0,messagem.length());
				Pattern Sync = Pattern.compile("Start", Pattern.CASE_INSENSITIVE);//start찾기
				int time;
				int endoftag;
				while(true){
					Matcher mat=Sync.matcher(filediv[i]);
					endoftag = filediv[i].indexOf('>');//start 끝 찾기
				if(mat.find()){
					int k=Integer.parseInt(filediv[i].substring(mat.end()+1,endoftag));
					time =k-prevtime;
					prevtime = k;
					break;
				}
				else{
					i++;
				}}
				try {
					Log.v("tagstart,end",(endoftag+12)+filediv[i].length()+"");
					messagem.append(filediv[i].substring(endoftag+12,filediv[i].length()));
					Log.e("parsing smi", "asdf"+(time-(Synctime-System.currentTimeMillis())));
					sleep((time-(Synctime-System.currentTimeMillis()))/10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendMessage(messagem.toString());
			}
		}
		/*@Override
		public void run() {
			String file = readfile(filename);
			StringBuffer message = new StringBuffer("");
			String div[]=file.split("\n");
			int j=1;
			for(int i=0; i<div.length; i++){
				boolean b = true;
				Pattern arrow = Pattern.compile("-->", Pattern.CASE_INSENSITIVE);
				Log.v("이번 자막은", j+" 번째 자막입니다.");
				if(!(Integer.parseInt(div[i])==j||div[i].equals(j+"")))
				while(b){
					j++;
					try {
						wait(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}


				}
				sendMessage(message.toString());
			}

		}*/
		public String readfile(String filename) {
			File f = new File(filename);
			if (!f.exists()) {
				Log.v("filecheck", filename + "not exist");
			}

			StringBuffer sb = new StringBuffer();
			Log.v("fileWriter", filename + " reading started");
			try {// 저장일시 읽어들이기
				FileInputStream fis = new FileInputStream(filename);
				int n;
				while ((n = fis.available()) > 0) {
					byte b[] = new byte[n];
					if (fis.read(b) == -1)
						break;
					sb.append(new String(b));
				}
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.err.println("Could not find file" + filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}
	}
	
	
    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                if(message != null && message.length() > 0)
                	sendMessage(message);
            }
            return true;
        }
    };
	
    // Sends user message to remote
    private void sendMessage(String message1) {
    	if(message1 == null || message1.length() < 1)
    		return;
    	// send to remote
    	if(mFragmentListener != null)
    		mFragmentListener.OnFragmentCallback(IFragmentListener.CALLBACK_SEND_MESSAGE, 0, 0, message1, null,null);
    	else
    		return;
    	// show on text view
    	if(mTextChat != null) {
    		//mTextChat.append("\nSend: ");
    		//mTextChat.append(message1);
        	int scrollamout = mTextChat.getLayout().getLineTop(mTextChat.getLineCount()) - mTextChat.getHeight();
        	if (scrollamout > mTextChat.getHeight())
        		mTextChat.scrollTo(0, scrollamout);
    	}
    	//mEditChat.setText("");
    }
    
    private static final int NEW_LINE_INTERVAL = 1000;
    private long mLastReceivedTime = 0L;
    
    // Show messages from remote
    public void showMessage(String message) {
    	if(message != null && message.length() > 0) {
    		long current = System.currentTimeMillis();
    		
    		if(current - mLastReceivedTime > NEW_LINE_INTERVAL) {
    			//mTextChat.append("\nRcv: ");
    		}
    		//mTextChat.append(message);
        	int scrollamout = mTextChat.getLayout().getLineTop(mTextChat.getLineCount()) - mTextChat.getHeight();
        	if (scrollamout > mTextChat.getHeight())
        		//mTextChat.scrollTo(0, scrollamout);
        	
        	mLastReceivedTime = current;
    	}
    }
    
}
