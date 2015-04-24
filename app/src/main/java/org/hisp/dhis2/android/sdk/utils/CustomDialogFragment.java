/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

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
    int iconId = -1;
    
    public CustomDialogFragment(String title, String message, String positiveButton, OnClickListener positiveButtonListener) {
    	this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.positiveButtonListener = positiveButtonListener;
        this.negativeButton = null;
    }

    public CustomDialogFragment(String title, String message, String positiveButton, int iconId,
                                OnClickListener positiveButtonListener) {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.iconId = iconId;
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

    public CustomDialogFragment(String title, String message, String positiveButton, String negativeButton, int iconId,
                                OnClickListener positiveButtonListener,
                                OnClickListener negativeButtonListener)
    {
        this.title = title;
        this.message = message;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.positiveButtonListener = positiveButtonListener;
        this.negativeButtonListener = negativeButtonListener;
        this.iconId = iconId;
    }
    
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity() );
        if (iconId > 0) {
            alertDialogBuilder.setIcon(iconId);
        }
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
