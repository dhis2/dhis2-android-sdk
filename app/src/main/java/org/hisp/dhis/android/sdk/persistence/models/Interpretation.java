/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.meta.State;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Araz Abishov <araz.abishov.gsoc@gmail.com>.
 */
@Table(databaseName = Dhis2Database.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Interpretation extends BaseMetaDataObject {

    public static final String TYPE_CHART = "chart";
    public static final String TYPE_MAP = "map";
    public static final String TYPE_REPORT_TABLE = "reportTable";
    public static final String TYPE_DATA_SET_REPORT = "dataSetReport";

    @JsonProperty("text")
    @Column(name = "text")
    String text;

    @JsonProperty("type")
    @Column(name = "type")
    String type;

    @JsonIgnore
    @Column(name = "state")
    @NotNull
    State state;

    @JsonProperty("user")
    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = "user", columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    User user;

    @JsonProperty("chart")
    InterpretationElement chart;

    @JsonProperty("map")
    InterpretationElement map;

    @JsonProperty("reportTable")
    InterpretationElement reportTable;

    @JsonProperty("dataSet")
    InterpretationElement dataSet;

    @JsonProperty("period")
    InterpretationElement period;

    @JsonProperty("organisationUnit")
    InterpretationElement organisationUnit;

    @JsonProperty("comments")
    List<InterpretationComment> comments;

    public Interpretation() {
        state = State.SYNCED;
    }

    /**
     * Creates comment for given interpretation. Comment is assigned to given user.
     *
     * @param interpretation Interpretation to associate comment with.
     * @param user           User who wants to create comment.
     * @param text           The actual content of comment.
     * @return Intrepretation comment.
     */
    public static InterpretationComment addComment(Interpretation interpretation, User user, String text) {
        //DateTime lastUpdated = DateTimeManager.getInstance()
        //        .getLastUpdated(ResourceType.INTERPRETATIONS);

        InterpretationComment comment = new InterpretationComment();
        //comment.setCreated(lastUpdated);
        //comment.setLastUpdated(lastUpdated);
        comment.setAccess(Access.provideDefaultAccess());
        comment.setText(text);
        comment.setState(State.TO_POST);
        comment.setUser(user);
        comment.setInterpretation(interpretation);
        return comment;
    }

    /**
     * This method allows to create interpretation from: chart, map,
     * reportTable. Please note, it won't work for data sets.
     * <p/>
     * Note, model won't be saved to database automatically. You have to call .save()
     * both on interpretation and interpretation elements of current object.
     *
     * @param item DashboardItem which will represent content of interpretation.
     * @param user User who associated with Interpretation.
     * @param text Interpretation text written by user.
     * @return new Interpretation.
     */
    public static Interpretation createInterpretation(DashboardItem item, User user, String text) {
        //DateTime lastUpdated = DateTimeManager.getInstance()
        //        .getLastUpdated(ResourceType.INTERPRETATIONS);

        Interpretation interpretation = new Interpretation();
        //interpretation.setCreated(lastUpdated);
        //interpretation.setLastUpdated(lastUpdated);
        interpretation.setAccess(Access.provideDefaultAccess());
        interpretation.setText(text);
        interpretation.setState(State.TO_POST);
        interpretation.setUser(user);

        switch (item.getType()) {
            case TYPE_CHART: {
                InterpretationElement element = InterpretationElement
                        .fromDashboardElement(interpretation, item.getChart(), TYPE_CHART);
                interpretation.setType(TYPE_CHART);
                interpretation.setChart(element);
                break;
            }
            case TYPE_MAP: {
                InterpretationElement element = InterpretationElement
                        .fromDashboardElement(interpretation, item.getMap(), TYPE_MAP);
                interpretation.setType(TYPE_MAP);
                interpretation.setMap(element);
                break;
            }
            case TYPE_REPORT_TABLE: {
                InterpretationElement element = InterpretationElement
                        .fromDashboardElement(interpretation, item.getReportTable(), TYPE_REPORT_TABLE);
                interpretation.setType(TYPE_REPORT_TABLE);
                interpretation.setReportTable(element);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported DashboardItem type");
            }
        }

        return interpretation;
    }

    /**
     * Method modifies the original interpretation text and sets TO_UPDATE as state,
     * if the object was received from server.
     * <p/>
     * If the model was persisted only locally, the State will remain TO_POST.
     *
     * @param text Edited text of interpretation.
     */
    public void updateInterpretation(String text) {
        setText(text);

        if (state != State.TO_DELETE && state != State.TO_POST) {
            state = State.TO_UPDATE;
        }

        super.save();
    }

    /**
     * Performs soft delete of model. If State of object was SYNCED, it will be set to TO_DELETE.
     * If the model is persisted only in the local database, it will be removed immediately.
     */
    public final void deleteInterpretation() {
        if (State.TO_POST.equals(getState())) {
            super.delete();
        } else {
            setState(State.TO_DELETE);
            super.save();
        }
    }

    /**
     * Convenience method which allows to set InterpretationElements
     * to Interpretation depending on their mime-type.
     *
     * @param elements List of interpretation elements.
     */
    public void setInterpretationElements(List<InterpretationElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        if (getType() == null) {
            return;
        }

        if (getType().equals(TYPE_DATA_SET_REPORT)) {
            for (InterpretationElement element : elements) {
                switch (element.getType()) {
                    case InterpretationElement.TYPE_DATA_SET: {
                        setDataSet(element);
                        break;
                    }
                    case InterpretationElement.TYPE_PERIOD: {
                        setPeriod(element);
                        break;
                    }
                    case InterpretationElement.TYPE_ORGANISATION_UNIT: {
                        setOrganisationUnit(element);
                        break;
                    }
                }
            }
        } else {
            switch (getType()) {
                case InterpretationElement.TYPE_CHART: {
                    setChart(elements.get(0));
                    break;
                }
                case InterpretationElement.TYPE_MAP: {
                    setMap(elements.get(0));
                    break;
                }
                case InterpretationElement.TYPE_REPORT_TABLE: {
                    setReportTable(elements.get(0));
                    break;
                }
            }
        }
    }

    /**
     * Convenience method which allows to get
     * interpretation elements assigned to current object.
     *
     * @return List of interpretation elements.
     */
    public List<InterpretationElement> getInterpretationElements() {
        List<InterpretationElement> elements = new ArrayList<>();

        switch (getType()) {
            case Interpretation.TYPE_CHART: {
                elements.add(getChart());
                break;
            }
            case Interpretation.TYPE_MAP: {
                elements.add(getMap());
                break;
            }
            case Interpretation.TYPE_REPORT_TABLE: {
                elements.add(getReportTable());
                break;
            }
            case Interpretation.TYPE_DATA_SET_REPORT: {
                elements.add(getDataSet());
                elements.add(getPeriod());
                elements.add(getOrganisationUnit());
                break;
            }
        }

        return elements;
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
