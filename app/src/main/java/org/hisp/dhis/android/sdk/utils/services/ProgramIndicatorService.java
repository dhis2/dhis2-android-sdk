/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.utils.services;

import android.util.Log;

import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.Constant;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.utils.DateUtils;
import org.hisp.dhis.android.sdk.utils.support.ExpressionUtils;
import org.hisp.dhis.android.sdk.utils.support.MathUtils;
import org.hisp.dhis.android.sdk.utils.support.TextUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author Chau Thu Tran
 * @author Simen Skogly Russnes
 */

/**
 * Handles logic related to ProgramIndicators such as calculating values based on expressions.
 * This class has been copied from the dhis 2 core repository and been stripped down.
 */
public class ProgramIndicatorService
{
    public static final String CLASS_TAG = "ProgramIndicatorService";
    public static final String ZERO = "0";

    /**
     * Calculate an program indicator value based on program instance and an
     * indicator defined for a TrackedEntityInstance
     *
     * @param programInstance  ProgramInstance
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public static String getProgramIndicatorValue( Enrollment programInstance, ProgramIndicator programIndicator )
    {
        Double value = getValue( programInstance, null, programIndicator );

        if ( value != null && !Double.isNaN( value ) )
        {
            value = MathUtils.getRounded( value, 2 );

            if ( programIndicator.getValueType().equals(ProgramIndicator.VALUE_TYPE_DATE) )
            {
                Date baseDate = new Date();

                if ( ProgramIndicator.INCIDENT_DATE.equals( programIndicator.getRootDate()) )
                {
                    baseDate = DateUtils.getMediumDate(programInstance.getDateOfIncident());
                }
                else if ( ProgramIndicator.ENROLLMENT_DATE.equals( programIndicator.getRootDate()) )
                {
                    baseDate = DateUtils.getMediumDate(programInstance.getDateOfEnrollment());
                }

                Date date = DateUtils.getDateAfterAddition( baseDate, value.intValue() );

                return DateUtils.getMediumDateString( date );
            }

            return String.valueOf( Math.floor( value ) );
        }

        return null;
    }

    /**
     * Calculate an program indicator value based on a single event
     *
     * @param event  Event
     * @param programIndicator ProgramIndicator
     * @return Indicator value
     */
    public static String getProgramIndicatorValue( Event event, ProgramIndicator programIndicator )
    {
        Double value = getValue( null, event, programIndicator );

        if ( value != null && !Double.isNaN( value ) )
        {
            value = MathUtils.getRounded( value, 2 );

            if ( programIndicator.getValueType().equals(ProgramIndicator.VALUE_TYPE_DATE) )
            {
                Date baseDate = new Date();

                if ( ProgramIndicator.INCIDENT_DATE.equals( programIndicator.getRootDate()) )
                { //todo: ignoring in case of single event event without registration
                    //baseDate = DateUtils.getMediumDate(programInstance.dateOfIncident);
                }
                else if ( ProgramIndicator.ENROLLMENT_DATE.equals( programIndicator.getRootDate()) )
                {
                    //baseDate = DateUtils.getMediumDate(programInstance.dateOfEnrollment);
                }

                Date date = DateUtils.getDateAfterAddition( baseDate, value.intValue() );

                return DateUtils.getMediumDateString( date );
            }
            return String.valueOf( value );
        }

        return null;
    }

    /**
     * Get indicator values of all program indicators defined for a TrackedEntityInstance
     *
     * @param programInstance ProgramInstance
     * @return Map<Indicator name, Indicator value>
     */
    public static Map<String, String> getProgramIndicatorValues( Enrollment programInstance )
    {
        Map<String, String> result = new HashMap<>();

        Collection<ProgramIndicator> programIndicators = new HashSet( programInstance.getProgram().getProgramIndicators() );

        for ( ProgramIndicator programIndicator : programIndicators )
        {
            String value = getProgramIndicatorValue( programInstance, programIndicator );

            if ( value != null )
            {
                result.put( programIndicator.getDisplayName(),
                        getProgramIndicatorValue( programInstance, programIndicator ) );
            }
        }

        return result;
    }

