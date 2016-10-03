/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.ui.bindings.presenters;

import org.hisp.dhis.client.sdk.core.UserInteractor;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.ui.bindings.commons.SyncDateWrapper;
import org.hisp.dhis.client.sdk.ui.bindings.views.HomeView;
import org.hisp.dhis.client.sdk.ui.bindings.views.View;
import org.hisp.dhis.client.sdk.utils.Logger;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;
import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

public class HomePresenterImpl implements HomePresenter {
    private final UserInteractor userAccountInteractor;
    private final SyncDateWrapper syncDateWrapper;
    private final Logger logger;

    private Subscription subscription;
    private HomeView homeView;

    public HomePresenterImpl(UserInteractor userAccountInteractor,
                             SyncDateWrapper syncDateWrapper,
                             Logger logger) {
//        this.userAccountInteractor = isNull(userAccountInteractor,
//                "UserAccountInteractor must not be null");
        this.userAccountInteractor = userAccountInteractor;
        this.syncDateWrapper = syncDateWrapper;
        this.logger = isNull(logger, "Logger must not be null");
    }

    @Override
    public void attachView(View view) {
        isNull(view, "HomeView must not be null");
        homeView = (HomeView) view;

        subscription = userAccountInteractor.store().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        String name = "";
                        if (!isEmpty(user.getFirstName()) &&
                                !isEmpty(user.getSurname())) {
                            name = String.valueOf(user.getFirstName().charAt(0)) +
                                    String.valueOf(user.getSurname().charAt(0));
                        } else if (user.getDisplayName() != null &&
                                user.getDisplayName().length() > 1) {
                            name = String.valueOf(user.getDisplayName().charAt(0)) +
                                    String.valueOf(user.getDisplayName().charAt(1));
                        }

                        homeView.setUsername(user.getDisplayName());
                        homeView.setUserInfo(user.getEmail());
                        homeView.setUserLetter(name);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        logger.e(HomePresenterImpl.class.getSimpleName(),
                                "Something went wrong", throwable);
                    }
                });
    }

    @Override
    public void detachView() {
        homeView = null;

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void calculateLastSyncedPeriod() {
        String lastSynced = syncDateWrapper.getLastSyncedString();
        homeView.showLastSyncedMessage(lastSynced);
    }
}
