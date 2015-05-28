package org.hisp.dhis.android.sdk.utils.support;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

/**
 * @author Lars Helge Overland
 */
public class ExpressionUtils
{
    private static final JexlEngine JEXL = new JexlEngine();

    static 
    {
        JEXL.setCache( 512 );
        JEXL.setSilent( false );
    }
    
    /**
     * Evaluates the given expression. The given variables will be substituted 
     * in the expression.
     * 
     * @param expression the expression.
     * @param vars the variables, can be null.
     * @return the result of the evaluation.
     */
    public static Object evaluate( String expression, Map<String, Object> vars )
    {
        Expression exp = JEXL.createExpression( expression );
        
        JexlContext context = vars != null ? new MapContext( vars ) : new MapContext();
                
        return exp.evaluate( context );
    }

    /**
     * Evaluates the given expression to true or false. The given variables will 
     * be substituted in the expression.
     * 
     * @param expression the expression.
     * @param vars the variables, can be null.
     * @return true or false.
     */
    public static boolean isTrue( String expression, Map<String, Object> vars )
    {
        Boolean result = (Boolean) evaluate( expression, vars );
        
        return result != null ? result : false;
    }    
}
