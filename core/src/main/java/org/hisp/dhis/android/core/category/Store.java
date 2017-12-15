package org.hisp.dhis.android.core.category;

public interface Store<E> {

    long insert(E element);

    boolean delete(E element);

    boolean update(E oldElement, E newElement);

}
