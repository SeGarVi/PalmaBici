package com.poguico.palmabici.widgets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.poguico.palmabici.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;

public class FloatingActionButton extends ImageButton {

	public enum ButtonState { UPDATE, UPDATING, SUCCESSFUL, FAILED, GOTO, ALARM_DISABLED, ALARM_ENABLED }
	
	private ButtonState state;
	private Context context;
	
	/*
     * Ugly solution to keep nice names
     * (change it to a properties file)
     */
    private static final Map<ButtonState, Integer> icons;
    static {
        Map<ButtonState, Integer> aMap = new HashMap<ButtonState, Integer>();
	    aMap.put(ButtonState.UPDATE, R.drawable.ic_sync_black_24dp);
	    aMap.put(ButtonState.UPDATING, R.drawable.ic_sync_black_24dp);
	    aMap.put(ButtonState.SUCCESSFUL, R.drawable.ic_done_black_24dp);
	    aMap.put(ButtonState.FAILED, R.drawable.ic_sync_problem_red_24dp);
	    aMap.put(ButtonState.GOTO, R.drawable.ic_directions_black_24dp);
	    aMap.put(ButtonState.ALARM_DISABLED, R.drawable.ic_notifications_black_24dp);
	    aMap.put(ButtonState.ALARM_ENABLED, R.drawable.ic_notifications_black_24dp);
	    icons = Collections.unmodifiableMap(aMap);
    }
    
    private static final Map<ButtonState, Integer> colors;
    static {
        Map<ButtonState, Integer> aMap = new HashMap<ButtonState, Integer>();
	    aMap.put(ButtonState.UPDATE, R.color.accent);
	    aMap.put(ButtonState.UPDATING, R.color.white);
	    aMap.put(ButtonState.SUCCESSFUL, R.color.succesful_updating);
	    aMap.put(ButtonState.FAILED, R.color.failed_updating);
	    aMap.put(ButtonState.GOTO, R.color.main_blue);
	    aMap.put(ButtonState.ALARM_DISABLED, R.color.disabled_alarm);
	    aMap.put(ButtonState.ALARM_ENABLED, R.color.enabled_alarm);
	    colors = Collections.unmodifiableMap(aMap);
    }
	
	public FloatingActionButton(Context context) {
		super(context);
		
		this.context = context;
		state = ButtonState.UPDATE;
	}
	
	public FloatingActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		state = ButtonState.UPDATE;
	}
	
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttrs) {
		super(context, attrs, defStyleAttrs);
		
		this.context = context;
		state = ButtonState.UPDATE;
	}
	
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttrs, int defStyleRes) {
		super(context, attrs, defStyleAttrs, defStyleRes);
		
		this.context = context;
		state = ButtonState.UPDATE;
	}
	
	
	
	public void toggleState(ButtonState state) {
		this.state = state;
		
		((GradientDrawable)this.getBackground()).setColor(colors.get(state));
		this.setImageDrawable(context.getDrawable(icons.get(state)));
	}
	
	public ButtonState getState() {
		return state;
	}
}
