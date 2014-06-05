/*
 * Copyright 2014 Sergio Garcia Villalonga (yayalose@gmail.com)
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

package com.poguico.palmabici.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.poguico.palmabici.PreferencesActivity;
import com.poguico.palmabici.R;
import com.poguico.palmabici.ShareActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SidebarMenu extends Fragment{

	private static final String CONTACT_URL = "mailto:yayalose+palmabici@gmail.com";
	private static final String GPLAY_URL = "https://play.google.com/store/apps/details?id=com.poguico.palmabici";
	
	private static SidebarElement[] elements;
	private static DrawerLayout mDrawerLayout;
    private static ListView mDrawerList;
    private static ActionBarDrawerToggle mDrawerToggle;
    private static Context context;
    
	private static class SidebarElement {
		private Drawable icon;
		private String   name;
		
		protected SidebarElement(Drawable icon, String name) {
			this.icon = icon;
			this.name = name;
		}
	}
    
	private static class SidebarElementAdapter extends ArrayAdapter<SidebarElement> {
		private final Context context;
    	private final SidebarElement[] sidebarElements;
    	private int layoutResourceId = R.layout.drawer_list_item;
		
		protected SidebarElementAdapter(Context context,
				SidebarElement[] sidebarElements) {
			super(context, R.layout.drawer_list_item, sidebarElements);
			this.context = context;
			this.sidebarElements = sidebarElements;
			this.layoutResourceId = R.layout.drawer_list_item;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) context
    				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View drawerListItem = convertView;
    		
			if (drawerListItem == null) {
				drawerListItem = inflater.inflate(layoutResourceId, parent, false);
				
	    		ImageView icon =(ImageView) drawerListItem.findViewById(R.id.sidebar_element_image);
	    		TextView  text = (TextView) drawerListItem.findViewById(R.id.sidebar_element_text);
	    		
	    		icon.setImageDrawable(sidebarElements[position].icon);
	    		text.setText(sidebarElements[position].name);
			}
    		
			return drawerListItem;
		}
	}
	
	public static void setDrawer(final Activity activity) {
		context = activity.getApplicationContext();
		elements = initElements();
	    
	    mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
	    mDrawerList   = (ListView) activity.findViewById(R.id.left_drawer);
	    
	    mDrawerList.setAdapter(new SidebarElementAdapter(context, elements));
	    mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if (position == 0) {
					Intent preferencesActivity =
			            	new Intent(activity, PreferencesActivity.class);
					activity.startActivity(preferencesActivity);
				} else if (position == 1) {
					Intent issueIntent = new Intent(Intent.ACTION_VIEW);
	                issueIntent.setData(Uri.parse(GPLAY_URL));
	                activity.startActivity(issueIntent);
				} else if (position == 2) {
					Intent shareActivity =
			            	new Intent(activity, ShareActivity.class);
					activity.startActivity(shareActivity);
				} else if (position == 3) {
					Intent issueIntent = new Intent(Intent.ACTION_VIEW);
	                issueIntent.setData(Uri.parse(CONTACT_URL));
	                activity.startActivity(issueIntent);
				} else if (position == 4) {
					new CreditsDialog(activity).show();
				}
				
				new Handler().postDelayed(closeDrawerRunnable(), 200);
			}
		});

	    ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
        		activity,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

	}
	
	private static SidebarElement[] initElements () {
		SidebarElement[] aList = new SidebarElement[5];
	    aList[0] = new SidebarElement(
		    	context.getResources().getDrawable(R.drawable.ic_action_settings),
		    	context.getResources().getString(R.string.sidebar_list_settings));
	    aList[1] = new SidebarElement(
		    	context.getResources().getDrawable(R.drawable.ic_action_star),
		    	context.getResources().getString(R.string.sidebar_list_qualify));
	    aList[2] = new SidebarElement(
		    	context.getResources().getDrawable(R.drawable.ic_action_share),
		    	context.getResources().getString(R.string.sidebar_list_spread));
	    aList[3] = new SidebarElement(
		    	context.getResources().getDrawable(R.drawable.ic_action_mail),
		    	context.getResources().getString(R.string.sidebar_list_contact));
	    aList[4] = new SidebarElement(
		    	context.getResources().getDrawable(R.drawable.ic_action_user),
		    	context.getResources().getString(R.string.sidebar_list_credits));
	    return aList;
	}
	
	private static Runnable closeDrawerRunnable() {
	    return new Runnable() {

	        @Override
	        public void run() {
	        	mDrawerLayout.closeDrawer(mDrawerList);
	        }
	    };
	}
}
