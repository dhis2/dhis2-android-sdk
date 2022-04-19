# Program rule engine { #android_sdk_program_rule_engine }

The program rule engine is not provided within the SDK. It is implemented in a separate library, so the same code is used by backend and android apps.

> **Important**
>
> The DHIS2 Android SDK and the Program Rule Engine use a common library to parse the DHIS2 expressions, the [DHIS2 Antlr Expression Parser](https://github.com/dhis2/dhis2-antlr-expression-parser). If the application imports both the the SDK and the Rule Engine, it is important that their parser versions match exactly; otherwise it could cause an unexpected behavior in the evaluation.
>
> Check compatibility [here](#android_sdk_compatibility).

More info [dhis2-rule-engine](https://github.com/dhis2/dhis2-rule-engine).
