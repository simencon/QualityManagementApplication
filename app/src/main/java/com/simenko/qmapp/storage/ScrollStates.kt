package com.simenko.qmapp.storage

enum class ScrollStates(val indexKey: String, val offsetKey: String) {
    DEPARTMENTS("DEPARTMENTS_LIST_INDEX", "DEPARTMENTS_LIST_OFFSET"),
    LINES("LINES_LIST_INDEX", "LINES_LIST_OFFSET")
}