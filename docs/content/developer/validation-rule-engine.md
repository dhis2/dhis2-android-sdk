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

## Complete only if validation passes

DataSets can be configured with the property `validCompleteOnly` ("Complete only if validation passes"), which means that the dataSet instance must not be marked as complete if any validation rule is violated. The dataSet instance service evaluates this configuration and returns the completion status, including the violations found:

```java
d2.dataSetModule()
    .dataSetInstanceService()
    .getCompletionStatus(<dataSet-uid>, <period-id>, <organisation-unit-uid>, <attribute-option-combo-uid>);
```

If the dataSet is not configured with `validCompleteOnly`, the validation rules are not evaluated and the status is always `Completable`. The SDK does not block the completion of a dataSet instance, so the app is responsible for checking this status before marking the dataSet instance as complete.

In order to know in advance if the validation rules must be evaluated, and if they are mandatory to complete the dataSet instance, the same service exposes the validation rules configuration of the dataSet:

```java
d2.dataSetModule()
    .dataSetInstanceService()
    .getValidationRulesConfiguration(<dataSet-uid>);
```

It returns `MANDATORY` if the dataSet has validation rules to evaluate in the form and it is configured with `validCompleteOnly`, `OPTIONAL` if it has validation rules but the instance can be completed anyway, and `NONE` if there is no validation rule to evaluate. A typical data entry form uses this configuration to decide whether to run the validation rules silently, to offer the user to run them, or to skip them.
