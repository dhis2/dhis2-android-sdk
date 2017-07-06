/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.controllers.wrappers;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.Attribute;
import org.hisp.dhis.android.sdk.persistence.models.AttributeValue;
import org.hisp.dhis.android.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis.android.sdk.persistence.models.ProgramIndicatorToSectionRelationship;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageDataElement;
import org.hisp.dhis.android.sdk.persistence.models.ProgramStageSection;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 21.08.15.
 * Class used to link references of a Program and corresponding sub items
 */
public class ProgramWrapper {

    public static List<DbOperation> setReferences(Program program) {
        List<DbOperation> operations = new ArrayList<>();
        Map<String, Attribute> attributes = new HashMap<>();
        if(program != null && program.getUid() != null) {
            operations.addAll(update(program));
            operations.add(DbOperation.save(program));
            int sortOrder = 0;
            for (ProgramTrackedEntityAttribute ptea : program.getProgramTrackedEntityAttributes()) {
                ptea.setProgram(program.getUid());
                ptea.setSortOrder(sortOrder);
                operations.add(DbOperation.save(ptea));
                sortOrder++;
            }

            for (ProgramStage programStage : program.getProgramStages()) {
                operations.add(DbOperation.save(programStage));
                if (programStage.getProgramStageSections() != null && !programStage.getProgramStageSections().isEmpty()) {
                    // due to the way the WebAPI lists programStageSections we have to manually
                    // set id of programStageSection in programStageDataElements to be able to
                    // access it later when loading from local db
                    for (ProgramStageSection programStageSection : programStage.getProgramStageSections()) {
                        operations.add(DbOperation.save(programStageSection));
                        //programStageSection.async().save();
                        for (ProgramStageDataElement programStageDataElement :
                                getProgramStageDataElementsBySection(programStage,programStageSection)) {
                            programStageDataElement.setProgramStageSection(programStageSection.getUid());
                            operations.add(DbOperation.save(programStageDataElement));
                            operations.addAll(saveDataElementAttributes(programStageDataElement.getDataElementObj(), attributes));
                        }
                        for (ProgramIndicator programIndicator : programStageSection.getProgramIndicators()) {
                            operations.add(DbOperation.save(programIndicator));

                            // relation to stage
                            operations.add(saveStageRelation(programIndicator, programStage.getUid()));

                            // relation to section
                            operations.add(saveStageRelation(programIndicator, programStageSection.getUid()));
                        }
                    }
                } else {
                    for (ProgramStageDataElement programStageDataElement : programStage.
                            getProgramStageDataElements()) {
                        operations.add(DbOperation.save(programStageDataElement));
                        operations.addAll(saveDataElementAttributes(programStageDataElement.getDataElement(), attributes));
                    }
                    for (ProgramIndicator programIndicator : programStage.getProgramIndicators()) {
                        operations.add(DbOperation.save(programIndicator));

                        // relation to stage
                        operations.add(saveStageRelation(programIndicator, programStage.getUid()));
                    }
                }
            }
        }
        return operations;
    }

    private static List<ProgramStageDataElement> getProgramStageDataElementsBySection(
            ProgramStage programStage, ProgramStageSection programStageSection) {

        List<ProgramStageDataElement> programStageDataElements = new ArrayList<>();

        if (programStageSection.getDataElements() != null){

            //v.2.27
            for (ProgramStageDataElement programStageDataElement:
                    programStage.getProgramStageDataElements()) {
                for (DataElement dataElement:programStageSection.getDataElements()){
                    if (dataElement.getUid().equals(programStageDataElement.getDataElement().getUid())){
                        programStageDataElements.add(programStageDataElement);
                    }
                }
            }

            return programStageDataElements;
        }else{
            //v.2.26
            return programStageSection.getProgramStageDataElements();
        }
    }


    private static List<DbOperation> update(Program program) {
        List<DbOperation> operations = new ArrayList<>();

        /**Delete everything (except shared things like dataElement and
         * trackedEntityAttribute) and store it again cause it's easier, it's not that big, and
         * it rarely happens.
         * what needs to be deleted is:
         * ProgramTrackedEntityAttribute (not the TrackedEntityAttribute itself)
         * ProgramStageDataElement (not the actual DataElement, we can simply update that)
         * The program stages since they are referenced with lazy loading
         *  but we need to delete the ProgramStageDataElements because it is the link
         *  between the program and dataelement
         */

        /*firstly we should get the old program from the database and delete using that
          reference
         */
        Program oldProgram = MetaDataController.getProgram(program.getUid());
        if (oldProgram != null) {
            for (ProgramTrackedEntityAttribute ptea : oldProgram.getProgramTrackedEntityAttributes()) {
                operations.add(DbOperation.delete(ptea));
            }
            for (ProgramStage programStage : program.getProgramStages()) {
                for (ProgramStageDataElement psde : programStage.getProgramStageDataElements()) {
                    operations.add(DbOperation.delete(psde));
                }
                for (ProgramStageSection programStageSection : programStage.getProgramStageSections()) {
                    operations.add(DbOperation.delete(programStageSection));
                }
                operations.add(DbOperation.delete(programStage));
            }
            for (ProgramIndicator programIndicator : program.getProgramIndicators()) {
                operations.add(DbOperation.delete(programIndicator));
            }
        }

        return operations;
    }

    private static DbOperation saveStageRelation(ProgramIndicator programIndicator, String programSection) {
            ProgramIndicatorToSectionRelationship stageRelation = new ProgramIndicatorToSectionRelationship();
            stageRelation.setProgramIndicator(programIndicator);
            stageRelation.setProgramSection(programSection);
            return DbOperation.save(stageRelation);
    }

    private static List<DbOperation> saveDataElementAttributes(DataElement dataElement, Map<String, Attribute> attributes){
        List<DbOperation> operations = new ArrayList<>();
        List<AttributeValue> attributeValues = dataElement.getAttributeValues();
        if (attributeValues!=null && !attributeValues.isEmpty()) {
            for (AttributeValue attributeValue : attributeValues) {
                attributeValue.setDataElement(dataElement.getUid());
                //Search for the attribute in the map, if not there, search for it in the DB, if not there create it
                operations.add(DbOperation.save(attributeValue));
                Attribute attribute = attributes.get(attributeValue.getAttributeId());
                if (attribute == null)
                    attribute = attributeValue.getAttributeObj();
                if (attribute == null)
                    attribute = attributeValue.getAttribute();
                attributes.put(attributeValue.getAttributeId(), attribute);
                operations.add(DbOperation.save(attribute));
            }
        }
        return operations;
    }
}
