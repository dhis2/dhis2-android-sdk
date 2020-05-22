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

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.parser.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.expression.ExpressionItem;
import org.hisp.dhis.android.core.parser.expression.ExpressionItemMethod;
import org.hisp.dhis.android.core.parser.service.dataitem.DimItemDataElementAndOperand;
import org.hisp.dhis.android.core.parser.service.dataitem.DimensionalItemId;
import org.hisp.dhis.android.core.parser.service.dataitem.ItemConstant;
import org.hisp.dhis.android.core.parser.service.dataitem.ItemDays;
import org.hisp.dhis.android.core.parser.service.dataitem.ItemOrgUnitGroup;
import org.hisp.dhis.android.core.parser.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;
import org.hisp.dhis.antlr.Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import static org.hisp.dhis.android.core.parser.expression.ParserUtils.COMMON_EXPRESSION_ITEMS;
import static org.hisp.dhis.android.core.parser.expression.ParserUtils.ITEM_EVALUATE;
import static org.hisp.dhis.android.core.parser.expression.ParserUtils.ITEM_GET_DESCRIPTIONS;
import static org.hisp.dhis.android.core.parser.expression.ParserUtils.ITEM_GET_IDS;
import static org.hisp.dhis.android.core.validation.MissingValueStrategy.NEVER_SKIP;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.C_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.DAYS;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.HASH_BRACE;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.OUG_BRACE;

@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public class ExpressionService {

    private IdentifiableObjectStore<DataElement> dataElementStore;
    private CategoryOptionComboStore categoryOptionComboStore;
    private IdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore;

    private final Map<Integer, ExpressionItem> validationRuleExpressionItems;

    @Inject
    public ExpressionService(IdentifiableObjectStore<DataElement> dataElementStore,
                             CategoryOptionComboStore categoryOptionComboStore,
                             IdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore) {
        this.dataElementStore = dataElementStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
        this.organisationUnitGroupStore = organisationUnitGroupStore;
        this.validationRuleExpressionItems = getExpressionItems();
    }

    private Map<Integer, ExpressionItem> getExpressionItems() {
        Map<Integer, ExpressionItem> expressionItems = new HashMap<>(COMMON_EXPRESSION_ITEMS);

        expressionItems.put(HASH_BRACE, new DimItemDataElementAndOperand());
        expressionItems.put(OUG_BRACE, new ItemOrgUnitGroup());
        expressionItems.put(DAYS, new ItemDays());
        expressionItems.put(C_BRACE, new ItemConstant());
        return expressionItems;
    }

    public Set<DimensionalItemId> getDimensionalItemIds(String expression) {
        if (expression == null) {
            return Collections.emptySet();
        }

        Set<DimensionalItemId> itemIds = new HashSet<>();
        CommonExpressionVisitor visitor = newVisitor(ITEM_GET_IDS, Collections.emptyMap());
        visitor.setItemIds(itemIds);

        Parser.visit(expression, visitor);

        return itemIds;
    }

    public Set<DataElementOperand> getDataElementOperands(String expression) {
        Set<DimensionalItemId> dimensionalItemIds = getDimensionalItemIds(expression);

        Set<DataElementOperand> dataElementOperands = new HashSet<>();
        for (DimensionalItemId di : dimensionalItemIds) {
            if (di.isDataElementOrOperand()) {
                dataElementOperands.add(DataElementOperand.builder()
                        .dataElement(ObjectWithUid.create(di.id0()))
                        .categoryOptionCombo(di.id1() == null ? null : ObjectWithUid.create(di.id1()))
                        .build());
            }
        }
        return dataElementOperands;
    }

    public String getExpressionDescription(String expression, Map<String, Constant> constantMap) {

        if (expression == null) {
            return "";
        }

        CommonExpressionVisitor visitor = newVisitor(ITEM_GET_DESCRIPTIONS, constantMap);

        Parser.visit(expression, visitor);

        Map<String, String> itemDescriptions = visitor.getItemDescriptions();

        String description = expression;

        for (Map.Entry<String, String> entry : itemDescriptions.entrySet()) {
            description = description.replace(entry.getKey(), entry.getValue());
        }

        return description;
    }

    public Object getExpressionValue(String expression) {
        return getExpressionValue(expression, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                0, NEVER_SKIP);
    }

    public Object getExpressionValue(String expression,
                                     Map<DimensionalItemObject, Double> valueMap,
                                     Map<String, Constant> constantMap,
                                     Map<String, Integer> orgUnitCountMap,
                                     Integer days,
                                     MissingValueStrategy missingValueStrategy) {

        if (expression == null) {
            return null;
        }

        CommonExpressionVisitor visitor = newVisitor(
                ITEM_EVALUATE,
                constantMap
        );

        Map<String, Double> itemValueMap = new HashMap<>();
        for (Map.Entry<DimensionalItemObject, Double> entry : valueMap.entrySet()) {
            itemValueMap.put(entry.getKey().getDimensionItem(), entry.getValue());
        }

        visitor.setItemValueMap(itemValueMap);
        visitor.setOrgUnitCountMap(orgUnitCountMap);

        if (days != null) {
            visitor.setDays(Double.valueOf(days));
        }

        Object value = Parser.visit(expression, visitor);

        int itemsFound = visitor.getItemsFound();
        int itemValuesFound = visitor.getItemValuesFound();

        switch (missingValueStrategy) {
            case SKIP_IF_ANY_VALUE_MISSING:
                if (itemValuesFound < itemsFound) {
                    return null;
                }

            case SKIP_IF_ALL_VALUES_MISSING:
                if (itemsFound != 0 && itemValuesFound == 0) {
                    return null;
                }

            case NEVER_SKIP:
            default:
                if (value == null) {
                    // TODO Handle other ParseType
                    return 0d;
                }
        }

        return value;
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
            Map<String, Constant> constantMap) {
        return CommonExpressionVisitor.newBuilder()
                //.withItemMap( PARSE_TYPE_EXPRESSION_ITEMS.get( parseType ) )
                .withItemMap(validationRuleExpressionItems)
                .withItemMethod(itemMethod)
                .withConstantMap(constantMap)
                .withDataElementStore(dataElementStore)
                .withCategoryOptionComboStore(categoryOptionComboStore)
                .withOrganisationUnitGroupStore(organisationUnitGroupStore)
                //.withSamplePeriods( samplePeriods )()
                .buildForExpressions();
    }
}