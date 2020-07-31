# Settings

<!--DHIS2-SECTION-ID:settings-->

Settings are downloaded on every metadata synchronization. There are different kinds of settings:

```java
d2.settingModule()
```

- **System settings**: system-wide properties such as `flag` or `style`.
- **User settings**: user specific settings such as `keyDbLocale` or `keyUiLocale`.
- **Settings app**: these settings offer additional control over the behavior of the application. More about this in the next section.



## Settings app

<!--DHIS2-SECTION-ID:settings_app-->

The DHIS2 instance might include a web application called "Android Settings" that allow to have remote control over certain parameters in the application. The installation and configuration of this application is optional.

This SDK downloads this configuration in every metadata synchronization and persist it in the database. Some of these parameters are automatically consumed by the SDK (in bold).

General:

- Metadata/data sync frequency: this value must be consumed by the application and used to trigger the synchronization in the SDK.
- Mobile configuration: gateway number, sender number. They must be consumed by the application and used to configure the SMS module in the SDK.
- **Reserved values**: number of attribute values to reserve.
- **Encrypt database**: whether or not to encrypt local database.

**Programs:** this section controls the program data synchronization parameters. It has a section to define global or default parameters to be used in the synchronization of all programs. Additionally it allows to set specific settings for particular programs. All these parameters are consumed by the SDK and used in the synchronization process.

**DataSets:** this section controls the aggregated data synchronization parameters. It has a section to define global or default parameters to be used in the synchronization of all dataSets. Additionally it allows to set specific setting for particular dataSets. All these parameters are consumed by the SDK and used in the synchronization process.

```java
// General settings
d2.settingModule().generalSetting().get();

// Program settings
d2.settingModule().programSetting().get();

// DataSet settings
d2.settingModule().dataSetSetting().get();
```

Although these parameters are automatically consumed by the SDK, the application might override some of those values in the synchronization process. For example, it might define a different TEI or event limit or a different download strategy (limitByOrgUnit, limitByProgram).