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

package org.hisp.dhis.client.sdk.core.interpretation;

import org.hisp.dhis.client.sdk.core.common.controllers.IdentifiableController;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.network.Response;
import org.hisp.dhis.client.sdk.core.common.persistence.DbOperationImpl;
import org.hisp.dhis.client.sdk.core.common.persistence.IdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.utils.ModelUtils;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;
import org.hisp.dhis.client.sdk.core.user.UserStore;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class InterpretationControllerImpl implements IdentifiableController<Interpretation> {
    private final InterpretationService mInterpretationService;
    private final UserAccountService mUserAccountService;

    private final IdentifiableObjectStore<Interpretation> mInterpretationStore;
    private final InterpretationElementStore mInterpretationElementStore;
    private final InterpretationCommentStore mInterpretationCommentStore;

    private final UserStore mUserStore;

    public InterpretationControllerImpl(InterpretationService interpretationsService,
                                        UserAccountService userAccountService,
                                        IdentifiableObjectStore<Interpretation> mInterpretationStore,
                                        InterpretationElementStore mInterpretationElementStore,
                                        InterpretationCommentStore mInterpretationCommentStore,
                                        UserStore mUserStore) {
        this.mInterpretationService = interpretationsService;
        this.mUserAccountService = userAccountService;
        this.mInterpretationStore = mInterpretationStore;
        this.mInterpretationElementStore = mInterpretationElementStore;
        this.mInterpretationCommentStore = mInterpretationCommentStore;
        this.mUserStore = mUserStore;
    }

    private void sendLocalChanges() throws ApiException {
        sendInterpretationChanges();
        sendInterpretationCommentChanges();
    }

    private void sendInterpretationChanges() throws ApiException {
        List<Interpretation> interpretations = null;
        // mInterpretationStore.filter(Action.SYNCED);

        if (interpretations == null || interpretations.isEmpty()) {
            return;
        }

        for (Interpretation interpretation : interpretations) {
            /* List<InterpretationElement> elements =
                    mInterpretationElementStore.list(interpretation);
            mInterpretationService.setInterpretationElements(interpretation, elements); */
        }

        /* for (Interpretation interpretation : interpretations) {
            switch (interpretation.getAction()) {
                case TO_POST: {
                    postInterpretation(interpretation);
                    break;
                }
                case TO_UPDATE: {
                    putInterpretation(interpretation);
                    break;
                }
                case TO_DELETE: {
                    deleteInterpretation(interpretation);
                    break;
                }
            }
        } */
    }

    public void postInterpretation(Interpretation interpretation) throws ApiException {
        try {
            Response response;

            switch (interpretation.getType()) {
                /* case Interpretation.TYPE_CHART: {
                    response = mDhisApi.postChartInterpretation(
                            interpretation.getChart().getUId(), new TypedString(interpretation
                            .getText()));
                    break;
                }
                case Interpretation.TYPE_MAP: {
                    response = mDhisApi.postMapInterpretation(
                            interpretation.getMap().getUId(), new TypedString(interpretation
                            .getText()));
                    break;
                }
                case Interpretation.TYPE_REPORT_TABLE: {
                    response = mDhisApi.postReportTableInterpretation(
                            interpretation.getReportTable().getUId(), new TypedString
                            (interpretation.getText()));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported interpretation type");
                */
            }

            /* Header header = NetworkUtils.findLocationHeader(response.getHeaders());
            String interpretationUid = Uri.parse(header
                    .getValue()).getLastPathSegment(); */
            //  interpretation.setUId(interpretationUid);
            // interpretation.setAction(Action.SYNCED);

            mInterpretationStore.save(interpretation);

            updateInterpretationTimeStamp(interpretation);

        } catch (ApiException apiException) {
            // ApiExceptionHandler.handleApiException(apiException, interpretation,
            // mInterpretationStore);
        }
    }

    public void putInterpretation(Interpretation interpretation) {
        /* try {
            mDhisApi.putInterpretationText(interpretation.getUId(),
                    new TypedString(interpretation.getText()));
            // interpretation.setAction(Action.SYNCED);

            mInterpretationStore.save(interpretation);

            updateInterpretationTimeStamp(interpretation);
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException, interpretation, mInterpretationStore);
        } */
    }

    public void deleteInterpretation(Interpretation interpretation) {
        /* try {
            mDhisApi.deleteInterpretation(interpretation.getUId());

            mInterpretationStore.delete(interpretation);
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException, interpretation, mInterpretationStore);
        } */
    }

    private void sendInterpretationCommentChanges() {
        List<InterpretationComment> comments = null;
        // mInterpretationCommentStore.query(Action.SYNCED);

        if (comments == null || comments.isEmpty()) {
            return;
        }

        for (InterpretationComment comment : comments) {
            /* switch (comment.getAction()) {
                case TO_POST: {
                    postInterpretationComment(comment);
                    break;
                }
                case TO_UPDATE: {
                    putInterpretationComment(comment);
                    break;
                }
                case TO_DELETE: {
                    deleteInterpretationComment(comment);
                    break;
                }
            } */
        }
    }

    public void postInterpretationComment(InterpretationComment comment) {
        Interpretation interpretation = comment.getInterpretation();

        /* if (interpretation != null && interpretation.getAction() != null) {
            boolean isInterpretationSynced = (interpretation.getAction().equals(Action.SYNCED) ||
                    interpretation.getAction().equals(Action.TO_UPDATE));

            if (!isInterpretationSynced) {
                return;
            }

            try {
                Response response = mDhisApi.postInterpretationComment(
                        interpretation.getUId(), new TypedString(comment.getText()));

                Header locationHeader = findLocationHeader(response.getHeaders());
                String commentUid = Uri.parse(locationHeader
                        .getValue()).getLastPathSegment();
                comment.setUId(commentUid);
                comment.setAction(Action.SYNCED);

                mInterpretationStore.save(interpretation);

                updateInterpretationCommentTimeStamp(comment);
            } catch (APIException apiException) {
                handleApiException(apiException, comment, mInterpretationCommentStore);
            }
        } */
    }

    public void putInterpretationComment(InterpretationComment comment) {
        Interpretation interpretation = comment.getInterpretation();

        /* if (interpretation != null && interpretation.getAction() != null) {
            boolean isInterpretationSynced = (interpretation.getAction().equals(Action.SYNCED) ||
                    interpretation.getAction().equals(Action.TO_UPDATE));

            if (!isInterpretationSynced) {
                return;
            }

            try {
                mDhisApi.putInterpretationComment(interpretation.getUId(),
                        comment.getUId(), new TypedString(comment.getText()));

                comment.setAction(Action.SYNCED);

                mInterpretationCommentStore.save(comment);

                updateInterpretationTimeStamp(comment.getInterpretation());
            } catch (APIException apiException) {
                handleApiException(apiException);
            }
        } */
    }

    public void deleteInterpretationComment(InterpretationComment comment) {
        Interpretation interpretation = comment.getInterpretation();

        /* if (interpretation != null && interpretation.getAction() != null) {
            boolean isInterpretationSynced = (interpretation.getAction().equals(Action.SYNCED) ||
                    interpretation.getAction().equals(Action.TO_UPDATE));

            // 1) If Action of Interpretation is TO_DELETE,
            //    there is no meaning to remove its comments by hand.
            //    They will be removed automatically when interpretation is removed.
            // 2) If Action of Interpretation is TO_POST,
            //    we cannot create comment on server, since we don't have
            //    interpretation UUID to associate comment with.
            // In all other Action cases (TO_UPDATE, SYNCED), we can delete comments
            if (!isInterpretationSynced) {
                return;
            }

            try {
                mDhisApi.deleteInterpretationComment(
                        interpretation.getUId(), comment.getUId());

                mInterpretationCommentStore.delete(comment);

                updateInterpretationTimeStamp(comment.getInterpretation());
            } catch (APIException apiException) {
                handleApiException(apiException, comment, mInterpretationCommentStore);
            }
        } */
    }

    /**
     * This method gets only time stamp from server
     * for given interpretation and updates it locally.
     *
     * @param interpretation Interpretation to update.
     */
    private void updateInterpretationTimeStamp(Interpretation interpretation) {
        /* try {
            final Map<String, String> QUERY_PARAMS = new HashMap<>();
            QUERY_PARAMS.put("fields", "[created,lastUpdated]");

            Interpretation updatedInterpretation = mDhisApi
                    .getInterpretation(interpretation.getUId(), QUERY_PARAMS);

            // merging updated timestamp to local interpretation model
            interpretation.setCreated(updatedInterpretation.getCreated());
            interpretation.setLastUpdated(updatedInterpretation.getLastUpdated());

            mInterpretationStore.save(interpretation);
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException, interpretation, mInterpretationStore);
        } */
    }

    private void updateInterpretationCommentTimeStamp(InterpretationComment comment) {
        /* try {
            // after posting comment, timestamp both of interpretation and comment will change.
            // we have to reflect these changes here in order not to break data integrity during
            // next synchronizations to server.
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("fields", "created,lastUpdated,comments[id,created,lastUpdated]");
            Interpretation persistedInterpretation = comment.getInterpretation();
            Interpretation updatedInterpretation = mDhisApi
                    .getInterpretation(persistedInterpretation.getUId(), queryParams);

            // first, update timestamp of interpretation
            persistedInterpretation.setCreated(updatedInterpretation.getCreated());
            persistedInterpretation.setLastUpdated(updatedInterpretation.getLastUpdated());

            mInterpretationStore.save(persistedInterpretation);

            // second, find comment which we have added recently and update its timestamp
            Map<String, InterpretationComment> updatedComments
                    = toMap(updatedInterpretation.getComments());
            if (updatedComments.containsKey(comment.getUId())) {
                InterpretationComment updatedComment = updatedComments.get(comment.getUId());

                // set timestamp here
                comment.setCreated(updatedComment.getCreated());
                comment.setLastUpdated(updatedComment.getLastUpdated());

                mInterpretationCommentStore.save(comment);
            }
        } catch (APIException apiException) {
            NetworkUtils.handleApiException(apiException);
        } */
    }

    private void getInterpretationDataFromServer() throws ApiException {
        /* DateTime lastUpdated = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.INTERPRETATIONS);
        DateTime serverTime = mDhisApi.getSystemInfo().getServerDate();

        List<Interpretation> interpretations = updateInterpretations(lastUpdated);
        List<InterpretationComment> comments = updateInterpretationComments(interpretations);
        List<User> users = updateInterpretationUsers(interpretations, comments);

        Queue<DbOperation> operations = new LinkedList<>();
        operations.addAll(DbUtils.createOperations(
                mUserStore, mUserStore.queryAll(), users));
        /* operations.addAll(createOperations(
                mInterpretationStore.filter(Action.TO_POST), interpretations)); */
        /* operations.addAll(DbUtils.createOperations(
                mInterpretationCommentStore, mInterpretationCommentStore.query
                (Action.TO_POST), comments)); */

        /* DbUtils.applyBatch(operations);
        DateTimeManager.getInstance()
                .setLastUpdated(ResourceType.INTERPRETATIONS, serverTime); */
    }

    private List<Interpretation> updateInterpretations(DateTime lastUpdated) {
        final Map<String, String> QUERY_MAP_BASIC = new HashMap<>();
        final Map<String, String> QUERY_MAP_FULL = new HashMap<>();
        final String BASE = "id,created,lastUpdated,name,displayName,access";

        QUERY_MAP_BASIC.put("fields", "id");
        QUERY_MAP_FULL.put("fields", BASE + ",text,type," +
                "chart" + "[" + BASE + "]," +
                "map" + "[" + BASE + "]," +
                "reportTable" + "[" + BASE + "]," +
                "user" + "[" + BASE + "]," +
                "dataSet" + "[" + BASE + "]," +
                "period" + "[" + BASE + "]," +
                "organisationUnit" + "[" + BASE + "]," +
                "comments" + "[" + BASE + ",user,text" + "]");

        if (lastUpdated != null) {
            QUERY_MAP_FULL.put("filter", "lastUpdated:gt:" + lastUpdated.toString());
        }

        /* List<Interpretation> actualInterpretations = NetworkUtils.unwrapResponse(mDhisApi
                .getInterpretations(QUERY_MAP_BASIC), "interpretations");

        List<Interpretation> updatedInterpretations = NetworkUtils.unwrapResponse(mDhisApi
                .getInterpretations(QUERY_MAP_FULL), "interpretations"); */

        List<Interpretation> updatedInterpretations = new ArrayList<>();

        if (updatedInterpretations != null && !updatedInterpretations.isEmpty()) {

            for (Interpretation interpretation : updatedInterpretations) {

                // build relationship with comments
                if (interpretation.getComments() != null &&
                        !interpretation.getComments().isEmpty()) {

                    for (InterpretationComment comment : interpretation.getComments()) {
                        comment.setInterpretation(interpretation);
                    }
                }

                // we need to set mime type and interpretation to each element
                switch (interpretation.getType()) {
                    case Interpretation.TYPE_CHART: {
                        interpretation.getChart()
                                .setType(InterpretationElement.TYPE_CHART);
                        interpretation.getChart()
                                .setInterpretation(interpretation);
                        break;
                    }
                    case Interpretation.TYPE_MAP: {
                        interpretation.getMap()
                                .setType(InterpretationElement.TYPE_MAP);
                        interpretation.getMap()
                                .setInterpretation(interpretation);
                        break;
                    }
                    case Interpretation.TYPE_REPORT_TABLE: {
                        interpretation.getReportTable()
                                .setType(InterpretationElement.TYPE_REPORT_TABLE);
                        interpretation.getReportTable()
                                .setInterpretation(interpretation);
                        break;
                    }
                    case Interpretation.TYPE_DATA_SET_REPORT: {
                        interpretation.getDataSet()
                                .setType(InterpretationElement.TYPE_DATA_SET);
                        interpretation.getPeriod()
                                .setType(InterpretationElement.TYPE_PERIOD);
                        interpretation.getOrganisationUnit()
                                .setType(InterpretationElement.TYPE_ORGANISATION_UNIT);

                        interpretation.getDataSet().setInterpretation(interpretation);
                        interpretation.getPeriod().setInterpretation(interpretation);
                        interpretation.getOrganisationUnit().setInterpretation(interpretation);
                        break;
                    }
                }
            }
        }

        List<Interpretation> persistedInterpretations = null;
        // mInterpretationStore.filter(Action.TO_POST);
        if (persistedInterpretations != null
                && !persistedInterpretations.isEmpty()) {
            for (Interpretation interpretation : persistedInterpretations) {
                List<InterpretationElement> elements =
                        mInterpretationElementStore.query(interpretation);
                // mInterpretationService.setInterpretationElements(interpretation, elements);

                List<InterpretationComment> comments = null;
                // mInterpretationCommentStore.query(interpretation, Action
                // .TO_POST);
                interpretation.setComments(comments);
            }
        }

        // return merge(actualInterpretations, updatedInterpretations, persistedInterpretations);
        return null;
    }

    private List<InterpretationComment> updateInterpretationComments(List<Interpretation>
                                                                             interpretations) {
        List<InterpretationComment> interpretationComments = new ArrayList<>();

        if (interpretations != null && !interpretations.isEmpty()) {
            for (Interpretation interpretation : interpretations) {
                interpretationComments.addAll(interpretation.getComments());
            }
        }

        return interpretationComments;
    }

    private List<User> updateInterpretationUsers(List<Interpretation> interpretations,
                                                 List<InterpretationComment> comments) {
        Map<String, User> users = new HashMap<>();
        UserAccount currentUserAccount = mUserAccountService.get();
        User currentUser = mUserStore.queryByUid(currentUserAccount.getUId());
        if (currentUser == null) {
            currentUser = mUserAccountService.toUser(currentUserAccount);
        }

        users.put(currentUser.getUId(), currentUser);

        if (interpretations != null && !interpretations.isEmpty()) {
            for (Interpretation interpretation : interpretations) {
                User user = interpretation.getUser();
                if (users.containsKey(user.getUId())) {
                    user = users.get(user.getUId());
                    interpretation.setUser(user);
                } else {
                    users.put(user.getUId(), user);
                }
            }
        }

        if (comments != null && !comments.isEmpty()) {
            for (InterpretationComment comment : comments) {
                User user = comment.getUser();
                if (users.containsKey(user.getUId())) {
                    user = users.get(user.getUId());
                    comment.setUser(user);
                } else {
                    users.put(user.getUId(), user);
                }
            }
        }

        return new ArrayList<>(users.values());
    }

    private List<DbOperationImpl> createOperations(List<Interpretation> oldModels,
                                                   List<Interpretation> newModels) {
        List<DbOperationImpl> ops = new ArrayList<>();

        Map<String, Interpretation> newModelsMap = ModelUtils.toMap(newModels);
        Map<String, Interpretation> oldModelsMap = ModelUtils.toMap(oldModels);

        for (String oldModelKey : oldModelsMap.keySet()) {
            Interpretation newModel = newModelsMap.get(oldModelKey);
            Interpretation oldModel = oldModelsMap.get(oldModelKey);

            if (newModel == null) {
                ops.add(DbOperationImpl.with(mInterpretationStore).delete(oldModel));
                continue;
            }

            if (newModel.getLastUpdated().isAfter(oldModel.getLastUpdated())) {
                newModel.setId(oldModel.getId());
                ops.add(DbOperationImpl.with(mInterpretationStore).update(newModel));
            }

            newModelsMap.remove(oldModelKey);
        }

        for (String newModelKey : newModelsMap.keySet()) {
            Interpretation item = newModelsMap.get(newModelKey);

            // we also have to insert interpretation elements here
            ops.add(DbOperationImpl.with(mInterpretationStore).insert(item));

            List<InterpretationElement> elements = new ArrayList<>();
            // = mInterpretationService.getInterpretationElements(item);
            for (InterpretationElement element : elements) {
                ops.add(DbOperationImpl.with(mInterpretationElementStore).insert(element));
            }
        }

        return ops;
    }

    @Override
    public void pull(SyncStrategy syncStrategy) throws ApiException {
        getInterpretationDataFromServer();
        sendLocalChanges();
    }

    @Override
    public void pull(SyncStrategy syncStrategy, Set<String> uids) throws ApiException {

    }
}