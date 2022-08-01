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

package org.hisp.dhis.android.core.parser.internal.service.dataitem;

import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.antlr.ParserExceptionWithoutContext;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext;

import static org.apache.commons.lang3.ObjectUtils.anyNotNull;
import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.DOUBLE_VALUE_IF_NULL;
import static org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType.DATA_ELEMENT;
import static org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemType.DATA_ELEMENT_OPERAND;

/**
 * Parsed expression item as handled by the expression service.
 * <p/>
 * When getting item id and org unit group, just return default values
 * (because not every item implements these, only those that need to.)
 *
 * @author Jim Grace
 */
public class DimItemDataElementAndOperand extends DimensionalItem {

    @Override
    public Object getDescription(ExprContext ctx, CommonExpressionVisitor visitor) {

        DataElement dataElement = visitor.getDataElementStore().selectByUid(ctx.uid0.getText());

        if (dataElement != null) {
            StringBuilder description = new StringBuilder(dataElement.displayName());

            if (isDataElementOperandSyntax(ctx)) {
                CategoryOptionCombo categoryOptionCombo =
                        visitor.getCategoryOptionComboStore().selectByUid(ctx.uid1.getText());

                String cocDescription = categoryOptionCombo == null ? ctx.uid1.getText() :
                        categoryOptionCombo.displayName();

                description.append(" (").append(cocDescription).append(')');
            }

            visitor.getItemDescriptions().put(ctx.getText(), description.toString());
        }

        return DOUBLE_VALUE_IF_NULL;
    }

    @Override
    public DimensionalItemId getDimensionalItemId(ExprContext ctx) {
        if (isDataElementOperandSyntax(ctx)) {
            return DimensionalItemId.builder()
                    .dimensionalItemType(DATA_ELEMENT_OPERAND)
                    .id0(ctx.uid0.getText())
                    .id1(ctx.uid1 == null ? null : ctx.uid1.getText())
                    .id2(ctx.uid2 == null ? null : ctx.uid2.getText())
                    .build();
        } else {
            return DimensionalItemId.builder()
                    .dimensionalItemType(DATA_ELEMENT)
                    .id0(ctx.uid0.getText())
                    .build();
        }
    }

    @Override
    public String getId(ExprContext ctx) {
        if (isDataElementOperandSyntax(ctx)) {
            return ctx.uid0.getText() + "." +
                    (ctx.uid1 == null ? "*" : ctx.uid1.getText()) +
                    (ctx.uid2 == null ? "" : "." + ctx.uid2.getText());
        } else {
            // Data element:
            return ctx.uid0.getText();
        }
    }

    /**
     * Does an item of the form #{...} have the syntax of a
     * data element operand (as opposed to a data element)?
     *
     * @param ctx the item context
     * @return true if data element operand syntax
     */
    private boolean isDataElementOperandSyntax(ExprContext ctx) {
        if (ctx.uid0 == null) {
            throw new ParserExceptionWithoutContext("Data Element or DataElementOperand must have a uid " +
                    ctx.getText());
        }

        return anyNotNull(ctx.uid1, ctx.uid2);
    }

}
