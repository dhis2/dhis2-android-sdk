package org.hisp.dhis.android.sdk.models.common;

import java.util.List;

/**
 * Created by arazabishov on 8/18/15.
 */
public interface IStore<T extends IdentifiableObject> {

    void insert(T object);

    void update(T object);

    void save(T object);

    void delete(T object);

    List<T> query();

    T query(long id);

    T query(String uid);
}
