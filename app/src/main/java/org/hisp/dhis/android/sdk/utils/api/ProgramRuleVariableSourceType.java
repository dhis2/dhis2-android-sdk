package org.hisp.dhis.android.sdk.utils.api;

public enum ProgramRuleVariableSourceType {
    DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE("dataelement_newest_event_program_stage"),
    DATAELEMENT_NEWEST_EVENT_PROGRAM("dataelement_newest_event_program"),
    DATAELEMENT_CURRENT_EVENT("dataelement_current_event"),
    DATAELEMENT_PREVIOUS_EVENT("dataelement_previous_event"),
    CALCULATED_VALUE("calculated_value"),
    TEI_ATTRIBUTE("tei_attribute"),
    CONSTANT("constant");

    private final String value;

    private ProgramRuleVariableSourceType( String value )
    {
        this.value = value;
    }

    public static ProgramRuleVariableSourceType fromValue( String value )
    {
        for ( ProgramRuleVariableSourceType type : ProgramRuleVariableSourceType.values() )
        {
            if ( type.value.equalsIgnoreCase( value ) )
            {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
