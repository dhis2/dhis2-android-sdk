package org.hisp.dhis.android.sdk.dataelement;

import org.hisp.dhis.java.sdk.models.dataelement.DataElement;

import java.util.List;

import rx.Observable;

public interface IDataElementScope {

    Observable<DataElement> get(String uid);

    Observable<DataElement> get(long id);

    Observable<List<DataElement>> list();

    Observable<Boolean> save(DataElement object);

    Observable<Boolean> remove(DataElement object);
}
