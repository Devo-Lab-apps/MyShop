package com.labs.devo.apps.myshop.util.exceptions

data class PageNotFoundException(val msg: String = "The page is not present and maybe deleted by another user.") :
    RuntimeException(msg)

data class PageLimitExceededException(val msg: String = "You can't have more than 50 pages per notebook.") :
    RuntimeException(msg)

data class NoPagesForNotebookException(val msg: String = "No pages for the selected notebook") :
    RuntimeException(msg)

data class NoPagesToCreateException(val msg: String = "No pages to create.") : RuntimeException(msg)