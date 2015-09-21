/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import org.hisp.dhis.android.sdk.ui.R;
import org.hisp.dhis.android.sdk.ui.views.callbacks.AbsTextWatcher;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static android.text.TextUtils.isEmpty;


public class AbsConfirmUserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String IS_LOADING = "state:isLoading";

    private Toolbar mToolbar;
    private CircularProgressBar mProgressBar;
    private View mViewsContainer;
    private EditText mUsername;
    private EditText mPassword;
    private Button mReLogIn;
    private Button mLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user);

        setSupportActionBar(mToolbar);
        // setTitle(R.string.app_name);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (CircularProgressBar) findViewById(R.id.progress_bar_circular_navy);
        mViewsContainer = findViewById(R.id.re_log_in_views_container);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mReLogIn = (Button) findViewById(R.id.re_log_in_button);
        mLogOut = (Button) findViewById(R.id.delete_and_log_out_button);

        AbsTextWatcher textWatcher = new AbsTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEditTextFields();
            }
        };

        mUsername.addTextChangedListener(textWatcher);
        mPassword.addTextChangedListener(textWatcher);

        hideProgress(false);
        checkEditTextFields();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING, mProgressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING)) {
            showProgress(false);
        } else {
            hideProgress(false);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void checkEditTextFields() {
        mReLogIn.setEnabled(
                !isEmpty(mUsername.getText()) &&
                        !isEmpty(mPassword.getText()));
    }

    protected void showProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
            mViewsContainer.startAnimation(anim);
        }
        mViewsContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
            mViewsContainer.startAnimation(anim);
        }

        mViewsContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.re_log_in_button) {

        }

        if (v.getId() == R.id.delete_and_log_out_button) {

        }
    }
}

