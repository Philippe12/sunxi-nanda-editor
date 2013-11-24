package com.llt.awse;

import java.io.File;

public class FexUtils 
{
	public static native Byte[] compileFex(File file);
	public static native String decompileBin(File file);
	
	static 
	{
		System.loadLibrary("libsunxi-tools");
	}
}
