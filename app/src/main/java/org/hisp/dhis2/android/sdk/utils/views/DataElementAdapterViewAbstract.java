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
package org.hisp.dhis2.android.sdk.utils.views;

import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;

import android.content.Context;
import android.view.View;

/**
 * @author Long Ngo Thanh
 *
 */
public abstract class DataElementAdapterViewAbstract
{
    private Context context;
    public DataValue dataValue;

    /**
     * The data element
     */
    protected DataElement dataElement;

    /**
     * Whether or not to show a red star next to name
     */
    protected boolean compulsory;

    /**
     *
     * @param context
     * @param dataElement
     * @param dataValue
     * @param compulsory set to true to show a red star next to the name.
     */
    public DataElementAdapterViewAbstract( Context context, DataElement dataElement,
                                           DataValue dataValue, boolean compulsory )
    {
        this.context = context;
        this.dataElement = dataElement;
        this.compulsory = compulsory;
        this.dataValue = dataValue;
    }

    /**
     * @return the context
     */

    public abstract View getView();

    public Context getContext()
    {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext( Context context )
    {
        this.context = context;
    }

    /**
     * returns the data value of this given data element view
     * @return
     */
    public DataValue getDataValue() {
        return dataValue;
    }

}
