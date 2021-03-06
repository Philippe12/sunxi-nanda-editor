/*
 * Copyright 2013-2014 Bartosz Jankowski
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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spazedog.lib.rootfw3.RootFW;

import org.ini4j.Ini;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity
{
  final static String TAG = "AWSE";

  private Boolean bReloading = false;
  private MenuItem mStatusAction;
  private DrawerLayout mDrawerLayout;
  private DrawerAdapter da;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private DrawerEntry[] mSections;
  private String szTitle;
  private final RootFW mRoot = new RootFW(true);
  private final Context mContext = this;

  public Ini fScript;

 	@Override
	protected void onStop()
	{
        Helpers.unmountLoader(mRoot);
	  	Helpers.removeTempFiles();
        mRoot.disconnect();
	  	super.onStop();
	}

    @Override
    protected void onDestroy() {

        Helpers.unmountLoader(mRoot);
        Helpers.removeTempFiles();

        if(mRoot.isConnected())
            mRoot.disconnect();

        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Crashlytics.start(this);

        setContentView(R.layout.main_layout_drawer);

        final ActionBar actionBar = getActionBar();

        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Check root access...
        if(!mRoot.isConnected())
            if (mRoot.connect()) {
                if (!mRoot.isRoot()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Error!");
                    alertDialog.setMessage("App needs root access to work!");
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
            } else {
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
            }

        //Check if it's an Allwinner CPU
        if (!Build.HARDWARE.equals("sun3i") && !Build.HARDWARE.equals("sun4i") && !Build.HARDWARE.equals("sun5i") &&
                !Build.HARDWARE.equals("sun6i") && !Build.HARDWARE.equals("sun7i") && !Build.HARDWARE.equals("sun8i")) {
            Log.e(TAG, "Unknown hardware '" + Build.HARDWARE + "' ! Are you sure it's an Allwinner device?");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);

            MADialogFragment alert = MADialogFragment.newInstance("Error", "Unknown hardware detected. Further actions may damage device.\nDo you want to continue?", MADialogFragment.UI_DIALOG_YESNO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        finish();
                    } else {
                        //Prepare UI
                        new InitUITask(mContext, mRoot, getFragmentManager()).execute();
                    }
                }
            });
            alert.show(ft, "dialog");
        } else {
               new InitUITask(mContext, mRoot, getFragmentManager()).execute();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Select a section");
            }

            public void onDrawerClosed(View view) {
                if (szTitle == null)
                    getActionBar().setTitle(R.string.app_name);
                else
                    getActionBar().setTitle(szTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           setTranslucent(true);

        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.actionbar_background_dark));
    }

    public void updateAdapter(Ini ini)
    {
        fScript = ini;
        // Generate layout for drawer
        mSections = new DrawerEntry[fScript.size()];

        int i = 0;
        for (String n : fScript.keySet())
        {
            mSections[i] = new DrawerEntry(R.drawable.ic_launcher, n, fScript.get(n).size());
            ++i;
        }

        da = new DrawerAdapter(this, R.layout.main_layout_drawer_item, mSections);
        mDrawerList.setAdapter(da);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
        mDrawerList.setPadding(mDrawerList.getPaddingLeft(), config.getPixelInsetTop(true), mDrawerList.getPaddingRight(), config.getPixelInsetBottom());
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

    private void selectItem(int position)
    {
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
    
    
// Right corner menu stuff...
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
            case R.id.menu_about:
                showAboutDialog();
            break;
            case R.id.menu_saveas:
                showExportDialog();
                break;
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog()
    {
        String version;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "?";
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MADialogFragment alert = MADialogFragment.newInstance(getString(R.string.app_name),
                "<p><b>AW-SE</b> version " + version +  " (c) Bartosz Jankowski" +
                "<p>Disclaimer: Inappropriate use of this application may be lead to damage of the device. I don't take responsibility " +
                        "for any defects done by this application. <b>You use it at own risk</b></p>" +
                "<p>This program uses" +
                "<div>\t• sunxi-tools (c) Alejandro Mery <a href='https://github.com/linux-sunxi/sunxi-tools'>GitHub</a></div>" +
                "<div>\t• ini4j (c) Ivan Szkiba <a href='http://ini4j.sourceforge.net'>SourceForge</a></div>" +
                "<div>\t• SystemBarTint (c) Jeff Gilfelt <a href='https://github.com/jgilfelt/SystemBarTint'>GitHub</a></div>" +
                "<div>\t• RootFW (c) Daniel Bergløv <a href='https://github.com/SpazeDog/rootfw'>GitHub</a></div>" +
                "</p>"
                , MADialogFragment.UI_DIALOG_ABOUT, null);
        alert.show(ft, "dialog");
    }

    private void showExportDialog()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MADialogFragment alert = MADialogFragment.newInstance(Environment.getExternalStorageDirectory().getAbsolutePath(), null, MADialogFragment.UI_DIALOG_SAVE, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MADialogFragment alert = (MADialogFragment) getFragmentManager().findFragmentByTag("dialog");
                switch(v.getId()) {
                    case R.id.awse_save_dialog_button_cancel:
                        alert.dismiss();
                        break;
                    case R.id.awse_save_dialog_button_save:
                        final String filename = ((EditText)alert.getDialog().findViewById(R.id.awse_save_dialog_file_name)).getText().toString();
                        final int type = ((Spinner)alert.getDialog().findViewById(R.id.awse_save_dialog_file_type)).getSelectedItemPosition();
                        final File path = alert.getCurrentLocation();
                        alert.dismiss();
                        if(type == 0) // FEX
                        {
                            try {
                                Helpers.exportIni(fScript, filename, path);
                                Toast.makeText(alert.getActivity(), "Script has been exported successfully!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to export ini: " + e);
                            }
                        }
                        else    // BIN
                        {
                            try {
                                Helpers.exportBin(fScript, filename, path);
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to export bin: " + e);
                                Toast.makeText(alert.getActivity(), "Script has been exported successfully!", Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                }
            }
        });

        alert.show(ft, "dialog");
    }

    @TargetApi(19)
    private void setTranslucent(boolean on)
    {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            winParams.flags &= ~(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        win.setAttributes(winParams);
    }
}