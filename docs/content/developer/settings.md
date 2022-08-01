# Settings { #android_sdk_settings }

Settings are downloaded on every metadata synchronization. There are different kinds of settings:

```java
d2.settingModule()
```

- **System settings**: system-wide properties such as `flag` or `style`.
- **User settings**: user specific settings such as `keyDbLocale` or `keyUiLocale`.
- **Settings app**: these settings offer additional control over the behavior of the application. More about this in the next section.



## Settings app { #android_sdk_settings_app }

The DHIS2 instance might include a web application called "Android Settings" that allow to have remote control over certain parameters in the application. The installation and configuration of this application is optional.

This SDK downloads this configuration in every metadata synchronization and persist it in the database. Some of these parameters are automatically consumed by the SDK (they are marked in bold below), although most of them might be overridden by the app.

### General settings { #android_sdk_general_settings }

```java
d2.settingModule().generalSetting()
```

It gives additional information about app settings:

- **Encrypt database**: whether or not to encrypt local database.
- **Reserved values**: number of attribute values to reserve. It might be overridden by the app.
- Mobile configuration: gateway number, result sender number. They must be consumed by the application and used to configure the SMS module in the SDK.
- Matomo configuration: if you have your own Matomo instance, you can expose this information to the app in order to configure its Matomo client.
- AllowScreenCapture: parameter to determine if the application should allow screen capture or not.
- MessageOfTheDay: a message to show to the users.

### Synchronization settings { #android_sdk_synchronization_settings }

```java
d2.settingModule().synchronizationSettings()
```

If offers additional parameters to control metadata/data synchronization.

- MetadataSync, DataSync: these two parameters define the periodicity of metadata/data sync. They must be used by the app to create scheduled jobs.
- **TrackerImporterVersion:** version of the tracker importer: *V1* refers to the legacy tracker importer (`/api/trackedEntityInstances` endpoint); *V2* refers to the importer introduced in 2.37 (`/api/tracker` endpoint).
- **ProgramSettings:** this section controls the program data synchronization parameters. It has a section to define global or default parameters to be used in the synchronization of all programs. Additionally it allows to set specific settings for particular programs. All these parameters are consumed by the SDK and used in the synchronization process.
- **DataSetsSettings:** this section controls the aggregated data synchronization parameters. It has a section to define global or default parameters to be used in the synchronization of all dataSets. Additionally it allows to set specific setting for particular dataSets. All these parameters are consumed by the SDK and used in the synchronization process.

### Appearance settings { #android_sdk_appearance_settings }

```java
d2.settingModule().appearanceSettings()
```

These settings give control over the appearance of the data entry form. 

- FilterSorting: it defines the filters that must be enabled in the different app menus.
- ProgramConfiguration: it defines two properties for programs.
    - CompletionSpinner: show/hide the completion spinner.
    - OptionalSearch: it defines if searching is mandatory or not before creating data.

These settings refer to visual components so they must be consumed by the app.

### Analytic settings { #android_sdk_analytic_settings }

```java
d2.settingModule().analyticsSetting()

d2.settingModule().analyticsSetting().teis()

d2.settingModule().analyticsSetting().dhisVisualizations()
```

Analytics settings define the analytic elements (charts, tables,...) that must be displayed to the user.

- **teis collection:** they define analytic elements referred to the context of a single TEI. These elements are intended to be displayed in a TEI dashboard.
- **dhisVisualizations:** they are organized into three sections (home, program, dataSet) and each section is composed of a list of groups. Each group contains a list of visualizations. About the sections:
    - *home:* those visualizations that must be displayed in the *home* screen.
    - *program:* map of objects with the key being a programId. These visualizations are intended to be displayed in the context of a particular program.
    - *dataSet:* map of objects with the key being a dataSetId. These visualizations are intended to be displayed in the context of a particular dataSet.


These settings refer to visual components so they must be consumed by the app.