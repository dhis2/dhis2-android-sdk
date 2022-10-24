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

package org.hisp.dhis.android.core.parser.internal.service;

import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.COMMON_EXPRESSION_ITEMS;
import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_EVALUATE;
import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_GET_DESCRIPTIONS;
import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_GET_IDS;
import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.ITEM_REGENERATE;
import static org.hisp.dhis.android.core.validation.MissingValueStrategy.NEVER_SKIP;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.HASH_BRACE;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.internal.expression.CommonParser;
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem;
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItemMethod;
import org.hisp.dhis.android.core.parser.internal.expression.literal.RegenerateLiteral;
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimItemDataElementAndOperand;
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId;
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.validation.MissingValueStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

@SuppressWarnings({
        "PMD.TooManyStaticImports",
        "PMD.ExcessiveImports",
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity"})
public class ExpressionService {

    private final IdentifiableObjectStore<DataElement> dataElementStore;
    private final CategoryOptionComboStore categoryOptionComboStore;
    private final IdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore;
    private final IdentifiableObjectStore<ProgramStage> programStageStore;

    private final Map<Integer, ExpressionItem> validationRuleExpressionItems;

    @Inject
    public ExpressionService(IdentifiableObjectStore<DataElement> dataElementStore,
                             CategoryOptionComboStore categoryOptionComboStore,
                             IdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore,
                             IdentifiableObjectStore<ProgramStage> programStageStore) {
        this.dataElementStore = dataElementStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
        this.organisationUnitGroupStore = organisationUnitGroupStore;
        this.programStageStore = programStageStore;
        this.validationRuleExpressionItems = getValidationRuleExpressionItems();
    }

    private Map<Integer, ExpressionItem> getValidationRuleExpressionItems() {
        Map<Integer, ExpressionItem> expressionItems = new HashMap<>(COMMON_EXPRESSION_ITEMS);

        expressionItems.put(HASH_BRACE, new DimItemDataElementAndOperand());

        return expressionItems;
    }

    public Set<DimensionalItemId> getDimensionalItemIds(String expression) {
        if (expression == null) {
            return Collections.emptySet();
        }

        Set<DimensionalItemId> itemIds = new HashSet<>();
        CommonExpressionVisitor visitor = newVisitor(ITEM_GET_IDS, Collections.emptyMap());
        visitor.setItemIds(itemIds);

        CommonParser.visit(expression, visitor);

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

        CommonParser.visit(expression, visitor);

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

        Object value = CommonParser.visit(expression, visitor);

        int itemsFound = visitor.getState().getItemsFound();
        int itemValuesFound = visitor.getState().getItemValuesFound();

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

        if (value instanceof Double && Double.isNaN((double) value)) {
            return null;
        } else {
            return value;
        }
    }

    public String regenerateExpression(String expression,
                                       Map<DimensionalItemObject, Double> valueMap,
                                       Map<String, Constant> constantMap,
                                       Map<String, Integer> orgUnitCountMap,
                                       Integer days) {

        if (expression == null) {
            return "";
        }

        CommonExpressionVisitor visitor = newVisitor(
                ITEM_REGENERATE,
                constantMap
        );

        Map<String, Double> itemValueMap = new HashMap<>();
        for (Map.Entry<DimensionalItemObject, Double> entry : valueMap.entrySet()) {
            itemValueMap.put(entry.getKey().getDimensionItem(), entry.getValue());
        }

        visitor.setItemValueMap(itemValueMap);
        visitor.setOrgUnitCountMap(orgUnitCountMap);
        visitor.setExpressionLiteral(new RegenerateLiteral());

        if (days != null) {
            visitor.setDays(Double.valueOf(days));
        }

        return (String) CommonParser.visit(expression, visitor);
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
                .withProgramStageStore(programStageStore)
                //.withSamplePeriods( samplePeriods )()
                .buildForExpressions();
    }
}