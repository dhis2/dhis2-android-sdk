# DHIS2 version compatibility strategy { #android_sdk_compatibility_strategy }

The SDK guarantees compatibility with the latest three DHIS 2 releases (see [Compatibility](#android_sdk_compatibility)). In case the SDK is still compatible with previous DHIS2 versions and no major issues have been detected, compatibility could be extended to previous versions.

In order to avoid accidental login into unsupported DHIS 2 instances, the SDK blocks connections to version that are not supported yet or that have been deprecated.

Regarding data model and compatibility, the main approach is to extend the data model to be able to support all the DHIS 2 versions. It usually happens that new DHIS 2 versions introduce extra functionality and do not remove existing one, so supporting a new DHIS 2 version usually means to use the latest data model.

As a general rule the SDK tries to avoid breaking changes in its API and new features are optional to the user. This rule is followed as much as possible, but there are cases where supporting old and new APIs to avoid breaking has a very high cost. In this scenario the **SDK might introduce breaking changes to be compatible with the new DHIS 2 version**.

Here you can find a few examples of changes in the SDK and the effect in the app.

## Example: minor change

Until version 2.30, Program model had a boolean attribute called "captureCoordinates". This attribute indicates if coordinates (point) must be stored in that program. As of 2.30, this attribute was replaced by "featureType" with 4 possible values: NONE, POINT, POLYGON, MULTI_POLYGON.

*Changes in the SDK:*

As of 2.30, the SDK uses the attribute "featureType". If the server version is lower than 2.30, the SDK maps the "captureCoordinates" value to:

- false - NONE
- true - POINT

*Changes in the app:*

The app is now force to use "featureType". Modifications in the code are quite straightforward.

## Example: major change

As of 2.30, Relationship model suffered from a deep refactor in order to allow relationships between event, enrollment and trackedEntityInstances. The SDK adopted the model for 2.30 and exposes this model to the app. When interacting with the API, the SDK translates between both models internally.

*Changes in the app:*

This change implies that the app must adopt a different model and changes are not so straightforward.
