package org.hisp.dhis.android.core.audit;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity"
})
final class MetadataClassFactory {
    private static final Map<String, Class<?>> metadataClassMap = createMap();

    private static Map<String, Class<?>> createMap() {
        Map<String, Class<?>> myMap = new HashMap<>();
        myMap.put("trackedEntity", TrackedEntity.class);
        myMap.put("optionSet", OptionSet.class);
        myMap.put("option", Option.class);
        myMap.put("dataElement", DataElement.class);
        myMap.put("trackedEntityAttribute", TrackedEntityAttribute.class);
        myMap.put("program", Program.class);
        myMap.put("programStage", ProgramStage.class);
        myMap.put("programIndicator", ProgramIndicator.class);
        myMap.put("programRule", ProgramRule.class);
        myMap.put("programRuleAction", ProgramRuleAction.class);
        myMap.put("programRuleVariable", ProgramRuleVariable.class);
        myMap.put("relationshipType", RelationshipType.class);
        myMap.put("organisationUnit", OrganisationUnit.class);
        myMap.put("dataElementCategory", Category.class);
        myMap.put("dataElementCategoryOption", CategoryOption.class);
        myMap.put("dataElementCategoryCombo", CategoryCombo.class);
        myMap.put("dataElementCategoryOptionCombo", CategoryOptionCombo.class);
        return myMap;
    }

    public static Class<?> getByName(String name) {
        if (metadataClassMap.containsKey(name)) {
            return metadataClassMap.get(name);
        } else {
            throw new IllegalArgumentException("No exists a metadata class for name: " + name);
        }
    }

    private MetadataClassFactory() {
    }
}
