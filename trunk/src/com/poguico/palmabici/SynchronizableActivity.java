package com.poguico.palmabici;

import android.app.Activity;

public abstract class SynchronizableActivity extends Activity {
	public abstract void successfulSynchronization();
	public abstract void unsuccessfulSynchronization();
}
