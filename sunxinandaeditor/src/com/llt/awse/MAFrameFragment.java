package com.llt.awse;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MAFrameFragment extends Fragment {
	public static final String ARG_POS= "POS";
	public static final String ARG_KEYS = "KEYS";
	public static final String ARG_VALS = "VALS";

	  private int nSection;
	  private String[] hKeys;
	  private String[] hVals;
	 
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	  {
	   LinearLayout items = new LinearLayout(inflater.getContext());
	   items.setOrientation(LinearLayout.VERTICAL);
	   ScrollView sv = new ScrollView(inflater.getContext());
	   sv.addView(items);
	   // Set padding at bottom so status bar won't overlap
	   sv.setPadding(0, 0, 0, 28);
	   for(int n = 0; n<hKeys.length; ++n)
	   {   
		   
		   //Parse value and set correct layout
		   View item;
		   if(hVals[n].isEmpty()) 
		   {
			   item = inflater.inflate(R.layout.fragment_config_entry_text, null);
			   TextView cEntry = (TextView) item.findViewById(R.id.t_entry);
			   cEntry.setText(hKeys[n]);
			   items.addView(item);
			   continue;
		   }
		   else if(Helpers.isPortEntry(hVals[n]))
		   {
			   item = inflater.inflate(R.layout.fragment_config_entry_gpio, null);
			   TextView cEntry = (TextView) item.findViewById(R.id.t_entry);
			   cEntry.setText(hKeys[n]);
			   
			   String[] subvals = Helpers.getPortValues(hVals[n]);
			   
			   EditText cValue = (EditText) item.findViewById(R.id.e_port);
			   cValue.setText(subvals[0]);
			   cValue = (EditText) item.findViewById(R.id.e_function);
			   cValue.setText(subvals[1]);
			   cValue = (EditText) item.findViewById(R.id.e_resistence);
			   cValue.setText(subvals[2]);
			   cValue = (EditText) item.findViewById(R.id.e_drive_str);
			   cValue.setText(subvals[3]);
			   cValue = (EditText) item.findViewById(R.id.e_out_lvl);
			   cValue.setText(subvals[4]);
			   
			   items.addView(item);
			   continue;
		   }
		   else if(android.text.TextUtils.isDigitsOnly(hVals[n]))
		   {
			   item = inflater.inflate(R.layout.fragment_config_entry_int, null);
		   }
		   else
		   {
			   item = inflater.inflate(R.layout.fragment_config_entry_text, null);
		   }
		   TextView cEntry = (TextView) item.findViewById(R.id.t_entry);
		   cEntry.setText(hKeys[n]);
		   EditText cValue = (EditText) item.findViewById(R.id.e_value);
		   cValue.setText(hVals[n]);
		   items.addView(item);

	   }
	   
	    return sv;
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
