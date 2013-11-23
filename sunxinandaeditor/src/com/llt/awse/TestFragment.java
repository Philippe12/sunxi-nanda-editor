package com.llt.awse;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestFragment extends Fragment {
	public static final String ARG_POS= "POS";
	  private String string;
	 
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_config_entry_text, null);
	    TextView textView = (TextView) view.findViewById(R.id.t_entry);
	    textView.setText(string);
	    return view;
	  }
	  @Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);  
	  }
	  @Override
	  public void setArguments(Bundle args) {
	    string = ""+args.getInt(ARG_POS);
	  }
}
