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

package org.hisp.dhis2.android.sdk.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.events.MessageEvent;
import org.hisp.dhis2.android.sdk.events.ResponseEvent;
import org.hisp.dhis2.android.sdk.network.managers.Base64Manager;
import org.hisp.dhis2.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.User;
import org.hisp.dhis2.android.sdk.utils.APIException;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class LoginActivity
    extends Activity
    implements OnClickListener, OnItemSelectedListener
{
    /**
     * 
     */
    private final static String CLASS_TAG = "LoginActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText serverEditText;
    private Spinner serverSpinner;
    private Button loginButton;
    private CheckBox showPasswordCheckbox;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_login);
        Dhis2Application.bus.register(this);
        setupUI();
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        setContentView( R.layout.activity_login );
    }

    /**
     * Sets up the initial UI elements
     */
    private void setupUI()
    {
        usernameEditText = (EditText) findViewById( R.id.usernameEditText );
        passwordEditText = (EditText) findViewById( R.id.passwordEditText );
        serverEditText = (EditText) findViewById( R.id.serverEditText );
        serverSpinner = (Spinner) findViewById( R.id.serverSpinner );
        loginButton = (Button) findViewById( R.id.loginButton );
        
        //Setting previous username in username field
        String username = null;
        
        if(username!=null)
        	if(username.length()>0)
        		{
        			usernameEditText.setText(username);
        			passwordEditText.setText("");
        		}

        showPasswordCheckbox = (CheckBox) findViewById( R.id.showPasswordCheckbox );

        showPasswordCheckbox.setOnCheckedChangeListener( new OnCheckedChangeListener()
        {
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked )
            {
                if ( !isChecked )
                {
                    passwordEditText.setTransformationMethod( PasswordTransformationMethod.getInstance() );
                }
                else
                {
                    passwordEditText.setTransformationMethod( HideReturnsTransformationMethod.getInstance() );
                }
            }
        } );

        serverSpinner.setOnItemSelectedListener( this );
        loginButton.setOnClickListener( this );
    }

    @Override
    public void onClick( View v )
    {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String serverFromSP = serverSpinner.getSelectedItem().toString();
        String serverFromET = serverEditText.getText().toString();

        String serverURL = serverFromSP;
        if ( serverFromET != null && !serverFromET.equals( "" ) )
        {
            serverURL = serverFromET;
        }
        
        //remove whitespace as last character for username
        if(username.charAt(username.length()-1)== ' ')
        	username=username.substring(0, username.length()-1);

        login(serverURL, username, password);
    }
    
    public void login(String serverUrl, String username, String password) {
        NetworkManager.getInstance().setServerUrl(serverUrl);
        NetworkManager.getInstance().setCredentials(NetworkManager.getInstance().getBase64Manager()
                .toBase64(username, password));
        Dhis2.getInstance().saveCredentials(this, serverUrl, username, password);
        Dhis2.getInstance().login(username, password);
    }

    @Subscribe
    public void onReceiveResponse(ResponseEvent event) {
        Log.e(CLASS_TAG, "on Login!");

        if (event.getResponseHolder().getItem() != null) {
            if(event.eventType == ResponseEvent.EventType.onLogin) {
                User user = (User) event.getResponseHolder().getItem();
                Log.e(CLASS_TAG, user.getName());
                user.save(false);
                Dhis2.getInstance().getMetaDataController().loadMetaData(this);
            }
        } else {
            if(event.getResponseHolder()!=null && event.getResponseHolder().getApiException() != null)
                event.getResponseHolder().getApiException().printStackTrace();
        }
    }

    @Subscribe
    public void onReceiveMessage(MessageEvent event) {
        if(event.eventType == ResponseEvent.EventType.onLoadingMetaDataFinished) {
            launchMainActivity();
        }
    }

    public void launchMainActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this, ((Dhis2Application) getApplication()).getMainActivity()));
            }
        });
        Dhis2Application.bus.unregister(this);
        this.finish();
    }
    
    @Override
    public void onItemSelected( AdapterView<?> parent, View view, int position, long id )
    {
        if ( serverSpinner.getSelectedItem().toString().equals( getString( R.string.custom_url ) ) )
        {
            serverEditText.setVisibility( View.VISIBLE );
        }
        else
        {
            serverEditText.setVisibility( View.INVISIBLE );
            serverEditText.setText( "" );
        }
    }

    @Override
    public void onNothingSelected( AdapterView<?> parent )
    {
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )
    {
        if ( (keyCode == KeyEvent.KEYCODE_BACK) )
        {
            finish();
            System.exit( 0 );
        }
        return super.onKeyDown( keyCode, event );
    }
}
