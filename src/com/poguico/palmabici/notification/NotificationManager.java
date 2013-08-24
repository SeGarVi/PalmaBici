/*
 * Copyright 2012 Sergio Garcia Villalonga (yayalose@gmail.com)
 *
 * This file is part of PalmaBici.
 *
 *    PalmaBici is free software: you can redistribute it and/or modify
 *    it under the terms of the Affero GNU General Public License version 3
 *    as published by the Free Software Foundation.
 *
 *    PalmaBici is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    Affero GNU General Public License for more details
 *    (https://www.gnu.org/licenses/agpl-3.0.html).
 *    
 */

package com.poguico.palmabici.notification;

import java.util.ArrayList;

import com.poguico.palmabici.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class NotificationManager {
	private static ArrayList<Message> msgQueue = new ArrayList<Message>();
	private static ShowLabelTask      msgDispatcher;
	
	public static class Message {
		private TextView textView;
		private String   message;
        private long    duration;
        private Activity parent;
        private boolean problem;
        
		public Message (TextView textView,
                         String   message,
                         long    duration,
                         Activity parent,
                         boolean problem) {
			this.textView = textView;
			this.message  = message;
			this.duration = duration;
			this.parent   = parent;
			this.problem  = problem;
		}

		public TextView getTextView() {
			return textView;
		}

		public String getMessage() {
			return message;
		}

		public long getDuration() {
			return duration;
		}

		public Activity getParent() {
			return parent;
		}

		public boolean isProblem() {
			return problem;
		}
	}
	
	private static class ShowLabelTask extends AsyncTask <Void, Void, Void> {
		private Message   msg;
		private TextView  textView;
		private long     duration;
		private Animation showLabel;
		private Animation hideLabel;
		private Activity  parent;
		private boolean  problem;
		
		public ShowLabelTask (Message msg) {
			this.msg      = msg;
			this.textView = msg.getTextView();
			this.duration = msg.getDuration();
			this.parent   = msg.getParent();
			this.problem  = msg.isProblem();
			
			showLabel = AnimationUtils.loadAnimation(parent, R.anim.push_up_in);
			hideLabel =
					AnimationUtils.loadAnimation(parent, R.anim.push_down_out);
			
			textView.setText(msg.getMessage());
			textView.setBackgroundResource((problem)?R.color.problem_palmabici:
                                                     R.color.pressed_palmabici);
			textView.setTextColor((problem)?0xFFFFFFFF:0xFF000000);
			textView.setVisibility(View.VISIBLE);
			textView.startAnimation(showLabel);
		}
		
    	protected Void doInBackground(Void... params) {
    		try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
        }

        protected void onPostExecute(Void params) {
        	synchronized (msgQueue) {
        		msgQueue.remove(msg);
        		if (!msgQueue.isEmpty()) {
        			ShowLabelTask nextNotification =
        					new ShowLabelTask(msgQueue.get(0));
        			nextNotification.execute((Void [])null);
        		} else {
                	textView.startAnimation(hideLabel);
                	textView.setVisibility(View.INVISIBLE);
        			NotificationManager.finalizeDispatcher();
        		}
        	}
        }
    }
	
	public static void showMessage(TextView textView,
                                      String   message,
                                      long    duration,
                                      Activity parent,
                                      boolean problem) {
		Message msg = new Message(textView, message, duration, parent, problem);
		
		synchronized (msgQueue) {
			msgQueue.add(msg);
		
			if (msgDispatcher == null) {
				msgDispatcher = new ShowLabelTask(msg);
				msgDispatcher.execute((Void [])null);
			}
		}
	}
	
	private static void finalizeDispatcher() {
		msgDispatcher = null;
	}
}
