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
import org.hisp.dhis.antlr.AntlrExprFunction;
import org.hisp.dhis.antlr.AntlrExpressionVisitor;
import org.hisp.dhis.antlr.ParserExceptionWithoutContext;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;

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
     * Map of ExprFunction instances to call for each org.hisp.dhis.rules.parser.expression function
     */
    private Map<Integer, AntlrExprFunction> functionMap;

    /**
     * Map of ExprItem instances to call for each expression item
     */
    private Map<Integer, ExprItem> itemMap;

    /**
     * Method to call within the ExprItem instance
     */
    private ExprItemMethod itemMethod;

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

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    protected CommonExpressionVisitor()
    {
    }

    @Override
    public Object visitItem( ExpressionParser.ItemContext ctx ) {
        ExprItem item = itemMap.get( ctx.it.getType() );

        if ( item == null ) {
            throw new org.hisp.dhis.antlr.ParserExceptionWithoutContext(
                    "Item " + ctx.it.getText() + " not supported for this type of expression" );
        }

        return itemMethod.apply( item, ctx, this );
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
    public Object visitExpr( ExpressionParser.ExprContext ctx ) {

        if ( ctx.fun != null ) {
            AntlrExprFunction function = functionMap.get( ctx.fun.getType() );

            if ( function == null ) {
                throw new ParserExceptionWithoutContext( "Function " + ctx.fun.getText() + " not supported for this type of expression" );
            }

            return function.evaluate(ctx, this);
        }

        if ( ctx.expr().size() > 0 ) { // If there's an expr, visit the expr
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

        public Builder withFunctionMap( Map<Integer, AntlrExprFunction> functionMap ) {
            this.visitor.functionMap = functionMap;
            return this;
        }

        public Builder withItemMap( Map<Integer, ExprItem> itemMap ) {
            this.visitor.itemMap = itemMap;
            return this;
        }

        public Builder withExprItemMethod( ExprItemMethod exprItemMethod ) {
            this.visitor.itemMethod = exprItemMethod;
            return this;
        }

        public CommonExpressionVisitor validateCommonProperties() {
            Validate.notNull( this.visitor.functionMap, "Missing required property 'functionMap'" );
            Validate.notNull( this.visitor.itemMap, "Missing required property 'itemMap'" );
            return visitor;
        }
    }
}
