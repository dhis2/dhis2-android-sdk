# Object style { #android_sdk_object_style }

Some elements include a property called "style" that defines an icon and a color. This visual information is very useful to quickly navigate through the application. Some typical use cases:

- Distinguish different programs and trackedEntityTypes in a program list.
- In data entry forms with an optionSet, show the options with icons and colors instead of names.
- Different programStages within a program.

This property is optional and it is not defined in most of the cases. An object style might have an icon, or a color, or both.

## Icon

The set of icons used in DHIS2 are included in the SDK. They are located within the resources, in the "drawable" folder. Currently they are predefined and cannot be customized.

```java

// Illustrative code to get the resource id
if (program.style().icon() != null) {
    String iconName = program.style().icon();
    int resourceId = getResources().getIdentifier(iconName, "drawable", getPackageName());
}
```

## Color

It contains the Hex value for the color. It can be used to customize the background, text color, line headings, etc.

```java
program.style().color();    // For example #9C33FF

```
