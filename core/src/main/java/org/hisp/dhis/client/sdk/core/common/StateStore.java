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

package org.hisp.dhis.client.sdk.core.common;

import org.hisp.dhis.client.sdk.core.common.persistence.Store;
import org.hisp.dhis.client.sdk.models.common.base.Model;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.common.state.State;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StateStore extends Store<State> {

    //////////////////////////////////////////////////////////////////////////////////////////
    // Helper methods for work with models and actions directly (by bypassing State model)
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param object Model for which the action should be inserted.
     * @param action Action to insert.
     */
    <T extends Model> boolean insertActionForModel(T object, Action action);

    /**
     * @param object Model for which the action should be updated.
     * @param action Action to update.
     */
    <T extends Model> boolean updateActionForModel(T object, Action action);

    /**
     * @param object Model for which the action should be saved.
     * @param action Action to save.
     */
    <T extends Model> boolean saveActionForModel(T object, Action action);

    /**
     * @param object Model for which the action should be deleted.
     */
    <T extends Model> boolean deleteActionForModel(T object);

    /**
     * @param modelType the model class for which we want to remove all corresponding actions
     * @param <T>       type of model class (must extend Model interface)
     * @return true if operation succeeded
     */
    <T extends Model> boolean deleteActionsForModelType(Class<T> modelType);

    /**
     * @param object Model which state the method returns.
     * @return State of given object.
     */
    <T extends Model> State queryStateForModel(T object);


    <T extends Model> Map<Long, State> queryStatesForModels(List<T> list);

    /**
     * @param object Model which action should be returned.
     * @return State of given object.
     */
    <T extends Model> Action queryActionForModel(T object);

    /**
     * @param clazz Class for which we want to retrieve all state models.
     * @return List of states for given class.
     */
    <T extends Model> List<State> queryStatesForModelClass(Class<T> clazz);

    /**
     * @param clazz Class for which we want to retrieve actions.
     * @return Map where key is id and the value is action for model of given Class.
     */
    <T extends Model> Map<Long, Action> queryActionsForModel(Class<T> clazz);

    /**
     * @param clazz   Class, instances of which we want to retrieve from database.
     * @param actions Action(s) which we want to have in resulting payload from database.
     * @return List of Class typed instances which State contain given action.
     */
    <T extends Model> List<T> queryModelsWithActions(Class<T> clazz, Action... actions);

    /**
     * @param clazz   Class, instances of which we want to retrieve from database.
     * @param uids    Set of model uids
     * @param actions Action(s) which we want to have in resulting payload from database.
     * @return List of Class typed instances which State contain given action.
     */
    <T extends Model> List<T> queryModelsWithActions(
            Class<T> clazz, Set<String> uids, Action... actions);
}
