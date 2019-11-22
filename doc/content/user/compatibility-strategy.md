# DHIS2 version compatibility strategy

<!--DHIS2-SECTION-ID:compatibility_strategy-->

The SDK guarantees compatibility with the latest three DHIS 2 releases. In case the SDK is still compatible with previous DHIS2 versions and no major issues have been detected, compatibility could be extended to previous versions.

In order to avoid accidental login into unsupported DHIS 2 instances, the SDK blocks connections to version that are not supported yet or that have been deprecated.

Regarding data model and compatibility, the main approach is to extend the data model to be able to support all the DHIS 2 versions. It usually happens that new DHIS 2 versions introduce extra functionality and do not remove existing one, so supporting a new DHIS 2 version usually means to use the latest data model.

As a general rule the SDK tries to avoid breaking changes in its API and new features are optional to the user. This rule is followed as much as possible, but there are cases where supporting old and new APIs to avoid breaking has a very high cost. In this scenario the **SDK might introduce breaking changes to be compatible with the new DHIS 2 version**.

## Example: minor change

...

## Example: breaking change

...
