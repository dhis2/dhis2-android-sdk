package org.hisp.dhis.android.sdk.constant;


import org.hisp.dhis.java.sdk.models.constant.Constant;

import java.util.List;

import rx.Observable;

public interface IConstantScope {

    Observable<Constant> get(String uid);

    Observable<Constant> get(long id);
    
    Observable<List<Constant>> list();

    Observable<Boolean> save(Constant object);

    Observable<Boolean> remove(Constant object);
}
