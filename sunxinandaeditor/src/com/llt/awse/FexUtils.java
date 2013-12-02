/*
 * Copyright 2013 Bartosz Jankowski
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
package com.llt.awse;

import java.io.InputStream;
import android.util.Log;


public class FexUtils 
{
	final static String TAG = "AWSE.Native";	
	
	public static native byte[] compileFex(InputStream file);
	public static native byte[] decompileBin(byte[] data, int len);

	
	static
	{ 
		try
		{
			System.loadLibrary("sunxi-tools");
		} 
		catch(UnsatisfiedLinkError e)	
		{
			Log.e(TAG, "WTF. Required library not found. Please copy libsunxi-tools.so to /system/lib!");
			throw e;
		}
	}
}
