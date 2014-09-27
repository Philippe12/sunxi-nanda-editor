package com.llt.awse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.spazedog.lib.rootfw3.RootFW;
import com.spazedog.lib.rootfw3.extenders.FileExtender;

import org.ini4j.Ini;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class InitUITask extends AsyncTask<Void, Integer, Integer>
{
    final static String TAG = "AWSE.InitUI";

    final private Context mContext;
    final private RootFW mRoot;
    private Ini mScript;
    final private FragmentManager mFragmentManager;

    final static int UI_DIALOG_FAILED_REMOUNT_ROOT = 0;
    final static int UI_DIALOG_FAILED_MOUNT_LOADER = 1;
    final static int UI_DIALOG_FAILED_SCRIPT_NOT_FOUND = 2;
    final static int UI_DIALOG_FAILED_TO_BACKUP_SCRIPT = 3;
    final static int UI_DIALOG_FAILED_BAD_SCRIPT = 4;
    final static int UI_DIALOG_PROCESSING = 5;
    final static int UI_DIALOG_READING_SECTION = 6;
    final static int UI_DIALOG_FAILED_NO_LIBRARY = 7;

    final static int UI_DIALOG_INIT_OK = 100;

    final DialogInterface.OnClickListener lExitDialog = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ((Activity)mContext).finish();
        }
    };

    public InitUITask(Context context, RootFW root, FragmentManager fm)
    {
        mContext = context;
        mRoot = root;
        mFragmentManager = fm;
    }

    @Override
    protected void onPostExecute(Integer errCode) {
        if(errCode == UI_DIALOG_INIT_OK) {
            hideDialog();
            ((MainActivity) mContext).updateAdapter(mScript);
        }
        else {
            showDialog(errCode);
        }
    }

    private void showDialog(int type)
    {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setTransition(ft.TRANSIT_FRAGMENT_FADE);

        MADialogFragment alert = (MADialogFragment)mFragmentManager.findFragmentByTag("dialog");

        if(alert == null) {
            alert = MADialogFragment.newInstance("Please wait", "Examining hardware...", MADialogFragment.UI_DIALOG_PROCESSING, null);
        }
        else
        {
            ft.remove(alert);
            switch (type)
            {
                case UI_DIALOG_READING_SECTION:
                    alert = MADialogFragment.newInstance("Please wait", "Reading sections...", MADialogFragment.UI_DIALOG_PROCESSING, null);
                    break;
                case UI_DIALOG_PROCESSING:
                    alert = MADialogFragment.newInstance("Please wait", "Examining hardware...", MADialogFragment.UI_DIALOG_PROCESSING, null);
                    break;
                case UI_DIALOG_FAILED_REMOUNT_ROOT:
                    alert = MADialogFragment.newInstance("Error", "Failed to remount root directory", MADialogFragment.UI_DIALOG_ERROR, lExitDialog);
                    break;
                case UI_DIALOG_FAILED_MOUNT_LOADER:
                    alert = MADialogFragment.newInstance("Error", "Failed to mount the device. Try to update su binary (Download SuperSU or SuperUser) or restart the device", MADialogFragment.UI_DIALOG_ERROR, lExitDialog);
                    break;
                case UI_DIALOG_FAILED_SCRIPT_NOT_FOUND:
                    alert = MADialogFragment.newInstance("Error", "Failed to find script.bin file on bootloader partition", MADialogFragment.UI_DIALOG_ERROR, lExitDialog);
                    break;
                case UI_DIALOG_FAILED_TO_BACKUP_SCRIPT:
                    alert = MADialogFragment.newInstance("Error", "Failed to copy script to the internal memory", MADialogFragment.UI_DIALOG_ERROR, lExitDialog);
                    break;
                case UI_DIALOG_FAILED_BAD_SCRIPT:
                    alert = MADialogFragment.newInstance("Error", "Failed to parse the script file", MADialogFragment.UI_DIALOG_ERROR, lExitDialog);
                    break;
                case UI_DIALOG_FAILED_NO_LIBRARY:
                    alert = MADialogFragment.newInstance("Error", "Cannot load native library\nPlease copy libsunxi-tools.so to /system/lib/ or reinstall app", MADialogFragment.UI_DIALOG_ERROR, lExitDialog);
                    break;
            }
        }
            try {
                alert.show(ft, "dialog");
            } catch(IllegalStateException e) {
            }

    }

    private void hideDialog()
    {
        MADialogFragment dialog = (MADialogFragment)mFragmentManager.findFragmentByTag("dialog");
        if(dialog != null)
        {
            try {
            dialog.dismiss();
            } catch(IllegalStateException e) {
            }
        }
    }

    @Override
    protected void onPreExecute() {
        showDialog(UI_DIALOG_PROCESSING);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int msgId = values[0];
        showDialog(msgId);
     }


    private Integer getScriptAlternative()
    {
        Ini ini = new Ini();
        publishProgress(UI_DIALOG_READING_SECTION);
        for(String section : Helpers.mScriptSections) {
            Helpers.getScriptSection(mRoot, section, ini);
        }
        mScript = ini;
        return UI_DIALOG_INIT_OK;
    }


    @Override
    protected Integer doInBackground(Void... args) {

        Boolean isScriptDumpAvailable =  mRoot.file("/sys/class/script/dump").exists();   // Use for Boot 2.0

        if (mRoot.filesystem("rootfs").addMount("/", new String[]{"rw", "remount"})) {
            Log.i(TAG, "Succesfully remounted root dir!");
        }
        else
        {
            return UI_DIALOG_FAILED_REMOUNT_ROOT;
        }
        //Create AWeSomE temp directories
        FileExtender.File f = mRoot.file("/mnt/awse");
        f.createDirectory();
        f = mRoot.file(Environment.getExternalStorageDirectory().getPath() + "/awse");
        f.createDirectory();
        Log.v(TAG,"Trying to mount bootloader partition...");
        if(mRoot.filesystem("/dev/block/nanda").addMount("/mnt/awse","vfat",new String[] {"ro"}))
            Log.v(TAG, "Successfully mounted!");
        else if(mRoot.filesystem("/mnt/awse").isMounted())
            Log.v(TAG, "Device is already mounted!");
        else
        {
            Log.e(TAG,"Failed to mount bootloader partition...");
            return isScriptDumpAvailable ? getScriptAlternative() : UI_DIALOG_FAILED_MOUNT_LOADER;
        }

        FileExtender.File fBin = mRoot.file("/mnt/awse/script.bin");

        if(!fBin.exists())
        {
            if(!isScriptDumpAvailable) {
                mRoot.shell().run("rmdir /mnt/awse");
                return UI_DIALOG_FAILED_SCRIPT_NOT_FOUND;
            }
            Log.v(TAG, "Script.bin doesn't exist. Trying alternative method...");

            return getScriptAlternative();
        }

            if (fBin.copy(Environment.getExternalStorageDirectory().getPath() + "/awse/script.bin", true)) {
                Log.i(TAG, "Successfully copied script to sdcard");
                Helpers.unmountLoader(mRoot);
            } else {
                Log.e(TAG, "Failed to copy script to sdcard!");
                return UI_DIALOG_FAILED_TO_BACKUP_SCRIPT;
            }
            java.io.File binCopy = new java.io.File(Environment.getExternalStorageDirectory().getPath() + "/awse/script.bin");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(binCopy);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "WTF. Script not found!");
                return UI_DIALOG_FAILED_TO_BACKUP_SCRIPT;
            }

            byte[] aBinData = new byte[(int) binCopy.length()];
            DataInputStream dis = new DataInputStream(fis);
            try {
                dis.readFully(aBinData);
            } catch (IOException e) {
                Log.e(TAG, "Cannot read script file!");
                return UI_DIALOG_FAILED_BAD_SCRIPT;
            }

            if (aBinData.length == 0) {
                Log.e(TAG, "Script file is empty!");
                return UI_DIALOG_FAILED_BAD_SCRIPT;
            }

            try {
                byte[] aFexData = FexUtils.decompileBin(aBinData, aBinData.length);

                if (aFexData == null) {
                    Log.e(TAG, "WTF.Script decompilation error");
                    return UI_DIALOG_FAILED_BAD_SCRIPT;
                }

                ByteArrayInputStream is = new ByteArrayInputStream(aFexData);

                mScript = new Ini();
                try {
                    mScript.load(is);
                } catch (IOException e) {
                    Log.e(TAG, "Cannot parse decompiled script!");
                    return UI_DIALOG_FAILED_BAD_SCRIPT;
                }
            }
            catch (UnsatisfiedLinkError e)
            {
                return UI_DIALOG_FAILED_NO_LIBRARY;
            }
            return UI_DIALOG_INIT_OK;
    }
}
