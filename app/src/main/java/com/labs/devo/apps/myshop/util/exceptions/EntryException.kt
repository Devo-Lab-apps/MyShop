package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException

data class NoEntryException(val msg: String = "No entries found to be inserted"): RuntimeException(msg)

data class EntryNotFoundException(val msg: String = "Entry not found."): RuntimeException(msg)