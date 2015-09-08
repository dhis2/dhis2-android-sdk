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

package org.hisp.dhis.android.sdk.models.interpretation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.hisp.dhis.android.sdk.models.user.User;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Interpretation extends BaseIdentifiableObject {
    public static final String TYPE_CHART = "chart";
    public static final String TYPE_MAP = "map";
    public static final String TYPE_REPORT_TABLE = "reportTable";
    public static final String TYPE_DATA_SET_REPORT = "dataSetReport";

    @JsonProperty("text")
    private String text;

    @JsonProperty("type")
    private String type;

    @JsonIgnore
    private State state;

    @JsonProperty("user")
    private User user;

    @JsonProperty("chart")
    private InterpretationElement chart;

    @JsonProperty("map")
    private InterpretationElement map;

    @JsonProperty("reportTable")
    private InterpretationElement reportTable;

    @JsonProperty("dataSet")
    private InterpretationElement dataSet;

    @JsonProperty("period")
    private InterpretationElement period;

    @JsonProperty("organisationUnit")
    private InterpretationElement organisationUnit;

    @JsonProperty("comments")
    private List<InterpretationComment> comments;

    public Interpretation() {
        state = State.SYNCED;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public InterpretationElement getChart() {
        return chart;
    }

    public void setChart(InterpretationElement chart) {
        this.chart = chart;
    }

    public InterpretationElement getMap() {
        return map;
    }

    public void setMap(InterpretationElement map) {
        this.map = map;
    }

    public InterpretationElement getReportTable() {
        return reportTable;
    }

    public void setReportTable(InterpretationElement reportTable) {
        this.reportTable = reportTable;
    }

    public InterpretationElement getDataSet() {
        return dataSet;
    }

    public void setDataSet(InterpretationElement dataSet) {
        this.dataSet = dataSet;
    }

    public InterpretationElement getPeriod() {
        return period;
    }

    public void setPeriod(InterpretationElement period) {
        this.period = period;
    }

    public InterpretationElement getOrganisationUnit() {
        return organisationUnit;
    }

    public void setOrganisationUnit(InterpretationElement organisationUnit) {
        this.organisationUnit = organisationUnit;
    }

    public List<InterpretationComment> getComments() {
        return comments;
    }

    public void setComments(List<InterpretationComment> comments) {
        this.comments = comments;
    }
}