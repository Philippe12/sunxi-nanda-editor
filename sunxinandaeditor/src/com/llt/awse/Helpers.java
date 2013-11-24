package com.llt.awse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.PatternMatcher;

public class Helpers
{
	final static Pattern pRegEx = Pattern.compile("^port:(.*)<(.*)><(.*)><(.*)><(.*)>$");
	
	static boolean isPortEntry(String val)
	{
		return pRegEx.matcher(val).find();
	}
	
	static String[] getPortValues(String val)
	{
		Matcher m = pRegEx.matcher(val);
		String[] ret = new String[5];
		if(m.find())
		{
			ret[0] = m.group(1);
			ret[1] = m.group(2);
			ret[2] = m.group(3);
			ret[3] = m.group(4);
			ret[4] = m.group(5);
			
		}
		return ret;
	}
}
