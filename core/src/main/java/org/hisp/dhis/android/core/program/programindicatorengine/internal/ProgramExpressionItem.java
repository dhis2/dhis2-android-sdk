package org.hisp.dhis.android.core.program.programindicatorengine.internal;

/*
 * Copyright (c) 2004-2020, University of Oslo
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

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemAttribute;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemPsEventdate;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.dataitem.ProgramItemStageElement;
import org.hisp.dhis.android.core.program.programindicatorengine.internal.variable.ProgramVariableItem;
import org.hisp.dhis.antlr.ParserExceptionWithoutContext;

import java.util.List;
import java.util.Map;

import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext;

@SuppressWarnings({"PMD.CyclomaticComplexity"})
public abstract class ProgramExpressionItem
        implements ExpressionItem {

    protected ProgramExpressionItem getProgramArgType(ExprContext ctx) {
        if (ctx.psEventDate != null) {
            return new ProgramItemPsEventdate();
        }

        if (ctx.uid1 != null) {
            return new ProgramItemStageElement();
        }

        if (ctx.uid0 != null) {
            return new ProgramItemAttribute();
        }

        if (ctx.programVariable() != null) {
            return new ProgramVariableItem();
        }

        throw new ParserExceptionWithoutContext("Illegal argument in program indicator expression: " + ctx.getText());
    }

    protected Event getSingleEvent(CommonExpressionVisitor visitor) {
        Enrollment enrollment = visitor.getProgramIndicatorContext().enrollment();
        Map<String, List<Event>> events = visitor.getProgramIndicatorContext().events();

        if (enrollment == null && events.size() == 1 && events.values().iterator().next().size() == 1) {
           return events.values().iterator().next().get(0);
        }
        return null;
    }

    protected String formatValue(String value, ValueType valueType) {
        if (valueType == ValueType.BOOLEAN) {
            return "true".equals(value) ? "1" : "0";
        }
        return value;
    }
}
