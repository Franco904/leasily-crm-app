package com.fstengineering.leasilycrm.core.presentation.utils.models

/**
Creates a presentation representation of a domain aggregate field, distinguishing it in:

- [NotApplicable] : This field is not applicable due to conditional rules or the
contract version in effect when the aggregate was registered/updated did not support this field.
It should not be rendered in the UI.
- [Applicable] : The user could intentionally provide this field. It should be rendered in the UI, but with an empty value if null.
 */
sealed interface FieldValueState<out T> {
    data object NotApplicable : FieldValueState<Nothing>
    data class Applicable<T>(val value: T?) : FieldValueState<T>

    val valueOrNull: T? get() = (this as? Applicable)?.value
}
