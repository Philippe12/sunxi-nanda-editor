package com.llt.awse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class MADialogFragment extends DialogFragment {

    final static int UI_DIALOG_PROCESSING = -1;   // Progress
    final static int UI_DIALOG_ERROR = -2;        // OK button
    final static int UI_DIALOG_YESNO = -3;        // Yes/No button
    final static int UI_DIALOG_CONFIRM = -4;      // OK/Cancel button

    private DialogInterface.OnClickListener mDialogListener;

    public static final MADialogFragment newInstance(final String title, final String message, int type, final DialogInterface.OnClickListener listener) {
        MADialogFragment dialFrag = new MADialogFragment();
        dialFrag.replaceDialogData(title,message, type, listener);

        return dialFrag;
    }

    public void replaceDialogData(final String title, final String message, int type, final DialogInterface.OnClickListener listener)
    {
        Bundle args = new Bundle(3);
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("type", type);
        mDialogListener = listener;
        setArguments(args);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        int type = getArguments().getInt("type", UI_DIALOG_PROCESSING);
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        if(type == UI_DIALOG_PROCESSING) {
            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle(title);
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
                    dialog.setPositiveButton("Yes", mDialogListener);
                    dialog.setNegativeButton("No", mDialogListener);
                    break;
                case UI_DIALOG_CONFIRM:
                    dialog.setPositiveButton("OK", mDialogListener);
                    dialog.setNegativeButton("Cancel", mDialogListener);
                    break;
                case UI_DIALOG_ERROR:
                    dialog.setNeutralButton("OK", mDialogListener);
                    break;
            }
        Dialog d = dialog.create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }
}
