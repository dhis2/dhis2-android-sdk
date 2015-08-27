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

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogAdapter.OptionAdapterValue;

import java.util.ArrayList;
import java.util.List;


public class OrgUnitDialogFragment extends AutoCompleteDialogFragment
        implements LoaderManager.LoaderCallbacks<List<OptionAdapterValue>> {
    public static final int ID = 450123;
    private static final int LOADER_ID = 1;

    public static OrgUnitDialogFragment newInstance(OnOptionSelectedListener listener,
                                                    Program.ProgramType... programKinds) {
        OrgUnitDialogFragment fragment = new OrgUnitDialogFragment();
        Bundle args = new Bundle();
        if( programKinds != null ) {
            String[] programKindStrings = new String[programKinds.length];
            for(int i = 0; i<programKinds.length; i++) {
                programKindStrings[i] = programKinds[i].name();
            }
            args.putStringArray(Program$Table.KIND, programKindStrings);
        }
        fragment.setArguments(args);
        fragment.setOnOptionSetListener(listener);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDialogLabel(R.string.dialog_organisation_units);
        setDialogId(ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<List<OptionAdapterValue>> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            String[] kinds = args.getStringArray(Program$Table.KIND);
            Program.ProgramType[] types = null;
            if( kinds != null ) {
                types = new Program.ProgramType[kinds.length];
                for( int i = 0; i<kinds.length; i++ ) {
                    types[i] = Program.ProgramType.valueOf(kinds[i]);
                }
            }
            return new DbLoader<>(
                    getActivity().getBaseContext(), modelsToTrack, new OrgUnitQuery(types)
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<OptionAdapterValue>> loader,
                               List<OptionAdapterValue> data) {
        if (loader.getId() == LOADER_ID) {
            getAdapter().swapData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<OptionAdapterValue>> loader) {
        getAdapter().swapData(null);
    }

    static class OrgUnitQuery implements Query<List<OptionAdapterValue>> {

        private final Program.ProgramType[] kinds;

        public OrgUnitQuery(Program.ProgramType... kinds) {
            this.kinds = kinds;
        }

        @Override
        public List<OptionAdapterValue> query(Context context) {
            List<OrganisationUnit> orgUnits = queryUnits();
            List<OptionAdapterValue> values = new ArrayList<>();
            for (OrganisationUnit orgUnit : orgUnits) {
                if (hasPrograms(orgUnit.getId(), this.kinds)) {
                    values.add(new OptionAdapterValue(orgUnit.getId(), orgUnit.getLabel()));
                }
            }

            return values;
        }

        private List<OrganisationUnit> queryUnits() {
            return MetaDataController
                    .getAssignedOrganisationUnits();
        }

        private boolean hasPrograms(String unitId, Program.ProgramType... kinds) {
            List<Program> programs = MetaDataController
                    .getProgramsForOrganisationUnit(
                            unitId, kinds
                    );
            return (programs != null && !programs.isEmpty());
        }
    }
}