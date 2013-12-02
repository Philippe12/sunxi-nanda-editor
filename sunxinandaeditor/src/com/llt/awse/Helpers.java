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
