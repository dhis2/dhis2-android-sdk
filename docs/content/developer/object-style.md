# Object style { #android_sdk_object_style }

Some elements include a property called "style" that defines an icon and a color. This visual information is very useful to quickly navigate through the application. Some typical use cases:

- Distinguish different programs and trackedEntityTypes in a program list.
- In data entry forms with an optionSet, show the options with icons and colors instead of names.
- Different programStages within a program.

This property is optional and it is not defined in most of the cases. An object style might have an icon, or a color, or both.

## Icon

DHIS2 includes a predefined set of icons. Those icons are included in the SDK and are located within the resources, in the "drawable" folder.

Staring on version v41, it is possible to upload user-defined icons and assign them to metadata objects. The actual images of these icons are stored as a FileResource and must be explicitly downloaded in a separate query.

```kt
d2.fileResourceModule().fileResourceDownloader()
    .byDomainType().eq(FileResourceDomainType.ICON)
    .download()
```

You can get the information about a particular icon by using the IconCollectionRepository. It returns an Icon object, which is a sealed class with two possible values: Default or Custom.  

The way to render the actual image will depend on the Icon type.

```kt

val icon = d2.iconModule().icons().key("icon_key").blockingGet()

icon?.let {
    when (icon) {
        is Icon.Custom -> {
            val path = icon.path
            val fileResourceUid = icon.fileResourceUid
            // Use this information to load the file
        }

        is Icon.Default -> {
            val resourceName = icon.key
            // Use this information to load the resource
        }
    }
}
```

## Color

It contains the Hex value for the color. It can be used to customize the background, text color, line headings, etc.

```kt
program.style().color()    // For example #9C33FF
```
