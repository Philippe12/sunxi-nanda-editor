package com.llt.awse;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import org.ini4j.Ini;

import com.crashlytics.android.Crashlytics;
import com.spazedog.lib.rootfw3.RootFW;
import com.spazedog.lib.rootfw3.extenders.FileExtender.File;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build;
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

public class MainActivity extends Activity {
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
  
  
	private void unmountLoader()
	{
		final RootFW root = new RootFW(true);
		if (root.connect())
		{
			if(root.isRoot())
			{
				if(root.filesystem("/dev/block/nanda").isMounted())
				{
					Log.v(TAG, "Unmounting device...");
					if(root.filesystem("/dev/block/nanda").removeMount())
					//Use rmdir to prevent from important files removal!
					root.shell().run("rmdir /mnt/awse");
				}
				else
				{
					Log.v(TAG,"Skipping unmount routine, cause loader isn't mounted");
				}
			}
			else
			{
				Log.w("TAG", "Device hasn't root access -- didn't have a chance to unmount");
			}
			root.disconnect();
		}
	}
	  
  
	private void removeTempFiles()
	{
		java.io.File f = new java.io.File("/sdcard/awse/script.bin");
		if(f.exists())
			f.delete();
	}
	
	@Override 
	protected void onPause()
	{
		unmountLoader();
	  	super.onPause();
	}
	  
	@Override
	protected void onDestroy()
	{
		// Clean up mounted point
	  	unmountLoader();
	  	removeTempFiles();
	  	super.onDestroy();
	}
  
	@Override
	protected void onResume()
	{
		super.onResume();
		//TODO: Add mounting function...

	}
  
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.main_layout_drawer);
        final ActionBar actionBar = getActionBar();
        
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
/*             
        AssetManager am = getAssets();        
        byte[] aFexData = {};
        try {
        	AssetFileDescriptor afd = am.openFd("script.mp3");
      
        	byte[] aBinData = new byte[(int)afd.getLength()];
        	DataInputStream dis = new DataInputStream(afd.createInputStream());
        	dis.readFully(aBinData);
        	aFexData = FexUtils.decompileBin(aBinData, aBinData.length);
		//	Log.e(TAG, new String(aFexData, "US-ASCII"));
		 } catch (IOException e1) {
			Log.e(TAG, "Cannot open script.bin asset!");
			e1.printStackTrace();
		}
        */
                   
        //Check if it's an Allwinner CPU
        
        if( !Build.HARDWARE.equals("sun4i") && !Build.HARDWARE.equals("sun5i") && 
        		!Build.HARDWARE.equals("sun6i") && !Build.HARDWARE.equals("sun7i")) 
        {
        	Log.e(TAG, "Unknown hardware '" + Build.HARDWARE + "' ! Are you sure it's an Allwinner device?");
        	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Error!");
			alertDialog.setMessage("Unknown hardware detected. Further actions may damage device.\nDo you want to continue?");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
			   @Override
			public void onClick(DialogInterface dialog, int which) {
			     alertDialog.dismiss();
			   }
			});
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Nope!", new DialogInterface.OnClickListener() {
				   @Override
				public void onClick(DialogInterface dialog, int which) {
				     alertDialog.dismiss();
				     finish();
				   }
				});
			alertDialog.show();	
        }
        
