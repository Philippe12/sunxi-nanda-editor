/*
 * Copyright 2014 Bartosz Jankowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.llt.awse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MADialogFragment extends DialogFragment {

    final static int UI_DIALOG_PROCESSING = -1;   // Progress
    final static int UI_DIALOG_ERROR = -2;        // OK button
    final static int UI_DIALOG_YESNO = -3;        // Yes/No button
    final static int UI_DIALOG_CONFIRM = -4;      // OK/Cancel button
    final static int UI_DIALOG_SAVE = -5;         // Save dialog
    final static int UI_DIALOG_ABOUT = -6;        // About popup

    private Object mDialogListener;
    private File mCurrentLocation;
    private Context mContext;
    private ListView mFileListView;
    private Dialog mDialog;

    /*
     *   @param listener is instance of AlertDialog.OnClickListener for all types except UI_DIALOG_SAVE
     *   which uses View.OnClickListener
     */
    public static final MADialogFragment newInstance(final String title, final String message, int type, final Object listener)    {
        MADialogFragment dialFrag = new MADialogFragment();
        dialFrag.replaceDialogData(title, message, type, listener);

        return dialFrag;
    }

    public void replaceDialogData(final String title, final String message, int type, final Object listener)    {
        Bundle args = new Bundle(3);
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("type", type);
        mDialogListener = listener;
        setArguments(args);

        if(type == UI_DIALOG_SAVE)
        {
            mCurrentLocation = new File(title);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        Log.v("MADialogFragment", "OnCreateDialog()");
        int type = getArguments().getInt("type", UI_DIALOG_PROCESSING);
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        if(type == UI_DIALOG_PROCESSING) {
            ProgressDialog dialog = new ProgressDialog(getActivity());
            mContext = dialog.getContext();
            dialog.setCancelable(false);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            mDialog = dialog;
            return mDialog;
        }
        else if(type == UI_DIALOG_SAVE) {

            final Dialog dialog = new Dialog(getActivity(), R.style.AWSEDialog);
            mContext = dialog.getContext();
            dialog.setContentView(R.layout.awse_save_dialog);
            dialog.setTitle(title);

            mDialog = dialog;

            dialog.findViewById(R.id.awse_save_dialog_button_save).setOnClickListener((View.OnClickListener)mDialogListener);
            dialog.findViewById(R.id.awse_save_dialog_button_cancel).setOnClickListener((View.OnClickListener)mDialogListener);
            dialog.findViewById(R.id.awse_save_dialog_button_new_folder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            mFileListView = (ListView) dialog.findViewById(R.id.awse_save_dialog_file_list);

            mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                    // Check if "../" item should be added.
                    ((EditText) dialog.findViewById(R.id.awse_save_dialog_file_name)).setText("");
                    if (id == 0) {
                        final String parentLocation = mCurrentLocation.getParent();
                        if (parentLocation != null) { // text == "../"
                            mCurrentLocation = new File(parentLocation);
                            makeList(mCurrentLocation);
                        } else {
                            onItemSelect(parent, position);
                        }
                    } else {
                        onItemSelect(parent, position);
                    }
                }
            });

            makeList(mCurrentLocation);

            return dialog;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        mContext = dialog.getContext();
        dialog.setTitle(title);
        if(type == UI_DIALOG_ABOUT) {
            TextView msg = new TextView(mContext);
            msg.setPadding(10,5,0,0);
            msg.setMovementMethod(LinkMovementMethod.getInstance());
            msg.setText(Html.fromHtml(message));
            dialog.setView(msg);
        }
        else
            dialog.setMessage(message);
        dialog.setCancelable(false);

        if(mDialogListener == null ) {
            mDialogListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == DialogInterface.BUTTON_POSITIVE)
                    {
                        dismiss();
                    }
                    else
                    {
                        dismiss();
                    }
                }
            };
        }
            switch (type) {
                case UI_DIALOG_YESNO:
                    dialog.setPositiveButton("Yes", (AlertDialog.OnClickListener)mDialogListener);
                    dialog.setNegativeButton("No", (AlertDialog.OnClickListener)mDialogListener);
                    break;
                case UI_DIALOG_CONFIRM:
                    dialog.setPositiveButton("OK", (AlertDialog.OnClickListener)mDialogListener);
                    dialog.setNegativeButton("Cancel", (AlertDialog.OnClickListener)mDialogListener);
                    break;
                case UI_DIALOG_ABOUT:
                case UI_DIALOG_ERROR:
                    dialog.setNeutralButton("OK", (AlertDialog.OnClickListener)mDialogListener);
                    break;
            }
        Dialog d = dialog.create();
        d.setCanceledOnTouchOutside(false);
        mDialog = d;
        return mDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("MADialogFragment", "OnCreateView()");
        Dialog dialog = getDialog();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    /*
        Get current location for DIALOG_SAVE
     */
    public File getCurrentLocation()
    {
        return mCurrentLocation;
    }


    private void onItemSelect(final AdapterView<?> parent, final int position) {
        final String itemText = ((FileData) parent.getItemAtPosition(position)).getFileName();
        final String itemPath = mCurrentLocation.getAbsolutePath() + File.separator + itemText;
        final File itemLocation = new File(itemPath);

        if (!itemLocation.canRead()) {
            Toast.makeText(mContext, "Access denied!!!", Toast.LENGTH_SHORT).show();
        } else if (itemLocation.isDirectory()) {
            mCurrentLocation = itemLocation;
            makeList(mCurrentLocation);
        } else if (itemLocation.isFile()) {
            final EditText fileName = (EditText) mDialog.findViewById(R.id.awse_save_dialog_file_name);
            fileName.setText(itemText);
        }
    }

    private void makeList(final File location) {
        final ArrayList<FileData> fileList = new ArrayList<FileData>();
        final String parentLocation = location.getParent();
        if (parentLocation != null) {
            // First item on the list.
            fileList.add(new FileData("../", FileData.UP_FOLDER));
        }
        File listFiles[] = location.listFiles();
        if (listFiles != null) {
            ArrayList<FileData> fileDataList = new ArrayList<FileData>();
            for (int index = 0; index < listFiles.length; index++) {
                File tempFile = listFiles[index];

                    int type = tempFile.isDirectory() ? FileData.DIRECTORY : FileData.FILE;
                    fileDataList.add(new FileData(listFiles[index].getName(), type));
            }
            fileList.addAll(fileDataList);
            Collections.sort(fileList);
        }
        // Fill the list with the contents of fileList.
        if (mFileListView != null) {
            FileListAdapter adapter = new FileListAdapter(getActivity(), fileList);
            mFileListView.setAdapter(adapter);
        }
    }
}
