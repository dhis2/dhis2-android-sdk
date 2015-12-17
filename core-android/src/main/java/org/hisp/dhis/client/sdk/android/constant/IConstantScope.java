package org.hisp.dhis.client.sdk.android.constant;


import org.hisp.dhis.client.sdk.models.constant.Constant;

import java.util.List;

import rx.Observable;

public interface IConstantScope {

    Observable<Constant> get(String uid);

    Observable<Constant> get(long id);

    Observable<List<Constant>> list();

    Observable<Boolean> save(Constant object);

    Observable<Boolean> remove(Constant object);
}
