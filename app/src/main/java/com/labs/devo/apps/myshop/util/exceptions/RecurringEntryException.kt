package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException


data class NoRecurringEntryException(val msg: String = "No recurring entries found to be inserted"): RuntimeException(msg)

data class RecurringEntryNotFoundException(val msg: String = "Recurring Entry not found."): RuntimeException(msg)