package org.hisp.dhis.android.sdk.utils.api;

public enum ProgramRuleActionType {
    DISPLAYTEXT( "displaytext" ),
    DISPLAYKEYVALUEPAIR( "displaykeyvaluepair" ),
    HIDEFIELD( "hidefield" ),
    HIDESECTION( "hidesection" ),
    ASSIGN( "assign" ),
    SHOWWARNING( "showwarning" ),
    SHOWERROR( "showerror" ),
    CREATEEVENT("createevent");

    final String value;

    private ProgramRuleActionType( String value )
    {
        this.value = value;
    }

    public static ProgramRuleActionType fromValue( String value )
    {
        for ( ProgramRuleActionType type : ProgramRuleActionType.values() )
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
