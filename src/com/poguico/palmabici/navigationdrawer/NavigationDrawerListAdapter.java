package com.poguico.palmabici.navigationdrawer;

import java.util.ArrayList;
import java.util.List;

import com.poguico.palmabici.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerListAdapter extends ArrayAdapter<NavigationDrawerItem> {

	private Context context;
    private List<NavigationDrawerItem> items;
    int resource;
	
    public NavigationDrawerListAdapter(Context context, int resource,
			List<NavigationDrawerItem> items) {
		super(context, resource, items);
		this.context = context;
    	this.items   = items;
    	this.resource = resource;
	}
    
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public NavigationDrawerItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 
        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
              LayoutInflater inflater = ((Activity) context).getLayoutInflater();
              drawerHolder = new DrawerItemHolder();

              view = inflater.inflate(resource, parent, false);
              drawerHolder.ItemName = (TextView) view
                          .findViewById(R.id.sidebar_element_text);
              drawerHolder.icon = (ImageView) view.findViewById(R.id.sidebar_element_image);

              view.setTag(drawerHolder);

        } else {
              drawerHolder = (DrawerItemHolder) view.getTag();

        }

        NavigationDrawerItem item = (NavigationDrawerItem) items.get(position);

        drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                    item.getIcon()));
        drawerHolder.ItemName.setText(item.getTitle());

        return view;
		
		/*
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, parent);
        }
		
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.sidebar_element_image);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.sidebar_element_text);
        
        imgIcon.setImageResource(items.get(position).getIcon());       
        txtTitle.setText(items.get(position).getTitle());
        
        return convertView;*/
	}
	
	private static class DrawerItemHolder {
        TextView ItemName;
        ImageView icon;
	}
}
