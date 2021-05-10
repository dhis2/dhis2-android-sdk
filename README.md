# DHIS2 Android SDK

[![Build Status](https://api.travis-ci.com/dhis2/dhis2-android-sdk.svg?branch=master)](https://travis-ci.com/dhis2/dhis2-android-sdk)

## Introduction

DHIS2 Android SDK is a library that abstracts the complexity of interacting with DHIS2 web api. It aims to be an starting point to build Android apps for DHIS2, covering some tasks that any Android app should implement, like metadata and data synchronization.

Main goals:

- **Abstract DHIS2 web api**. There is no need to perform api queries against the server. The SDK includes methods to interact with the web api.
- **Work offline**. It implements a simplified version of DHIS2 model that is persisted in a local database (SQLite). It ensures that all the metadata required to perform data entry tasks is available at any time to build the data entry forms. Data is saved locally and upload to the server when connectivity is available.
- **Ensure DHIS2 compatibility**. It encapsulates the changes between DHIS2 versions so the app does not have to care about them. In case the SDK introduces some changes to accommodate a new DHIS2 version, the app can safely detect these changes at compile-time.

## Documentation

Developer-oriented documentation can be found in [Documentation section](https://docs.dhis2.org/en/develop/developing-with-the-android-sdk/about-this-guide.html) in DHIS2 web. It is intended to be used by developers.

## Examples

Code examples can be found in the [Android Skeleton app](https://github.com/dhis2/dhis2-android-skeleton-app). This app contains different branches showing different use cases of the SDK.

## Community

Community support can be found in [Android SDK Development](https://community.dhis2.org/c/development/sdk-android-development) category in the community portal. Any feedback on the SDK will be highly appreciated.

To report bugs or request new features, there is project in DHIS2 Jira named [ANDROSDK](https://jira.dhis2.org/projects/ANDROSDK/issues). Please do not hesitate to create an issue with your request.
