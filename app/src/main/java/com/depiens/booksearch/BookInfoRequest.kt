package com.depiens.booksearch

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class BookInfoRequest {
    private val TAG = this@BookInfoRequest::class.simpleName

    interface OnBookInfoResponseListener {
        fun onBookInfoResponseBookInfo(jsonObj: JSONObject)
    }

    private var onBookInfoResponseListener: OnBookInfoResponseListener? = null

    private val REST_API = "https://dapi.kakao.com/v3/search"
    private val BOOK_REST_API = "$REST_API/book"

    private val AUTH_KEY_NAME         = "Authorization"
    private val CONTENT_TYPE_KEY_NAME = "Content-Type"
    private val CHARSET_KEY_NAME      = "charset"

    private val REST_API_KEY          = "KakaoAK 8c4662a5eb3f7c2953df8b2e54c11e0d"
    private val CONTENT_TYPE_VALUE    = "application/x-www-form-urlencoded"
    private val CHARSET_KEY_VALUE     = "utf-8"
    private val CONNECT_TIMEOUT       = 15000
    private val READ_TIMEOUT          = 10000

    private var keyword = ""
    private var sort    = "accuracy"
    private var page    = 1
    private var size    = 50

    fun setOnBookInfoResponseListener(listener: OnBookInfoResponseListener) {
        this.onBookInfoResponseListener = listener
    }

    fun setKeyword(keyword: String): BookInfoRequest {
        this.keyword = keyword
        return this
    }

    fun setSort(sort: String): BookInfoRequest {
        this.sort = sort
        return this
    }

    fun setPage(page: Int): BookInfoRequest {
        this.page = page
        return this
    }

    fun setSize(size: Int): BookInfoRequest {
        this.size = size
        return this
    }

    fun getSize(): Int {
        return size
    }

    fun request() {
        Thread {
            val query     = "sort=${sort}&page=${page}&size=${size}&query=${keyword}"
            val urlString = "${BOOK_REST_API}?${query}"
            Log.d(TAG, "REQUEST URL = $urlString")

            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection

                conn.setRequestProperty(AUTH_KEY_NAME, REST_API_KEY)
                conn.setRequestProperty(CONTENT_TYPE_KEY_NAME, CONTENT_TYPE_VALUE)
                conn.setRequestProperty(CHARSET_KEY_NAME, CHARSET_KEY_VALUE)
                conn.connectTimeout = CONNECT_TIMEOUT
                conn.readTimeout    = READ_TIMEOUT
                conn.requestMethod  = "GET"

                if (HttpURLConnection.HTTP_OK == conn.responseCode) {
                    val streamReader = InputStreamReader(conn.inputStream)
                    val buffered = BufferedReader(streamReader)

                    val content = StringBuilder()
                    while (true) {
                        val line = buffered.readLine() ?: break
                        content.append(line)
                    }

                    buffered.close()
                    conn.disconnect()

                    val jsonText = content.toString()
                    val jsonObj = JSONObject(jsonText.substring(jsonText.indexOf("{"), jsonText.lastIndexOf("}") + 1))

                    onBookInfoResponseListener?.onBookInfoResponseBookInfo(jsonObj)
                } else {
                    Log.d(TAG, "RESPONSE CODE = ${conn.responseCode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}