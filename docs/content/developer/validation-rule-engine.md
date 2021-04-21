# Validation rule engine { #android_sdk_validation_rule_engine }

Validation rules associated to a particular dataSet can be evaluated using the validation rule module. It only suppports the evaluation of validation rules in the context of a data entry form, i.e, validation rules that use data values contained in a particular combination of dataSet, period, organisationUnit and attributeOptionCombo.

> **Important**
>
> Currently it is not possible to evaluate validation rules acrross different dataSets, periods, organisationUnits or attributeOptionCombos.

```java
d2.validationModule()
    .validationEngine()
    .validate(<dataSet-uid>, <period-id>, <organisation-unit-uid>, <attribute-option-combo-uid>);
```

It returns a validation result containing the list of violations. Each violation includes helpful methods to get a human-readable representation of the conflict.