// Check root access...
        
        final RootFW root = new RootFW(true);

        if (root.connect())
        {
            if(!root.isRoot())
            {
            	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    			alertDialog.setTitle("Error!");
    			alertDialog.setMessage("App needs root access to work!");
    			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    			     alertDialog.dismiss();
    			     root.disconnect();
    			     finish();
    			   }
    			});
    			alertDialog.show();	
    			return;
            }
            // Try to remount root dir\=
            if(root.filesystem("rootfs").addMount("/", new String[] {"rw","remount"}))
            {
            	Log.i(TAG,"Succesfully remounted root dir!");
            }
            
            //Create AWeSomE temp directories
            
            File f = root.file("/mnt/awse");
            f.createDirectory();
            f = root.file("/sdcard/awse");
            f.createDirectory();
            	Log.v(TAG,"Trying to mount bootloader partition...");
            	//int res = root.shell().run("busybox mount -oro,loop -tvfat /dev/block/nanda /mnt/awse").getResultCode();
            	if(root.filesystem("/dev/block/nanda").addMount("/mnt/awse","vfat",new String[] {"ro"}))
            		Log.v(TAG, "Successfully mounted!");
            	else if(root.filesystem("/mnt/awse").isMounted())
            		Log.v(TAG, "Device is already mounted!");
            	else
            	{
                	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        			alertDialog.setTitle("Error!");
        			alertDialog.setMessage("Couldn't mount the device. Try to update su binary (Download SuperSU or SuperUser) or restart the device!");
        			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
        			   @Override
        			public void onClick(DialogInterface dialog, int which) {
        			     alertDialog.dismiss();
        			     //Use rmdir to prevent from important files removal!
        			     root.shell().run("rmdir /mnt/awse");
        			     root.disconnect();
        			     finish();
        			   }
        			});
        			alertDialog.show();
        			return;
            	}
        
        
	        File fBin = root.file("/mnt/awse/script.bin");
	        if(!fBin.exists())
	        {
	        	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    			alertDialog.setTitle("WTF!");
    			alertDialog.setMessage("Didn't find script.bin file on bootloader partition. This is may cause device brick if you reboot!");
    			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    			     alertDialog.dismiss();
    			     //Use rmdir to prevent from important files removal!
    			     root.shell().run("rmdir /mnt/awse");
    			     root.disconnect();
    			     finish();
    			   }
    			});
    			alertDialog.show();	
    			return;
	        }
	        if(fBin.copy("/sdcard/awse/script.bin"))
	        {
	        	Log.i(TAG, "Successfully copied script to sdcard");
	        	unmountLoader();
	        }
	        else
	        {
	        	Log.e(TAG, "Failed to copy script to sdcard!");
	        	finish();
	        }
	        java.io.File binCopy = new java.io.File("/sdcard/awse/script.bin");
	        FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(binCopy);
			} catch (FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
        	byte[] aBinData = new byte[(int)binCopy.length()];
        	DataInputStream dis = new DataInputStream(fis);
        	try
			{
				dis.readFully(aBinData);
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	if(aBinData.length == 0) return;
	    	byte[] aFexData = FexUtils.decompileBin(aBinData, aBinData.length);
	        ByteArrayInputStream is = new ByteArrayInputStream(aFexData);
	        
	        fScript = new Ini();
	        try
	        {
	        	fScript.load(is);
	        	
	        } catch (IOException e)	{
				Log.e(TAG, "Cannot parse decompiled script!");
				e.printStackTrace();
			}
	
	        // Generate layout for drawer
	        mSections = new DrawerEntry[fScript.size()];
	        Set<String> names = fScript.keySet();
	        
	        Iterator<String> it = names.iterator();
	        
	        for(int i = 0; it.hasNext(); ++i)
	        {
	        	String szSection = it.next();
	        	mSections[i] = new DrawerEntry(R.drawable.ic_launcher, szSection , fScript.get(szSection).size());
	        }
	        
	        	
	  
	        DrawerAdapter da = new DrawerAdapter(this, R.layout.main_layout_drawer_item, mSections);
	             
	        mDrawerList.setAdapter(da);
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	        
	        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
	
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
	        
	        
	       // findViewById(R.id.content_frame);
	       root.disconnect();
       }
       else
       {
    	   	Log.e(TAG, "WTF. Couldn't connect to RootTools!");
        	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("WTF!");
			alertDialog.setMessage("Cant connect to RootTools!");
			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
			   @Override
			public void onClick(DialogInterface dialog, int which) {
			     alertDialog.dismiss();
			     finish();
			   }
			});
			alertDialog.show();	
			return;
        }

    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(mDrawerToggle != null)
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null)
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new MAFrameFragment();
        Bundle args = new Bundle();
        
        String[] keys = fScript.get(mSections[position].szEntry).keySet().toArray(new String[0]);      
        String[] vals = fScript.get(mSections[position].szEntry).values().toArray(new String[0]);
        		
        args.putStringArray(MAFrameFragment.ARG_KEYS, keys);
        args.putStringArray(MAFrameFragment.ARG_VALS, vals);
        args.putInt(MAFrameFragment.ARG_POS, position);
        
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.content_frame, fragment)
                       .commit();

        mDrawerList.setItemChecked(position, true);
        szTitle = getString(R.string.app_short) + " : " + mSections[position].szEntry;
        getActionBar().setTitle(szTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    
// Left corner menu stuff...
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
      getMenuInflater().inflate(R.menu.edit, menu);
      mStatusAction = menu.findItem(R.id.menu_status);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) 
          return true;
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

  
} 