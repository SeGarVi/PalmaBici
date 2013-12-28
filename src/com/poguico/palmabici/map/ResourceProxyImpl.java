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
 * This file is a slightly modified version of the same contained at
 * the osmandroid API examples. Get it at https://code.google.com/p/osmdroid/
 */

package com.poguico.palmabici.map;

import org.osmdroid.DefaultResourceProxyImpl;

import com.poguico.palmabici.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * This is an extension of {@link org.osmdroid.DefaultResourceProxyImpl}
 * that first tries to get from the resources that this class is defined in.
 * If you don't want to copy this to your own app, you could instead use {@link org.osmdroid.util.ResourceProxyImpl}.
 */
public class ResourceProxyImpl extends DefaultResourceProxyImpl {

	private final Context mContext;

	public ResourceProxyImpl(final Context pContext) {
		super(pContext);
		mContext = pContext;
	}

	@Override
	public String getString(final string pResId) {
		try {
			final int res = R.string.class.getDeclaredField(pResId.name()).getInt(null);
			return mContext.getString(res);
		} catch (final Exception e) {
			return super.getString(pResId);
		}
	}

	@Override
	public String getString(final string pResId, final Object... formatArgs) {
		try {
			final int res = R.string.class.getDeclaredField(pResId.name()).getInt(null);
			return mContext.getString(res, formatArgs);
		} catch (final Exception e) {
			return super.getString(pResId, formatArgs);
		}
	}

	@Override
	public Bitmap getBitmap(final bitmap pResId) {
		try {
			final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
			return BitmapFactory.decodeResource(mContext.getResources(), res);
		} catch (final Exception e) {
			return super.getBitmap(pResId);
		}
	}
	
	public Bitmap getBitmap(final String pResId) {
		try {
			final int res = R.drawable.class.getDeclaredField(pResId).getInt(null);
			return BitmapFactory.decodeResource(mContext.getResources(), res);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Drawable getDrawable(final bitmap pResId) {
		try {
			final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
			return mContext.getResources().getDrawable(res);
		} catch (final Exception e) {
			return super.getDrawable(pResId);
		}
	}
}
