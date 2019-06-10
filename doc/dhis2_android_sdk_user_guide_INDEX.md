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
