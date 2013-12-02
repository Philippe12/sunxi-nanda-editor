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

import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MAFrameFragment extends Fragment {
	public static final String ARG_POS = "POS";
	public static final String ARG_KEYS = "KEYS";
	public static final String ARG_VALS = "VALS";

	private int nSection;
	private String[] hKeys;
	private String[] hVals;
	private EntryAdapter mHost;
	
	private final String TAG = "AWSE";
      
      private interface ScriptEntry
      {
      }
      
      private class TextEntry implements ScriptEntry
      {
         String szKey;
         String szValue;
      }
      
      private class IntEntry extends TextEntry {}

      private class GpioEntry implements ScriptEntry
      {
          String szKey;
    	  String szPort;
    	  String szFunction;
    	  String szResistence;
    	  String szDriveStrength;
    	  String szOutputLevel;       
      }
	  
      
	  private class EntryAdapter extends BaseAdapter {
	        
	        private ArrayList<ScriptEntry> mSectionData;
	        private LayoutInflater mInflater;
	        private Context mContext;
	 
	        
	      	private static final int TYPE_TEXT = 0;
	      	private static final int TYPE_INT = 1;
	      	private static final int TYPE_GPIO = 2;
	        
	        private class TextHolder 
	        {
	           TextView tKey;
	           EditText eValue;
	        }
	        
	        private class IntHolder extends TextHolder {}

	        private class GpioHolder
	        {
	           TextView tKey;
	           EditText ePort;
	           EditText eFunction;
	           EditText eResistence;
	           EditText eDriveStrength;
	           EditText eOutputLevel;       
	        }
	  	  
	        
	        public EntryAdapter(Context context, ArrayList<ScriptEntry> sectionItems)
	        {
	        	mContext = context;
	            mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            mSectionData = sectionItems;
	        }

	 
	        @Override
	        public int getItemViewType(int position)
	        {
	        	if(mSectionData.get(position).getClass() == TextEntry.class)
	        	{
	        		return TYPE_TEXT;
	        	}
	        	else if(mSectionData.get(position).getClass() == IntEntry.class)
	        	{
	        		return TYPE_INT;
	        	}
	        	else if(mSectionData.get(position).getClass() == GpioEntry.class)
	        	{
	        		return TYPE_GPIO;
	        	}
	            return super.getItemViewType(position);
	        }
	 
	        @Override
	        public int getViewTypeCount()
	        {
	            return 3;
	        }
	 
	        @Override
	        public int getCount() 
	        {
	            return mSectionData.size();
	        }
	 
	        @Override
	        public ScriptEntry getItem(int position)
	        {
	            return mSectionData.get(position);
	        }
	 
	        @Override
	        public long getItemId(int position) 
	        {
	            return position;
	        }
	 
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) 
	        {
	            Object holder = null;
	            int type = getItemViewType(position);
	            if (convertView == null) 
	            {
	                switch (type) 
	                {
	                    case TYPE_TEXT:
	                    	holder = new TextHolder();
	                        convertView = mInflater.inflate(R.layout.fragment_config_entry_text, null);
	                        ((TextHolder)holder).tKey = (TextView)convertView.findViewById(R.id.t_entry);
	                        ((TextHolder)holder).eValue = (EditText)convertView.findViewById(R.id.e_value);
	                        break;
	                    case TYPE_INT:
	                    	holder = new IntHolder();
	                        convertView = mInflater.inflate(R.layout.fragment_config_entry_int, null);
	                        ((IntHolder)holder).tKey = (TextView)convertView.findViewById(R.id.t_entry);
	                        ((IntHolder)holder).eValue = (EditText)convertView.findViewById(R.id.e_value);
	                        break;
	                    case TYPE_GPIO:
	                    	holder = new GpioHolder();
	                        convertView = mInflater.inflate(R.layout.fragment_config_entry_gpio, null);
	                        ((GpioHolder)holder).tKey = (TextView)convertView.findViewById(R.id.t_entry);
	                        ((GpioHolder)holder).ePort = (EditText)convertView.findViewById(R.id.e_port);
	                        ((GpioHolder)holder).eFunction = (EditText)convertView.findViewById(R.id.e_function);
	                        ((GpioHolder)holder).eResistence = (EditText)convertView.findViewById(R.id.e_resistence);
	                        ((GpioHolder)holder).eDriveStrength = (EditText)convertView.findViewById(R.id.e_drive_str);
	                        ((GpioHolder)holder).eOutputLevel = (EditText)convertView.findViewById(R.id.e_out_lvl);
	                        break;
	                }
	                convertView.setTag(holder);
	            }
	            else holder = convertView.getTag();
	            
	            switch(type)
	            {
	                case TYPE_TEXT:
	                	((TextHolder)holder).tKey.setText(((TextEntry)mSectionData.get(position)).szKey);
	                	((TextHolder)holder).eValue.setText(((TextEntry)mSectionData.get(position)).szValue);
	                	break;
	                case TYPE_INT:
	                	((IntHolder)holder).tKey.setText(((IntEntry)mSectionData.get(position)).szKey);
	                	((IntHolder)holder).eValue.setText(((IntEntry)mSectionData.get(position)).szValue);
	                	break;
	                case TYPE_GPIO:
	                	((GpioHolder)holder).tKey.setText(((GpioEntry)mSectionData.get(position)).szKey);
	                	((GpioHolder)holder).ePort.setText(((GpioEntry)mSectionData.get(position)).szPort);
	                	((GpioHolder)holder).eFunction.setText(((GpioEntry)mSectionData.get(position)).szFunction);
	                	((GpioHolder)holder).eResistence.setText(((GpioEntry)mSectionData.get(position)).szResistence);
	                	((GpioHolder)holder).eDriveStrength.setText(((GpioEntry)mSectionData.get(position)).szDriveStrength);
	                	((GpioHolder)holder).eOutputLevel.setText(((GpioEntry)mSectionData.get(position)).szOutputLevel);
	                	break;
	            }
	            return convertView;
	        }    
	  }
	  
	  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState)
	{
		LinearLayout aLayoutView = new LinearLayout(inflater.getContext());
		ListView aListView = new ListView(inflater.getContext());
		ArrayList<ScriptEntry> aItems = new ArrayList<ScriptEntry>();

		aLayoutView.setOrientation(LinearLayout.VERTICAL);
		aLayoutView.setPadding(0, 0, 0, 30);
		aLayoutView.addView(aListView);
		
		aListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		
		for (int n = 0; n < hKeys.length; ++n)
		{
			// Parse value and set correct layout

			if (Helpers.isPortEntry(hVals[n]))
			{
				String[] subvals = Helpers.getPortValues(hVals[n]);

				GpioEntry t = new GpioEntry();
				t.szKey = hKeys[n];
				t.szPort = subvals[0];
				t.szFunction = subvals[1];
				t.szResistence = subvals[2];
				t.szDriveStrength = subvals[3];
				t.szOutputLevel = subvals[4];

				aItems.add(t);
			} 
			else if (android.text.TextUtils.isDigitsOnly(hVals[n]))
			{
				IntEntry t = new IntEntry();
				t.szKey = hKeys[n];
				t.szValue = hVals[n];

				aItems.add(t);
			} 
			else
			{
				TextEntry t = new TextEntry();
				t.szKey = hKeys[n];
				t.szValue = hVals[n];

				aItems.add(t);				
			}
		}

		mHost = new EntryAdapter(inflater.getContext(), aItems);
		aListView.setAdapter(mHost);
		return aLayoutView;
	}
	  
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void setArguments(Bundle args)
	{
		nSection = args.getInt(ARG_POS);
		hKeys = args.getStringArray(ARG_KEYS);
		hVals = args.getStringArray(ARG_VALS);
	}
}
