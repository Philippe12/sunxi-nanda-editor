package com.llt.awse;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import org.ini4j.Ini;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity {
  final static String TAG = "AWSE";
  private Boolean bReloading = false;
  private MenuItem mStatusAction;
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private DrawerEntry[] mSections;
  private String szTitle;

  public Ini fScript;

  private class DrawerEntry
  {
	  private int nIcon;
	  private String szEntry;
	  private int nCount;
	  	
	  	DrawerEntry()
	  	{
	  		nIcon = R.drawable.ic_launcher;
	  		nCount = 0;
	  		szEntry = "???";
	  	}
	  	
	  	DrawerEntry(int Icon, String Entry, int Count)
	  	{
	  		super();
	  		nIcon = Icon;
	  		szEntry = Entry; 
	  		nCount = Count;
	  	}
	  	
  }
  
  private class DrawerAdapter extends ArrayAdapter<DrawerEntry>
  {
	 int layoutResourceId;
	 Context context;
	 DrawerEntry[] entries;
	 
		DrawerAdapter(Context context, int layoutResourceId, DrawerEntry[] ents)
		{
			super(context, layoutResourceId, ents);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.entries = ents;
		}
		
	    private class EntryHolder
	    {
	        ImageView vIcon;
	        TextView vSectName;
	        TextView vSectCount;
	    }
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
		    View v = convertView;
		    EntryHolder holder = null;
			
			   if(v == null)
		        {
		            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		            v = inflater.inflate(layoutResourceId, parent, false);
		            
		            holder = new EntryHolder();
		            holder.vIcon = (ImageView)v.findViewById(R.id.sect_icon);
		            holder.vSectName = (TextView)v.findViewById(R.id.sect_name);
		            holder.vSectCount = (TextView)v.findViewById(R.id.sect_count);
		            v.setTag(holder);
		        }
		        else
		        {
		            holder = (EntryHolder)v.getTag();
		        }
		        
			    DrawerEntry e = entries[position];
		        holder.vSectName.setText(e.szEntry);
		        holder.vSectCount.setText(""+e.nCount);
		        holder.vIcon.setImageResource(e.nIcon);

		        return v;
		}
		
		
  }
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_mainlayout);
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        AssetManager am = getAssets();        
        
        fScript = new Ini();
        try
        {
        	fScript.load(am.open("script.fex"));
        } catch (IOException e)	{
			Log.e(TAG, "Cannot load script.fex asset!");
			e.printStackTrace();
		}
       // mSections = fScript.keySet().toArray(new String[0]);
        
        // Generate layout for drawer
        mSections = new DrawerEntry[fScript.size()];
        Set<String> names = fScript.keySet();
        
        for(int i = 0; i< fScript.size(); ++i)
        	mSections[i] = new DrawerEntry(R.drawable.ic_launcher, names.toArray(new String[0])[i] , fScript.get(names.toArray(new String[0])[i]).size());
  
        DrawerAdapter da = new DrawerAdapter(this, R.layout.drawer_item, mSections);
             
        mDrawerList.setAdapter(da);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerToggle = new ActionBarDrawerToggle(this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */) {

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Select a section");  
            }
            
            public void onDrawerClosed(View view) {
            	if(szTitle == null)
            		getActionBar().setTitle(R.string.app_name);
            	else
            		getActionBar().setTitle(szTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        

    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.edit, menu);
      mStatusAction = menu.findItem(R.id.menu_status);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
        switch(item.getItemId())
        {
            case R.id.menu_exit:
                finish();
                break;
            case R.id.menu_reload:
            	if(mStatusAction == null)
            		return true;
            	if(!bReloading)
            	{
            		mStatusAction.setVisible(true);
            		mStatusAction.expandActionView();
            		bReloading = true;
            	}
            	else
            	{
            		mStatusAction.setVisible(false);
            		mStatusAction.collapseActionView();
            		bReloading = false;
            	}
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    
/** Swaps fragments in the main content view */

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putInt(TestFragment.ARG_POS, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.content_frame, fragment)
                       .commit();
       // fragmentManager.

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        szTitle = getString(R.string.app_short) + " : " + mSections[position];
        getActionBar().setTitle(szTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

  
} 