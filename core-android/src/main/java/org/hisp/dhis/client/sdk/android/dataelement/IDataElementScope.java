package org.hisp.dhis.client.sdk.android.dataelement;


import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

import java.util.List;

import rx.Observable;

public interface IDataElementScope {

    Observable<DataElement> get(String uid);

    Observable<DataElement> get(long id);

    Observable<List<DataElement>> list();

    Observable<Boolean> save(DataElement object);

    Observable<Boolean> remove(DataElement object);
}
