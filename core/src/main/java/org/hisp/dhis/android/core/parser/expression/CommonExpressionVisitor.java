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

package org.hisp.dhis.android.core.parser.expression;

import org.apache.commons.lang3.Validate;
import org.hisp.dhis.android.core.parser.antlr.AntlrExpressionVisitor;
import org.hisp.dhis.android.core.parser.antlr.ParserExceptionWithoutContext;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common traversal of the ANTLR4 org.hisp.dhis.rules.parser.expression parse tree using the
 * visitor pattern.
 *
 * @author Jim Grace
 */
public class CommonExpressionVisitor
        extends AntlrExpressionVisitor
{
    /**
     * Map of ExprItem instances to call for each expression item
     */
    private Map<Integer, ExpressionItem> itemMap;

    /**
     * Method to call within the ExprItem instance
     */
    private ExpressionItemMethod itemMethod;

    /**
     * Count of days in period to use in evaluating an expression.
     */
    private Double days = null;

    /**
     * By default, replace nulls with 0 or ''.
     */
    private boolean replaceNulls = true;

    /**
     * Values to use for variables in evaluating an org.hisp.dhis.rules.parser.expression.
     */
    private Map<String, Double> itemValueMap = new HashMap<>();

    /**
     * Supplementary data for users and org units
     */
    private Map<String, List<String>> supplementaryData = new HashMap<>();

    /**
     * Default value for data type double.
     */
    public static final double DEFAULT_DOUBLE_VALUE = 1d;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    CommonExpressionVisitor()
    {
    }

    /**
     * Creates a new Builder for CommonExpressionVisitor.
     *
     * @return a Builder for CommonExpressionVisitor.
     */
    public static Builder newBuilder()
    {
        return new CommonExpressionVisitor.Builder();
    }

    // -------------------------------------------------------------------------
    // Visitor methods
    // -------------------------------------------------------------------------

    @Override
    public Object visitExpr( ExprContext ctx )
    {
        if ( ctx.it != null )
        {
            ExpressionItem item = itemMap.get( ctx.it.getType() );

            if ( item == null )
            {
                throw new ParserExceptionWithoutContext(
                        "Item " + ctx.it.getText() + " not supported for this type of expression" );
            }

            return itemMethod.apply( item, ctx, this );
        }

        if ( ctx.expr().size() > 0 ) // If there's an expr, visit the expr
        {
            return visit( ctx.expr( 0 ) );
        }

        return visit( ctx.getChild( 0 ) ); // All others: visit first child.
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Map<String, Double> getItemValueMap()
    {
        return itemValueMap;
    }

    public void setItemValueMap( Map<String, Double> itemValueMap )
    {
        this.itemValueMap = itemValueMap;
    }

    public Double getDays() {
        return this.days;
    }

    public void setDays(Double days) {
        this.days = days;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Builder for {@link CommonExpressionVisitor} instances.
     */
    public static class Builder {
        private CommonExpressionVisitor visitor;

        protected Builder()
        {
            this.visitor = new CommonExpressionVisitor();
        }

        public Builder withItemMap( Map<Integer, ExpressionItem> itemMap ) {
            this.visitor.itemMap = itemMap;
            return this;
        }

        public Builder withExprItemMethod( ExpressionItemMethod expressionItemMethod) {
            this.visitor.itemMethod = expressionItemMethod;
            return this;
        }

        public CommonExpressionVisitor validateCommonProperties() {
            Validate.notNull( this.visitor.itemMap, "Missing required property 'itemMap'" );
            return visitor;
        }
    }
}
