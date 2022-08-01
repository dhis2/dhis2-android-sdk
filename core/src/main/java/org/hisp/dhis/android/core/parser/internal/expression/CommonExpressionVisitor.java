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

package org.hisp.dhis.android.core.parser.internal.expression;

import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.DOUBLE_VALUE_IF_NULL;

import com.google.common.base.Joiner;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.Validate;
import org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.IndicatorContext;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.android.core.parser.internal.service.dataitem.DimensionalItemId;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorContext;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorExecutor;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.ProgramIndicatorSQLContext;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.antlr.AntlrExpressionVisitor;
import org.hisp.dhis.antlr.ParserExceptionWithoutContext;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessivePublicCount"})
public class CommonExpressionVisitor
        extends AntlrExpressionVisitor {

    private IdentifiableObjectStore<DataElement> dataElementStore;

    private IdentifiableObjectStore<TrackedEntityAttribute> attributeStore;

    private IdentifiableObjectStore<CategoryOptionCombo> categoryOptionComboStore;

    private IdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore;

    private IdentifiableObjectStore<ProgramStage> programStageStore;

    /**
     * Map of ExprItem instances to call for each expression item
     */
    private Map<Integer, ExpressionItem> itemMap;

    /**
     * Method to call within the ExprItem instance
     */
    private ExpressionItemMethod itemMethod;

    /**
     * Used to collect the string replacements to build a description.
     */
    private final Map<String, String> itemDescriptions = new HashMap<>();

    /**
     * Constants to use in evaluating an expression.
     */
    private Map<String, Constant> constantMap = new HashMap<>();

    /**
     * Used to collect the dimensional item ids in the expression.
     */
    private Set<DimensionalItemId> itemIds = new HashSet<>();

    /**
     * Organisation unit group counts to use in evaluating an expression.
     */
    Map<String, Integer> orgUnitCountMap = new HashMap<>();

    /**
     * Count of days in period to use in evaluating an expression.
     */
    private Double days;

    /**
     * Values to use for variables in evaluating an org.hisp.dhis.rules.parser.expression.
     */
    private Map<String, Double> itemValueMap = new HashMap<>();

    /**
     * Expression state
     */
    private final ExpressionState state = new ExpressionState();

    /**
     * Default value for data type double.
     */
    public static final double DEFAULT_DOUBLE_VALUE = 1d;

    // Program indicators

    private ProgramIndicatorContext programIndicatorContext;

    private ProgramIndicatorExecutor programIndicatorExecutor;

    private ProgramIndicatorSQLContext programIndicatorSQLContext;

    // Analytic indicator

    private IndicatorContext indicatorContext;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    protected CommonExpressionVisitor() {
        // This constructor is intentionally empty.
    }

    /**
     * Creates a new Builder for CommonExpressionVisitor.
     *
     * @return a Builder for CommonExpressionVisitor.
     */
    public static Builder newBuilder() {
        return new CommonExpressionVisitor.Builder();
    }

    // -------------------------------------------------------------------------
    // Visitor methods
    // -------------------------------------------------------------------------

    @Override
    public Object visitExpr(ExprContext ctx) {
        if (ctx.it != null) {
            ExpressionItem item = itemMap.get(ctx.it.getType());

            if (item == null) {
                throw new ParserExceptionWithoutContext(
                        "Item " + ctx.it.getText() + " not supported for this type of expression");
            }

            return itemMethod.apply(item, ctx, this);
        }

        if (ctx.expr().size() > 0) {
            // If there's an expr, visit the expr
            return visit(ctx.expr(0));
        }

        return visit(ctx.getChild(0)); // All others: visit first child.
    }

    // -------------------------------------------------------------------------
    // Logic for expression items
    // -------------------------------------------------------------------------

    /**
     * Visits a context while allowing null values (not replacing them
     * with 0 or ''), even if we would otherwise be replacing them.
     *
     * @param ctx any context
     * @return the value while allowing nulls
     */
    public Object visitAllowingNulls(ParserRuleContext ctx) {
        boolean savedReplaceNulls = state.getReplaceNulls();

        state.setReplaceNulls(false);

        Object result = visit(ctx);

        state.setReplaceNulls(savedReplaceNulls);

        return result;
    }

    /**
     * Handles nulls and missing values.
     * <p/>
     * If we should replace nulls with the default value, then do so, and
     * remember how many items found, and how many of them had values, for
     * subsequent MissingValueStrategy analysis.
     * <p/>
     * If we should not replace nulls with the default value, then don't,
     * as this is likely for some function that is testing for nulls, and
     * a missing value should not count towards the MissingValueStrategy.
     *
     * @param value the (possibly null) value
     * @return the value we should return.
     */
    public Object handleNulls(Object value) {
        if (state.getReplaceNulls()) {
            state.setItemsFound(state.getItemsFound() + 1);

            if (value == null) {
                return DOUBLE_VALUE_IF_NULL;
            } else {
                state.setItemValuesFound(state.getItemValuesFound() + 1);
                if (ParserUtils.isZeroOrPositive(value.toString())) {
                    state.setItemZeroPosValuesFound(state.getItemZeroPosValuesFound() + 1);
                }
            }
        }

        return value;
    }

    /**
     * Regenerates an expression by visiting all the children of the
     * expression node (including any terminal nodes).
     *
     * @param ctx the expression context
     * @return the regenerated expression (as a String)
     */
    public Object regenerateAllChildren(ExprContext ctx) {
        List<String> result = new ArrayList<>();
        for (ParseTree child : ctx.children) {
            result.add(castStringVisit(child));
        }
        return Joiner.on(' ').join(result);
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public IdentifiableObjectStore<DataElement> getDataElementStore() {
        return dataElementStore;
    }

    public IdentifiableObjectStore<TrackedEntityAttribute> getTrackedEntityAttributeStore() {
        return attributeStore;
    }

    public IdentifiableObjectStore<CategoryOptionCombo> getCategoryOptionComboStore() {
        return categoryOptionComboStore;
    }

    public IdentifiableObjectStore<OrganisationUnitGroup> getOrganisationUnitGroupStore() {
        return organisationUnitGroupStore;
    }

    public IdentifiableObjectStore<ProgramStage> getProgramStageStore() {
        return programStageStore;
    }

    public Map<String, String> getItemDescriptions() {
        return itemDescriptions;
    }

    public Map<String, Constant> getConstantMap() {
        return constantMap;
    }

    public Set<DimensionalItemId> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<DimensionalItemId> itemIds) {
        this.itemIds = itemIds;
    }

    public Map<String, Integer> getOrgUnitCountMap() {
        return orgUnitCountMap;
    }

    public void setOrgUnitCountMap(Map<String, Integer> orgUnitCountMap) {
        this.orgUnitCountMap = orgUnitCountMap;
    }

    public Map<String, Double> getItemValueMap() {
        return itemValueMap;
    }

    public void setItemValueMap(Map<String, Double> itemValueMap) {
        this.itemValueMap = itemValueMap;
    }

    public Double getDays() {
        return this.days;
    }

    public void setDays(Double days) {
        this.days = days;
    }

    public ExpressionState getState() {
        return state;
    }

    public ProgramIndicatorContext getProgramIndicatorContext() {
        return programIndicatorContext;
    }

    public ProgramIndicatorExecutor getProgramIndicatorExecutor() {
        return programIndicatorExecutor;
    }

    public ProgramIndicatorSQLContext getProgramIndicatorSQLContext() {
        return programIndicatorSQLContext;
    }

    public IndicatorContext getIndicatorContext() {
        return indicatorContext;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Builder for {@link CommonExpressionVisitor} instances.
     */
    public static class Builder {
        private final CommonExpressionVisitor visitor;

        protected Builder() {
            this.visitor = new CommonExpressionVisitor();
        }

        public Builder withItemMap(Map<Integer, ExpressionItem> itemMap) {
            this.visitor.itemMap = itemMap;
            return this;
        }

        public Builder withItemMethod(ExpressionItemMethod itemMethod) {
            this.visitor.itemMethod = itemMethod;
            return this;
        }

        public Builder withConstantMap(Map<String, Constant> constantMap) {
            this.visitor.constantMap = constantMap;
            return this;
        }

        public Builder withDataElementStore(IdentifiableObjectStore<DataElement> store) {
            this.visitor.dataElementStore = store;
            return this;
        }

        public Builder withTrackedEntityAttributeStore(IdentifiableObjectStore<TrackedEntityAttribute> store) {
            this.visitor.attributeStore = store;
            return this;
        }

        public Builder withCategoryOptionComboStore(IdentifiableObjectStore<CategoryOptionCombo> store) {
            this.visitor.categoryOptionComboStore = store;
            return this;
        }

        public Builder withOrganisationUnitGroupStore(IdentifiableObjectStore<OrganisationUnitGroup> store) {
            this.visitor.organisationUnitGroupStore = store;
            return this;
        }

        public Builder withProgramStageStore(IdentifiableObjectStore<ProgramStage> store) {
            this.visitor.programStageStore = store;
            return this;
        }

        public Builder withProgramIndicatorContext(ProgramIndicatorContext programIndicatorContext) {
            this.visitor.programIndicatorContext = programIndicatorContext;
            return this;
        }

        public Builder withProgramIndicatorExecutor(ProgramIndicatorExecutor programIndicatorExecutor) {
            this.visitor.programIndicatorExecutor = programIndicatorExecutor;
            return this;
        }

        public Builder withProgramIndicatorSQLContext(ProgramIndicatorSQLContext programIndicatorSQLContext) {
            this.visitor.programIndicatorSQLContext = programIndicatorSQLContext;
            return this;
        }

        public Builder withIndicatorContext(IndicatorContext indicatorContext) {
            this.visitor.indicatorContext = indicatorContext;
            return this;
        }

        private CommonExpressionVisitor validateCommonProperties() {
            Validate.notNull(this.visitor.constantMap, missingProperty("constantMap"));
            Validate.notNull(this.visitor.itemMap, missingProperty("itemMap"));
            Validate.notNull(this.visitor.itemMethod, missingProperty("itemMethod"));
            return visitor;
        }

        public CommonExpressionVisitor buildForExpressions() {
            Validate.notNull(this.visitor.dataElementStore, missingProperty("dataElementStore"));
            Validate.notNull(this.visitor.categoryOptionComboStore, missingProperty("categoryOptionComboStore"));
            Validate.notNull(this.visitor.organisationUnitGroupStore, missingProperty("organisationUnitGroupStore"));
            Validate.notNull(this.visitor.programStageStore, missingProperty("programStageStore"));

            return validateCommonProperties();
        }

        public CommonExpressionVisitor buildForProgramIndicator() {
            Validate.notNull(this.visitor.programIndicatorContext, missingProperty("programIndicatorContext"));
            Validate.notNull(this.visitor.programIndicatorExecutor, missingProperty("programIndicatorExecutor"));
            Validate.notNull(this.visitor.attributeStore, missingProperty("trackedEntityAttributeStore"));
            Validate.notNull(this.visitor.programStageStore, missingProperty("programStageStore"));

            return validateCommonProperties();
        }

        public CommonExpressionVisitor buildForProgramSQLIndicator() {
            Validate.notNull(this.visitor.programIndicatorSQLContext, missingProperty("programIndicatorSQLContext"));
            Validate.notNull(this.visitor.dataElementStore, missingProperty("dataElementStore"));
            Validate.notNull(this.visitor.attributeStore, missingProperty("trackedEntityAttributeStore"));

            return validateCommonProperties();
        }

        public CommonExpressionVisitor buildForAnalyticsIndicator() {
            Validate.notNull(this.visitor.indicatorContext, missingProperty("indicatorContext"));

            return validateCommonProperties();
        }
    }

    private static String missingProperty(String property) {
        return "Missing required property '" + property + "'.";
    }
}
