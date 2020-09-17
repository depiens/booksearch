package com.depiens.booksearch

data class BookInfo(val no: Int,
                    val title: String,
                    val contents: String,
                    val authors: String,
                    val publisher: String,
                    val date: String,
                    val price: Int,
                    val thumbnail: String)