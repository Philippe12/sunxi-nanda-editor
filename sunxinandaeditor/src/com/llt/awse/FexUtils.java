package com.llt.awse;

import java.io.InputStream;

public class FexUtils 
{
	public static native byte[] compileFex(InputStream file);
	public static native byte[] decompileBin(byte[] data, int len);
	
	static 
	{ 
		System.loadLibrary("sunxi-tools");
	}
}