    /**
     * Get description of an indicator expression
     *
     * @param expression A expression string
     * @return The description
     */
    public static String getExpressionDescription( String expression )
    {
        StringBuffer description = new StringBuffer();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher( expression );

        while ( matcher.find() )
        {
            String key = matcher.group( 1 );
            String uid = matcher.group( 2 );

            if ( ProgramIndicator.KEY_DATAELEMENT.equals( key ) )
            {
                String de = matcher.group( 3 );

                ProgramStage programStage = MetaDataController.getProgramStage( uid );
                DataElement dataElement = MetaDataController.getDataElement( de );

                if ( programStage != null && dataElement != null )
                {
                    String programStageName = programStage.getDisplayName();

                    String dataelementName = dataElement.getDisplayName();

                    matcher.appendReplacement( description, programStageName + ProgramIndicator.SEPARATOR_ID + dataelementName );
                }
            }

            else if ( ProgramIndicator.KEY_ATTRIBUTE.equals( key ) )
            {
                TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute( uid );

                if ( attribute != null )
                {
                    matcher.appendReplacement( description, attribute.getDisplayName());
                }
            }
            else if ( ProgramIndicator.KEY_CONSTANT.equals( key ) )
            {
                Constant constant = MetaDataController.getConstant( uid );

                if ( constant != null )
                {
                    matcher.appendReplacement( description, constant.getDisplayName() );
                }
            }
            else if ( ProgramIndicator.KEY_PROGRAM_VARIABLE.equals( key ) )
            {
                if ( uid.equals( ProgramIndicator.CURRENT_DATE ) )
                {
                    matcher.appendReplacement( description, "Current date" );
                }
                else if ( uid.equals( ProgramIndicator.ENROLLMENT_DATE ) )
                {
                    matcher.appendReplacement( description, "Enrollment date" );
                }
                else if ( uid.equals( ProgramIndicator.INCIDENT_DATE ) )
                {
                    matcher.appendReplacement( description, "Incident date" );
                }
                else if ( uid.equals( ProgramIndicator.VALUE_COUNT ) )
                {
                    matcher.appendReplacement( description, "Value count" );
                }
            }
        }

        matcher.appendTail( description );

        return description.toString();

    }

