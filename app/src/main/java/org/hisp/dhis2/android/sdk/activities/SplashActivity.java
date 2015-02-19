package org.hisp.dhis2.android.sdk.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.services.StartPeriodicSynchronizerService;

/**
 * Simple Splash activity that displays the DHIS 2 logo for a given time and initiates the Dhis2Manager.
 */
public class SplashActivity
    extends Activity
{

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_splash );

        new Handler().postDelayed( new Runnable()
        {
            @Override
            public void run()
            {
                Class<? extends Activity> nextActivity = LoginActivity.class;
                //if (Dhis2Manager.getInstance().getRecordManager().getLoggedIn())
                //	nextActivity = PinActivity.class;

                Intent i = new Intent( SplashActivity.this, nextActivity );
                startActivity( i );
                finish();
            }
        }, 3000 );
        startService(new Intent(this, StartPeriodicSynchronizerService.class));
    }
}
