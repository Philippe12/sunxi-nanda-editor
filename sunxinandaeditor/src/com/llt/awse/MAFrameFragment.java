package com.llt.awse;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	   for(int n = 0; n<hKeys.length; ++n)
	   { 
		   View item = inflater.inflate(R.layout.fragment_config_entry_text, null);
		   TextView cEntry = (TextView) item.findViewById(R.id.t_entry);
		   cEntry.setText(hKeys[n]);
		   TextView cValue = (TextView) item.findViewById(R.id.e_value);
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
