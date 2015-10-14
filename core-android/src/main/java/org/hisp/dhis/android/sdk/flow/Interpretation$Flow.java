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

package org.hisp.dhis.android.sdk.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.common.meta.DbDhis;
import org.hisp.dhis.java.sdk.models.interpretation.Interpretation;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Interpretation$Flow extends BaseIdentifiableObject$Flow {

    @Column(name = "text")
    String text;

    @Column(name = "type")
    String type;

    @Column(name = "action")
    @NotNull
    org.hisp.dhis.java.sdk.models.common.state.Action action;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = "user", columnType = long.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    User$Flow user;

    public Interpretation$Flow() {
        action = org.hisp.dhis.java.sdk.models.common.state.Action.SYNCED;
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

    public org.hisp.dhis.java.sdk.models.common.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.java.sdk.models.common.state.Action action) {
        this.action = action;
    }

    public User$Flow getUser() {
        return user;
    }

    public void setUser(User$Flow user) {
        this.user = user;
    }

    public static Interpretation toModel(Interpretation$Flow interpretationFlow) {
        if (interpretationFlow == null) {
            return null;
        }

        Interpretation interpretation = new Interpretation();
        interpretation.setId(interpretationFlow.getId());
        interpretation.setUId(interpretationFlow.getUId());
        interpretation.setCreated(interpretationFlow.getCreated());
        interpretation.setLastUpdated(interpretationFlow.getLastUpdated());
        interpretation.setName(interpretationFlow.getName());
        interpretation.setDisplayName(interpretationFlow.getDisplayName());
        interpretation.setAccess(interpretationFlow.getAccess());
        interpretation.setText(interpretationFlow.getText());
        interpretation.setType(interpretationFlow.getType());
        // interpretation.setAction(interpretationFlow.getAction());
        interpretation.setUser(User$Flow.toModel(interpretationFlow.getUser()));
        return interpretation;
    }

    public static Interpretation$Flow fromModel(Interpretation interpretation) {
        if (interpretation == null) {
            return null;
        }

        Interpretation$Flow interpretationFlow = new Interpretation$Flow();
        interpretationFlow.setId(interpretation.getId());
        interpretationFlow.setUId(interpretation.getUId());
        interpretationFlow.setCreated(interpretation.getCreated());
        interpretationFlow.setLastUpdated(interpretation.getLastUpdated());
        interpretationFlow.setName(interpretation.getName());
        interpretationFlow.setDisplayName(interpretation.getDisplayName());
        interpretationFlow.setAccess(interpretation.getAccess());
        interpretationFlow.setText(interpretation.getText());
        interpretationFlow.setType(interpretation.getType());
        // interpretationFlow.setAction(interpretation.getAction());
        interpretationFlow.setUser(User$Flow.fromModel(interpretation.getUser()));
        return interpretationFlow;
    }

    public static List<Interpretation> toModels(List<Interpretation$Flow> interpretationFlows) {
        List<Interpretation> interpretations = new ArrayList<>();

        if (interpretationFlows != null && !interpretationFlows.isEmpty()) {
            for (Interpretation$Flow interpretationFlow : interpretationFlows) {
                interpretations.add(toModel(interpretationFlow));
            }
        }

        return interpretations;
    }

    public static List<Interpretation$Flow> fromModels(List<Interpretation> interpretations) {
        List<Interpretation$Flow> interpretationFlows = new ArrayList<>();

        if (interpretations != null && !interpretations.isEmpty()) {
            for (Interpretation interpretation : interpretations) {
                interpretationFlows.add(fromModel(interpretation));
            }
        }

        return interpretationFlows;
    }
}