    /**
     * Get description of an indicator expression
     *
     * @param expression A expression string
     * @return The expression is valid or not
     */
    public static String expressionIsValid( String expression )
    {
        StringBuffer description = new StringBuffer();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher( expression );

        while ( matcher.find() )
        {
            String key = matcher.group( 1 );
            String uid = matcher.group( 2 );

            if ( ProgramIndicator.KEY_DATAELEMENT.equals( key ) )
            {
                String de = matcher.group( 3 );

                ProgramStage programStage = MetaDataController.getProgramStage( uid );
                DataElement dataElement = MetaDataController.getDataElement( de );

                if ( programStage != null && dataElement != null )
                {
                    matcher.appendReplacement( description, String.valueOf( 1 ) );
                }
                else
                {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            }

            else if ( ProgramIndicator.KEY_ATTRIBUTE.equals( key ) )
            {
                TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute( uid );

                if ( attribute != null )
                {
                    matcher.appendReplacement( description, String.valueOf( 1 ) );
                }
                else
                {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            }
            else if ( ProgramIndicator.KEY_CONSTANT.equals( key ) )
            {
                Constant constant = MetaDataController.getConstant( uid );

                if ( constant != null )
                {
                    matcher.appendReplacement( description, String.valueOf( constant.getValue()) );
                }
                else
                {
                    return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
                }
            }
            else if ( ProgramIndicator.KEY_PROGRAM_VARIABLE.equals( key ) )
            {
                matcher.appendReplacement( description, String.valueOf( 0 ) );
            }
        }

        matcher.appendTail( description );

        // ---------------------------------------------------------------------
        // Well-formed expression
        // ---------------------------------------------------------------------

        if ( MathUtils.expressionHasErrors(description.toString()) )
        {
            return ProgramIndicator.EXPRESSION_NOT_WELL_FORMED;
        }

        return ProgramIndicator.VALID;
    }

    /**
     * Get all {@link org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement} part of the expression of the
     * given indicator.
     *
     * @param indicator the ProgramIndicator.
     * @return a set of ProgramStageDataElements.
     */
    public static Set<ProgramStageDataElement> getProgramStageDataElementsInExpression( ProgramIndicator indicator )
    {
        Set<ProgramStageDataElement> elements = new HashSet<>();

        Matcher matcher = ProgramIndicator.DATAELEMENT_PATTERN.matcher( indicator.getExpression() );

        while ( matcher.find() )
        {
            String ps = matcher.group( 1 );
            String de = matcher.group( 2 );

            ProgramStage programStage = MetaDataController.getProgramStage( ps );
            DataElement dataElement = MetaDataController.getDataElement( de );

            if ( programStage != null && dataElement != null )
            {
                elements.add( programStage.getProgramStageDataElement( dataElement.id ) );
            }
        }

        return elements;
    }

    /**
     * Get all {@link org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute} part of the expression of the
     * given indicator.
     *
     * @param indicator the ProgramIndicator.
     * @return a set of TrackedEntityAttributes.
     */
    public static Set<TrackedEntityAttribute> getAttributesInExpression( ProgramIndicator indicator )
    {
        Set<TrackedEntityAttribute> attributes = new HashSet<>();

        Matcher matcher = ProgramIndicator.ATTRIBUTE_PATTERN.matcher( indicator.getExpression());

        while ( matcher.find() )
        {
            String at = matcher.group( 1 );

            TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute( at );

            if ( attribute != null )
            {
                attributes.add( attribute );
            }
        }

        return attributes;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     *
     * @param programInstance can be null if event is not null in case single event without reg
     * @param event can be null if programInstance is not null
     * @param indicator
     * @return
     */
    private static Double getValue( Enrollment programInstance, Event event, ProgramIndicator indicator )
    {
        StringBuffer buffer = new StringBuffer();

        String expression = indicator.getExpression();

        Matcher matcher = ProgramIndicator.EXPRESSION_PATTERN.matcher( expression );

        int valueCount = 0;
        int zeroPosValueCount = 0;

        while ( matcher.find() )
        {
            String key = matcher.group( 1 );
            String uid = matcher.group( 2 );

            if ( ProgramIndicator.KEY_DATAELEMENT.equals( key ) )
            {
                String de = matcher.group( 3 );
                ProgramStage programStage = MetaDataController.getProgramStage( uid );
                DataElement dataElement = MetaDataController.getDataElement( de );

                if ( programStage != null && dataElement != null )
                {
                    Event programStageInstance;
                    if(programInstance == null) { //in case single event without reg
                        programStageInstance = event;
                    } else {
                        programStageInstance = DataValueController.getEvent(programInstance.localId, programStage.id );
                    }

                    DataValue dataValue = null;
                    if(programStageInstance.getDataValues() == null) {
                        continue;
                    }
                    for(DataValue value: programStageInstance.getDataValues()) {
                        if(value.getDataElement().equals(dataElement.id)) dataValue = value;
                    }

                    String value;
                    if ( dataValue == null || dataValue.getValue()== null || dataValue.getValue().isEmpty()) {
                        value = ZERO;
                    }
                    else {
                        value = dataValue.getValue();

                        valueCount++;
                        zeroPosValueCount = isZeroOrPositive( value ) ? ( zeroPosValueCount + 1 ) : zeroPosValueCount;
                    }

                    if ( dataElement.getType().equals( DataElement.VALUE_TYPE_DATE ) ) {
                        value = DateUtils.daysBetween( new Date(), DateUtils.getDefaultDate( value ) ) + " ";
                    }

                    matcher.appendReplacement( buffer, value );
                }
                else {
                    continue;
                }
            }
            else if ( ProgramIndicator.KEY_ATTRIBUTE.equals( key ) ) {
                if(programInstance != null) { //in case single event without reg
                    TrackedEntityAttribute attribute = MetaDataController.getTrackedEntityAttribute(uid);

                    if (attribute != null) {
                        TrackedEntityAttributeValue attributeValue = DataValueController.getTrackedEntityAttributeValue(
                                attribute.id, programInstance.trackedEntityInstance);
                        String value;
                        if (attributeValue == null || attributeValue.getValue()== null || attributeValue.getValue().isEmpty()) {
                            value = ZERO;
                        } else {
                            value = attributeValue.getValue();

                            valueCount++;
                            zeroPosValueCount = isZeroOrPositive( value ) ? ( zeroPosValueCount + 1 ) : zeroPosValueCount;
                        }

                        if (attribute.getValueType().equals(TrackedEntityAttribute.TYPE_DATE)) {
                            value = DateUtils.daysBetween(new Date(), DateUtils.getDefaultDate(value)) + " ";
                        }
                        matcher.appendReplacement(buffer, value);
                    } else {
                        continue;
                    }
                }
            }
            else if ( ProgramIndicator.KEY_CONSTANT.equals( key ) )
            {
                Constant constant = MetaDataController.getConstant( uid );

                if ( constant != null )
                {
                    matcher.appendReplacement( buffer, String.valueOf( constant.getValue()) );
                }
                else
                {
                    continue;
                }
            }
            else if ( ProgramIndicator.KEY_PROGRAM_VARIABLE.equals( key ) )
            {
                if(programInstance != null) { //in case of single event without reg
                    Date currentDate = new Date();
                    Date date = null;

                    if (uid.equals(ProgramIndicator.ENROLLMENT_DATE)) {
                        date = DateUtils.getMediumDate(programInstance.getDateOfEnrollment());
                    } else if (uid.equals(ProgramIndicator.INCIDENT_DATE)) {
                        date = DateUtils.getMediumDate(programInstance.getDateOfIncident());
                    } else if (uid.equals(ProgramIndicator.CURRENT_DATE)) {
                        date = currentDate;
                    }

                    if (date != null) {
                        matcher.appendReplacement(buffer, DateUtils.daysBetween(currentDate, date) + "");
                    }
                }
            }

        }

        expression = TextUtils.appendTail( matcher, buffer );

        // ---------------------------------------------------------------------
        // Value count variable
        // ---------------------------------------------------------------------
        buffer = new StringBuffer();
        matcher = ProgramIndicator.VALUECOUNT_PATTERN.matcher( expression );

        while ( matcher.find() )
        {
            String var = matcher.group( 1 );

            if ( ProgramIndicator.VAR_VALUE_COUNT.equals( var ) )
            {
                matcher.appendReplacement( buffer, String.valueOf( valueCount ) );
            }
            else if ( ProgramIndicator.VAR_ZERO_POS_VALUE_COUNT.equals( var ) )
            {
                matcher.appendReplacement( buffer, String.valueOf( zeroPosValueCount ) );
            }
        }

        expression = TextUtils.appendTail( matcher, buffer );
        return ExpressionUtils.evaluateToDouble(expression, null);
    }

    private static boolean isZeroOrPositive( String value )
    {
        return MathUtils.isNumeric( value ) && Double.valueOf( value ) >= 0d;
    }
}
