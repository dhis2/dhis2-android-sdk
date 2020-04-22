/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.parser.service;

import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.parser.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.expression.ExpressionItem;
import org.hisp.dhis.android.core.parser.expression.ExpressionItemMethod;
import org.hisp.dhis.android.core.parser.service.dataitem.DimItemDataElementAndOperand;
import org.hisp.dhis.android.core.parser.service.dataitem.ItemDays;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.hisp.dhis.antlr.Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hisp.dhis.android.core.parser.expression.ParserUtils.COMMON_EXPRESSION_ITEMS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.DAYS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.HASH_BRACE;

public class ExpressionService {

    private final Map<Integer, ExpressionItem> VALIDATION_RULE_EXPRESSION_ITEMS;

    public ExpressionService() {
        this.VALIDATION_RULE_EXPRESSION_ITEMS = getExpressionItems();
    }

    private Map<Integer, ExpressionItem> getExpressionItems() {
        Map<Integer, ExpressionItem> expressionItems = new HashMap<>(COMMON_EXPRESSION_ITEMS);

        expressionItems.put(HASH_BRACE, new DimItemDataElementAndOperand());
        //expressionItems.put(OUG_BRACE, new ItemOrgUnitGroup());
        expressionItems.put(DAYS, new ItemDays());
        return expressionItems;
    }

    public Double getExpressionValue(String expression,
                                     Map<DataElementOperand, Double> valueMap, Map<String, Constant> constantMap,
                                     Map<String, Integer> orgUnitCountMap, Integer days,
                                     MissingValueStrategy missingValueStrategy ) {

        if ( expression == null) {
            return null;
        }

        CommonExpressionVisitor visitor = newVisitor(
                ExpressionItem::evaluate,
                constantMap
        );

        Map<String, Double> itemValueMap = new HashMap<>();
        for (Map.Entry<DataElementOperand, Double> entry : valueMap.entrySet()) {
            // TODO create key
            itemValueMap.put(entry.getKey().dataElement().uid(), entry.getValue());
        }

        visitor.setItemValueMap(itemValueMap);
        //visitor.setOrgUnitCountMap( orgUnitCountMap );

        if ( days != null )
        {
            visitor.setDays( Double.valueOf( days ) );
        }

        Object value = Parser.visit(expression, visitor);

        int itemsFound = visitor.getItemsFound();
        int itemValuesFound = visitor.getItemValuesFound();

        switch ( missingValueStrategy )
        {
            case SKIP_IF_ANY_VALUE_MISSING:
                if ( itemValuesFound < itemsFound )
                {
                    return null;
                }

            case SKIP_IF_ALL_VALUES_MISSING:
                if ( itemsFound != 0 && itemValuesFound == 0 )
                {
                    return null;
                }

            case NEVER_SKIP:
                if ( value == null )
                {
                    // TODO Handle other ParseType
                    return 0d;
                }
        }

        return (Double) value;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Creates a new ExpressionItemsVisitor object.
     */
    private CommonExpressionVisitor newVisitor(
                                                //ParseType parseType,
                                               ExpressionItemMethod itemMethod,
                                               //List<Period> samplePeriods,
                                               Map<String, Constant> constantMap )
    {
        return CommonExpressionVisitor.newBuilder()
                //.withItemMap( PARSE_TYPE_EXPRESSION_ITEMS.get( parseType ) )
                .withItemMap( VALIDATION_RULE_EXPRESSION_ITEMS )
                .withItemMethod( itemMethod )
                .withConstantMap( constantMap )
                //.withDimensionService( dimensionService )
                //.withOrganisationUnitGroupService( organisationUnitGroupService )
                //.withSamplePeriods( samplePeriods )();
                .buildForExpressions();
    }
}