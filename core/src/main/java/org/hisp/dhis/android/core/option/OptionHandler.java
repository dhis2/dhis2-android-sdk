/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.option;

import org.hisp.dhis.android.core.common.DictionaryTableHandler;
import org.hisp.dhis.android.core.common.ObjectStyle;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class OptionHandler {
    private final OptionStore optionStore;
    private final DictionaryTableHandler<ObjectStyle> styleHandler;


    public OptionHandler(OptionStore optionStore, DictionaryTableHandler<ObjectStyle> styleHandler) {
        this.optionStore = optionStore;
        this.styleHandler = styleHandler;
    }

    public void handleOptions(List<Option> options) {
        if (options == null) {
            return;
        }

        deleteOrPersistOptions(options);
    }

    private void deleteOrPersistOptions(List<Option> options) {
        int size = options.size();

        for (int i = 0; i < size; i++) {
            Option option = options.get(i);

            if (isDeleted(option)) {
                optionStore.delete(option.uid());
            } else {
                int updatedRow = optionStore.update(option.uid(), option.code(), option.name(), option.displayName(),
                        option.created(), option.lastUpdated(), option.optionSet().uid(), option.uid());

                if (updatedRow <= 0) {
                    optionStore.insert(option.uid(), option.code(), option.name(), option.displayName(),
                            option.created(), option.lastUpdated(), option.optionSet().uid());
                }

                styleHandler.handle(option.style(), option.uid(), OptionModel.TABLE);
            }
        }
    }
}
