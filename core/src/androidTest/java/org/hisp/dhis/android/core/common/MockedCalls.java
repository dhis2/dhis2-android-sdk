package org.hisp.dhis.android.core.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.user.User;

import java.io.IOException;

public class MockedCalls {

    public static final String DELETED_OBJECT_EMPTY =
            "deletedobject/deleted_object_empty.json";
    public static final String DELETED_OBJECT_USER =
            "deletedobject/deleted_object_user.json";
    public static final String DELETED_OBJECT_ORGANISATION_UNITS =
            "deletedobject/deleted_object_organisation_unit.json";
    public static final String DELETED_OBJECT_CATEGORIES =
            "deletedobject/deleted_object_categories.json";
    public static final String SYSTEM_INFO = "system_info.json";
    public static final String USER = "user.json";
    public static final String ADMIN_USER = "admin/user.json";
    public static final String ALTERNATIVE_USER = "deletedobject/alternative_user.json";
    public static final String MULTIPLE_ORGANISATIONN_UNITS = "deletedobject/multiple_organisationUnits.json";
    public static final String ORGANISATION_UNITS = "organisationUnits.json";
    public static final String ADMIN_ORGANISATION_UNITS = "admin/organisation_units.json";
    public static final String SIMPLE_CATEGORIES = "deletedobject/simple_categories.json";

    public static final String CATEGORIES = "categories.json";
    public static final String CATEGORY_COMBOS = "category_combos.json";
    public static final String PROGRAMS = "programs.json";
    public static final String TRACKED_ENTITIES = "tracked_entities.json";
    public static final String OPTION_SETS = "option_sets.json";
    public static final String AFTER_DELETE_EXPECTED_USER =
            "deletedobject/expected_multi_users.json";
    public static final String AFTER_DELETE_EXPECTED_ORGANISATION_UNIT =
            "deletedobject/expected_not_deleted_organisationUnit.json";
    public static final String AFTER_DELETE_EXPECTED_CATEGORIES =
            "deletedobject/simple_categories_after_delete.json";
    public static final String MULTI_USERS_EXPECTED =
            "deletedobject/expected_not_deleted_user.json";
    public static final String NORMAL_USER =
            "deletedobject/expected_normal_user.json";

    final static String[] commonMetadataJsonFiles = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_EMPTY, USER,
            DELETED_OBJECT_EMPTY, ORGANISATION_UNITS,
            DELETED_OBJECT_EMPTY, CATEGORIES,
            DELETED_OBJECT_EMPTY, CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY, PROGRAMS,
            DELETED_OBJECT_EMPTY, TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};
    final static String[] adminCommonMetadataJsonFiles = new String[]{
            SYSTEM_INFO,
            DELETED_OBJECT_EMPTY, ADMIN_USER,
            DELETED_OBJECT_EMPTY, ADMIN_ORGANISATION_UNITS,
            DELETED_OBJECT_EMPTY, CATEGORIES,
            DELETED_OBJECT_EMPTY, CATEGORY_COMBOS,
            DELETED_OBJECT_EMPTY, PROGRAMS,
            DELETED_OBJECT_EMPTY, TRACKED_ENTITIES,
            DELETED_OBJECT_EMPTY, OPTION_SETS};

    public static void givenAMetadataInDatabase(Dhis2MockServer dhis2MockServer)
            throws Exception {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(commonMetadataJsonFiles);
    }

    public static void givenAMetadataWithDescendantsInDatabase(Dhis2MockServer dhis2MockServer)
            throws IOException {
        dhis2MockServer.enqueueMockedResponsesFromArrayFiles(adminCommonMetadataJsonFiles);
    }
    public static Payload<User> parseUserResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<User>>() {
                });
    }

    public static Payload<OrganisationUnit> parseOrganisationUnitResponse(String file)
            throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<OrganisationUnit>>() {
                });
    }

    public static Payload<Category> parseCategoryResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<Category>>() {
                });
    }

    public static Payload<CategoryCombo> parseCategoryComboResponse(String file)
            throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<CategoryCombo>>() {
                });
    }

    public static Payload<Program> parseProgramResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<Program>>() {
                });
    }

    public static Payload<TrackedEntity> parseTrackedEntityResponse(String file)
            throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<TrackedEntity>>() {
                });
    }

    public static Payload<OptionSet> parseOptionSetResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<OptionSet>>() {
                });
    }

    public static Payload<Event> parseEventResponse(String file) throws IOException {
        String expectedResponseJson = new AssetsFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedResponseJson,
                new TypeReference<Payload<Event>>() {
                });
    }
}
