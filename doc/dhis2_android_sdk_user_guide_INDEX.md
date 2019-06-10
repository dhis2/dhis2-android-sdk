<!--DHIS2-SECTION-ID:index-->

# Compatibility: SDK / core / Android

| SDK      | DHIS2 core       | Android SDK |
|----------|------------------|-------------|
| 0.17.0   | 2.30, 2.31, 2.32 | 19 - 28     |

# Overview

- Approach: the sdk is currently oriented to end-user apps (i.e, data encoders)
- Works offline
- 

# Getting started

## Installation

Maven dependency

## D2 initialization

# Workflow

Currently, the SDK is primarily oriented to build apps that work most of the time in an offline mode. In short, the SDK maintains a local database instance that is used to get all the work done (create forms, manage data, ...). From time to time, this local database instance is synchronized with the server.

A typical workflow would be like this:

1. Login
2. Sync metadata: the SDK the metadata so it is available to be used at any time. Metadata sync is totally user-dependent (see [Synchronization](...) for more details)
3. Download data:
4. Do the work: at this point the app is able to create the data entry forms and show some existing data. Then the user can edit/delete/update data.
5. Upload data: from time to time, the work done in the local database instance is sent to the server.
6. Sync metadata: it is recommended to sync metadata quite often to detect changes in metadata configuration.

## Metadata synchronization

What is synced?

Corrupted configurations

# Module architecture

## Filters

## Nested fields

# Error management

# SMS module

# DHIS2 version compatibility strategy

# Program indicator engine

The SDK includes its own Program Indicator engine for the evaluation of **in-line Program Indicators**. These kind of indicators are evaluated within the context of an enrollment and they are usually placed in the data entry form offering additional information to the data encoder. This means that, even though they are regular Program Indicators and can be calculated across enrollments, they have provide useful information within a single enrollment.

A good example, "Average time between visits".

A bad example, "Number of active TEIs": it would always be 1.

In order to trigger the Program Indicator Engine, just execute:

```
d2.programModule()
    .programIndicatorEngine
    .getProgramIndicatorValue(<enrollment-uid>, <event-uid>, <program-indicator-uid>);
```

Compatibility table:

| Function (d2:)(doc)| Supported |
|--------------------|-----------|
| ceil              |   Yes     |
| floor             |   Yes     |
| round             |   Yes     |
| modulus           |   Yes     |
| zing              |   Yes     |
| oizp              |   Yes     |
| concatenate       |   Yes     |
| condition         |   Yes     |
| minutesBetween    |   No      |
| daysBetween       |   Yes     |
| monthsBetween     |   Yes     |
| yearsBetween      |   Yes     |
| relationshipCount |   No      |
| count             |   No      |
| countIfValue      |   No      |
| countIfZeroPos    |   No doc  |
| hasValue          |   No      |
| zpvc              |   Yes     |
| validatePatten    |   Yes     |
| left              |   Yes     |
| right             |   Yes     |
| substring         |   Yes     |
| split             |   Yes     |
| length            |   Yes     |
| inOrgUnitGroup    |   No doc  |
| hasUserRole       |   No doc  |

| Variables (doc)       | Supported |
|-----------------------|-----------|
| current_date          | Yes       |
| event_date            | Yes       |
| due_date              | Yes       |
| event_count           | Yes       |
| enrollment_date       | Yes       |
| incident_date         | Yes       |
| program_stage_id      | No        |
| program_stage_name    | No        |
| enrollment_status     | Yes       |
| value_count           | Yes       |
| zero_pos_value_count  | Yes       |
| reporting_period_start| N/A       |
| reporting_period_end  | N/A       |
| tei_count             | N/A       |
| enrollment_count      | N/A       |
| organisationunit_count| N/A       |
