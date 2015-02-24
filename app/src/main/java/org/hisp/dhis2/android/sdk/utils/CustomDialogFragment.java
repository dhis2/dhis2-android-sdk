package org.hisp.dhis2.android.sdk.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class CustomDialogFragment
    extends DialogFragment
{
    String title;
    String message;
    String positiveButton;
    String negativeButton;
    OnClickListener positiveButtonListener;
    OnClickListener negativeButtonListener;
    
    public CustomDialogFragment(String title, String message, String positiveButton, OnClickListener positiveButtonListener) {
    	this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.positiveButtonListener = positiveButtonListener;
        this.negativeButton = null;
    }
    
    public CustomDialogFragment(String title, String message, String positiveButton)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = null;
        this.positiveButtonListener = null;
    }
    
    public CustomDialogFragment(String title, String message, String positiveButton, String negativeButton, OnClickListener positiveButtonListener)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.positiveButtonListener = positiveButtonListener;
    }
    
    public CustomDialogFragment(String title, String message, String positiveButton, String negativeButton, OnClickListener positiveButtonListener,
    		OnClickListener negativeButtonListener)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.positiveButtonListener = positiveButtonListener;
        this.negativeButtonListener = negativeButtonListener;
    }
    
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity() );
        alertDialogBuilder.setTitle( title );
        alertDialogBuilder.setMessage( message );
        // null should be your on click listener
        alertDialogBuilder.setPositiveButton( positiveButton, positiveButtonListener );
        if(negativeButtonListener==null)
        	negativeButtonListener = new OnClickListener()
            {

                @Override
                public void onClick( DialogInterface dialog, int which )
                {
                    dialog.dismiss();
                }
            }; 
        if(negativeButton!=null)
        	alertDialogBuilder.setNegativeButton( negativeButton, negativeButtonListener);

        return alertDialogBuilder.create();
    }
}
