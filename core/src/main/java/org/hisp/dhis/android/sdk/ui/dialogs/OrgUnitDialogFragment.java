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

package org.hisp.dhis.android.sdk.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.loaders.DbLoader;
import org.hisp.dhis.android.sdk.persistence.loaders.Query;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogAdapter.OptionAdapterValue;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OrgUnitDialogFragment extends AutoCompleteDialogFragment
        implements LoaderManager.LoaderCallbacks<OrgUnitDialogFragmentForm> {
    public static final int ID = 450123;
    private static final int LOADER_ID = 1;
    private static final String PROGRAMTYPE = "programType";

    public static OrgUnitDialogFragment newInstance(OnOptionSelectedListener listener,
                                                    ProgramType... programKinds) {
        OrgUnitDialogFragment fragment = new OrgUnitDialogFragment();
        Bundle args = new Bundle();
        if (programKinds != null) {
            String[] programKindStrings = new String[programKinds.length];
            for (int i = 0; i < programKinds.length; i++) {
                programKindStrings[i] = programKinds[i].name();
            }
            args.putStringArray(PROGRAMTYPE, programKindStrings);
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
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public Loader<OrgUnitDialogFragmentForm> onCreateLoader(int id, Bundle args) {
        if (LOADER_ID == id && isAdded()) {
            List<Class<? extends Model>> modelsToTrack = new ArrayList<>();
            modelsToTrack.add(OrganisationUnitProgramRelationship.class);
            modelsToTrack.add(OrganisationUnit.class);
            modelsToTrack.add(Program.class);

            String[] kinds = args.getStringArray(PROGRAMTYPE);
            ProgramType[] types = null;
            if (kinds != null) {
                types = new ProgramType[kinds.length];
                for (int i = 0; i < kinds.length; i++) {
                    types[i] = ProgramType.valueOf(kinds[i]);
                }
            }
            return new DbLoader<>(
                    getActivity().getBaseContext(),
                    modelsToTrack,
                    new OrgUnitQuery(types)
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<OrgUnitDialogFragmentForm> loader,
                               OrgUnitDialogFragmentForm data) {
        if (loader.getId() == LOADER_ID) {
            getAdapter().swapData(data.getOptionAdapterValueList());

            if (MetaDataController.isDataLoaded(getActivity())) {
                mProgressBar.setVisibility(View.GONE);

                if(data.getType().equals(OrgUnitDialogFragmentForm.Error.NO_ASSIGNED_ORGANISATION_UNITS)) {
                    this.setNoItemsTextViewVisibility(View.VISIBLE);
                    this.setTextToNoItemsTextView(getString(R.string.no_organisation_units));
                }
                else if(data.getType().equals(OrgUnitDialogFragmentForm.Error.NO_PROGRAMS_TO_ORGANSATION_UNIT)) {
                    this.setNoItemsTextViewVisibility(View.VISIBLE);
                    this.setTextToNoItemsTextView(getString(R.string.no_programs));
                }
                else {
                    this.setNoItemsTextViewVisibility(View.GONE);
                    this.setTextToNoItemsTextView("");
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<OrgUnitDialogFragmentForm> loader) {
        getAdapter().swapData(null);
    }

    static class OrgUnitQuery implements Query<OrgUnitDialogFragmentForm> {

        private final ProgramType[] kinds;

        public OrgUnitQuery(ProgramType... kinds) {
            this.kinds = kinds;
        }

        @Override
        public OrgUnitDialogFragmentForm query(Context context) {
            OrgUnitDialogFragmentForm mForm = new OrgUnitDialogFragmentForm();

            List<OrganisationUnit> orgUnits = queryUnits();
            List<OptionAdapterValue> values = new ArrayList<>();
            if(orgUnits.isEmpty()) {
                mForm.setType(OrgUnitDialogFragmentForm.Error.NO_ASSIGNED_ORGANISATION_UNITS);
            }
            else {
                for (OrganisationUnit orgUnit : orgUnits) {
                    if (hasPrograms(orgUnit.getId(), this.kinds)) {
                        values.add(new OptionAdapterValue(orgUnit.getId(), orgUnit.getLabel()));
                    } else {
                        mForm.setType(OrgUnitDialogFragmentForm.Error.NO_PROGRAMS_TO_ORGANSATION_UNIT);
                    }
                }
            }

            if(!values.isEmpty()) {
                Collections.sort(values);
                mForm.setType(OrgUnitDialogFragmentForm.Error.NONE); // if has values, no errors
            }

            mForm.setOrganisationUnits(orgUnits);
            mForm.setOptionAdapterValueList(values);
            return mForm;
        }

        private List<OrganisationUnit> queryUnits() {
            return MetaDataController
                    .getAssignedOrganisationUnits();
        }

        private boolean hasPrograms(String unitId, ProgramType... kinds) {
            List<Program> programs = MetaDataController
                    .getProgramsForOrganisationUnit(
                            unitId, kinds
                    );
            return (programs != null && !programs.isEmpty());
        }
    }
}