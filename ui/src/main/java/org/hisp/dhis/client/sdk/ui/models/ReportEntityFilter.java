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

package org.hisp.dhis.client.sdk.ui.models;

/**
 * Created by thomaslindsjorn on 19/08/16.
 */
public class ReportEntityFilter implements Comparable<ReportEntityFilter> {

    private String dataElementId;
    private String dataElementLabel;
    private boolean show;

    public ReportEntityFilter(String dataElementId, String dataElementLabel, boolean show) {
        this.dataElementId = dataElementId;
        this.dataElementLabel = dataElementLabel;
        this.show = show;
    }

    public String getDataElementId() {
        return dataElementId;
    }

    public void setDataElementId(String dataElementId) {
        this.dataElementId = dataElementId;
    }

    public String getDataElementLabel() {
        return dataElementLabel;
    }

    public void setDataElementLabel(String dataElementLabel) {
        this.dataElementLabel = dataElementLabel;
    }

    public boolean show() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    /**
     * Sort alphabetically by data element label, but show checked items (enabled filters) first
     */
    @Override
    public int compareTo(ReportEntityFilter another) {
        if (this.show == another.show) {
            return this.dataElementLabel.compareTo(another.dataElementLabel);
        }
        if (this.show) return -1;
        if (another.show) return 1;
        return 0;
    }
}
