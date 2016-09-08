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

package org.hisp.dhis.client.sdk.android.enrollment;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.apache.commons.lang3.NotImplementedException;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EnrollmentFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EnrollmentFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectDataStore;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperationImpl;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class EnrollmentStoreImpl extends AbsIdentifiableObjectDataStore<Enrollment, EnrollmentFlow> implements
        EnrollmentStore {

    public EnrollmentStoreImpl(StateStore stateStore) {
        super(EnrollmentFlow.MAPPER, stateStore);
    }

    @Override
    public boolean insert(Enrollment enrollment) {
        boolean isSuccess = super.insert(enrollment);

//        if (isSuccess) {
//            saveEnrollmentTrackedEntityAttributeValues(enrollment);
//        }

        return isSuccess;
    }

    @Override
    public boolean update(Enrollment enrollment) {
        boolean isSuccess = super.update(enrollment);

//        if (isSuccess) {
//            saveEnrollmentTrackedEntityAttributeValues(enrollment);
//        }

        return isSuccess;
    }

    @Override
    public boolean save(Enrollment enrollment) {
        boolean isSuccess = super.save(enrollment);

//        if (isSuccess) {
//            saveEnrollmentTrackedEntityAttributeValues(enrollment);
//        }

        return isSuccess;
    }

    @Override
    public Enrollment queryById(long id) {
        Enrollment enrollment = super.queryById(id);

//        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
//                trackedEntityAttributeValueStore.query(enrollment);
//        if (enrollment != null) {
//            enrollment.setTrackedEntityAttributeValues(trackedEntityAttributeValues);
//        }

        return enrollment;
    }

    @Override
    public Enrollment queryByUid(String uid) {
        Enrollment enrollment = super.queryByUid(uid);

//        List<TrackedEntityAttributeValue> dataValues = trackedEntityAttributeValueStore.query(enrollment);
//        if (enrollment != null) {
//            enrollment.setTrackedEntityAttributeValues(dataValues);
//        }

        return enrollment;
    }

    @Override
    public List<Enrollment> queryByUids(Set<String> uids) {
        List<Enrollment> enrollments = super.queryByUids(uids);
//        return mapEventsToDataValues(enrollments, trackedEntityAttributeValueStore
//                .query(enrollments));
        return enrollments;
    }

    @Override
    public List<Enrollment> queryAll() {
//        return mapEventsToDataValues(super.queryAll(),
//                trackedEntityAttributeValueStore.queryAll());
        return super.queryAll();
    }


    @Override
    public List<Enrollment> query(Program program, TrackedEntityInstance instance) {
        return null;
    }

    @Override
    public Enrollment queryActiveEnrollment(OrganisationUnit unit, Program program, TrackedEntityInstance instance) {
        return null;
    }

    @Override
    public List<Enrollment> query(TrackedEntityInstance instance) {
        return null;
    }

    @Override
    public List<Enrollment> query(OrganisationUnit organisationUnit, Program program) {
//        isNull(organisationUnit, "OrganisationUnit must not be null");
//        isNull(program, "Program must not be null");
//
//        List<EnrollmentFlow> enrollmentFlows = new Select()
//                .from(EnrollmentFlow.class)
//                .where(EnrollmentFlow_Table
//                        .orgUnit.is(organisationUnit.getUId()))
//                .and(EnrollmentFlow_Table
//                        .program.is((program.getUId())))
//                .queryList();
//
//        List<Enrollment> enrollments = getMapper().mapToModels(enrollmentFlows);
//        return mapEventsToDataValues(enrollments, dataValueStore.query(enrollments));
        throw new NotImplementedException("Not implemented yet");
    }

//    private void saveEnrollmentTrackedEntityAttributeValues(Enrollment enrollment) {
//        List<TrackedEntityAttributeValue> dataValues = enrollment.getTrackedEntityAttributeValues();
//        List<TrackedEntityAttributeValue> persistedAttributeValues = dataValueStore.query(enrollment);
//
//        Map<String, TrackedEntityDataValue> updatedDataValuesMap = toMap(dataValues);
//        Map<String, TrackedEntityDataValue> persistedDataValueMap = toMap(persistedDataValues);
//
//        List<DbOperation> dbOperations = new ArrayList<>();
//        for (String dataElementUid : updatedDataValuesMap.keySet()) {
//            TrackedEntityDataValue updatedDataValue =
//                    updatedDataValuesMap.get(dataElementUid);
//            TrackedEntityDataValue persistedDataValue =
//                    persistedDataValueMap.get(dataElementUid);
//
//            if (persistedDataValue == null) {
//                dbOperations.add(DbOperationImpl.with(dataValueStore)
//                        .insert(updatedDataValue));
//                continue;
//            }
//
//            dbOperations.add(DbOperationImpl.with(dataValueStore)
//                    .update(updatedDataValue));
//            persistedDataValueMap.remove(dataElementUid);
//        }
//
//        for (String dataElementUid : persistedDataValueMap.keySet()) {
//            TrackedEntityDataValue dataValue =
//                    persistedDataValueMap.get(dataElementUid);
//            dbOperations.add(DbOperationImpl.with(dataValueStore).delete(dataValue));
//        }
//
//        transactionManager.transact(dbOperations);
//    }
//
//    private static List<Enrollment> mapEnrollmentsToAttributeValues(
//            List<Enrollment> enrollments, List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
//        if (enrollments == null || enrollments.isEmpty() ||
//                trackedEntityAttributeValues == null || trackedEntityAttributeValues.isEmpty()) {
//            return enrollments;
//        }
//
//        Map<String, Enrollment> enrollmentMap = ModelUtils.toMap(enrollments);
//        for (TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues) {
//            if (trackedEntityAttributeValue.getEvent() == null ||
//                    enrollmentMap.get(trackedEntityAttributeValue.getEvent().getUId()) == null) {
//                continue;
//            }
//
//            Enrollment enrollment = enrollmentMap.get(trackedEntityAttributeValue.getEvent().getUId());
//            if (enrollment.getTrackedEntityAttributeValues() == null) {
//                enrollment.setTrackedEntityAttributeValues(new ArrayList<TrackedEntityAttributeValue>());
//            }
//
//            enrollment.getTrackedEntityAttributeValues().add(trackedEntityAttributeValue);
//        }
//
//        return enrollments;
//    }
//
//    private static Map<String, TrackedEntityDataValue> toMap(
//            Collection<TrackedEntityDataValue> dataValueCollection) {
//
//        Map<String, TrackedEntityDataValue> dataValueMap = new HashMap<>();
//        if (dataValueCollection != null && !dataValueCollection.isEmpty()) {
//            for (TrackedEntityDataValue dataValue : dataValueCollection) {
//                dataValueMap.put(dataValue.getDataElement(), dataValue);
//            }
//        }
//
//        return dataValueMap;
//    }
}
