/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorParserUtils.assumeProgramAttributeSyntax
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorParserUtils.assumeStageElementSyntax
import org.hisp.dhis.parser.expression.antlr.ExpressionBaseListener
import org.hisp.dhis.parser.expression.antlr.ExpressionParser
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.A_BRACE
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.HASH_BRACE

internal class ProgramIndicatorItemIdsCollector : ExpressionBaseListener() {

    val itemIds: MutableList<DimensionalItemId> = mutableListOf()

    override fun enterExpr(ctx: ExpressionParser.ExprContext) {
        if (ctx.it != null) {
            when (ctx.it.type) {
                HASH_BRACE -> {
                    assumeStageElementSyntax(ctx)

                    val stageId = ctx.uid0.text
                    val dataElementId = ctx.uid1.text

                    itemIds.add(
                        DimensionalItemId.builder()
                            .dimensionalItemType(DimensionalItemType.TRACKED_ENTITY_DATA_VALUE)
                            .id0(stageId)
                            .id1(dataElementId)
                            .build()
                    )
                }

                A_BRACE -> {
                    assumeProgramAttributeSyntax(ctx)

                    val attributeId = ctx.uid0.text

                    itemIds.add(
                        DimensionalItemId.builder()
                            .dimensionalItemType(DimensionalItemType.TRACKED_ENTITY_ATTRIBUTE)
                            .id0(attributeId)
                            .build()
                    )
                }
            }
        }
    }
}
