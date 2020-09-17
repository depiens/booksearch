package com.depiens.booksearch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_book_list.view.*
import org.json.JSONObject

class BookListFragment : Fragment(), BookInfoRequest.OnBookInfoResponseListener {
    private val TAG = this@BookListFragment::class.simpleName

    private var mainActivity: MainActivity? = null
    private var request: BookInfoRequest?   = null
    private var adapter: BookListAdpter?    = null
    private var editSearch: EditText?       = null
    private var progressBar: ProgressBar?   = null

    private val listData: MutableList<BookInfo> = mutableListOf()
    private var pageTotalCount = 0
    private var pageIndex      = 1
    private var prevKeyword    = ""

    private val DATA_NOT_FOUND = -1
    private val DATA_REFRESH   = 0

    private val KEY_META           = "meta"
    private val KEY_PAGEABLE_COUNT = "pageable_count"
    private val KEY_DOCUMENTS      = "documents"
    private val KEY_TITLE          = "title"
    private val KEY_CONTENTS       = "contents"
    private val KEY_AUTHORS        = "authors"
    private val KEY_PUBLISHER      = "publisher"
    private val KEY_DATETIME       = "datetime"
    private val KEY_PRICE          = "price"
    private val KEY_THUMBNAIL      = "thumbnail"

    private val handler = object: Handler() {
        override fun handleMessage(msg: Message) {
            Log.d(TAG, "handleMessage01 pageIndex = ${pageIndex}, dataCount = ${listData.size}")

            when(msg.what) {
                DATA_REFRESH -> {
                    val startPos = (request?.getSize() ?: 0) * (pageIndex - 1)
                    when(startPos) {
                        0 -> adapter?.notifyDataSetChanged()
                        else -> {
                            val itemCnt = listData.size - startPos
                            adapter?.notifyItemRangeInserted(startPos, itemCnt);
                        }
                    }
                }
                DATA_NOT_FOUND -> { Toast.makeText(context, getString(R.string.data_not_found), Toast.LENGTH_LONG).show() }
            }

            showProgress(View.GONE)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
        request     = BookInfoRequest()

        request?.setOnBookInfoResponseListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        view.editSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    hideKeypad()
                    search()
                    true
                }
                else -> false
            }
        }

        editSearch  = view.editSearch
        progressBar = view.progressBar

        view.btnSearch.setOnClickListener {
            hideKeypad()
            search()
        }

        adapter = BookListAdpter(this)
        adapter?.setListData(listData)
        adapter?.setOnBottomReachedListener(object : BookListAdpter.OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                if ( pageIndex < pageTotalCount ) {
                    pageIndex++
                    search(true)
                }
            }
        })

        view.listView.adapter = adapter
        view.listView.layoutManager = LinearLayoutManager(mainActivity)

        return view
    }

    override fun onBookInfoResponseBookInfo(jsonObj: JSONObject) {
        val metaJson = jsonObj.getJSONObject(KEY_META)
        pageTotalCount = metaJson.getInt(KEY_PAGEABLE_COUNT)

        Log.d(TAG, "onBookInfoResponseBookInfo pageTotalCount = ${pageTotalCount}")

        if ( 0 == pageTotalCount ) {
            Thread.sleep(500)
            handler.sendEmptyMessage(DATA_NOT_FOUND)
        } else {
            val dataCnt  = listData.size
            val docsJson = jsonObj.getJSONArray(KEY_DOCUMENTS)

            var item: JSONObject?

            var title: String
            var contents: String
            var authors = ""
            var publisher: String
            var date: String
            var price: Int
            var thumbnail: String

            for (idxDocs in 0 until docsJson.length()) {
                item = docsJson.getJSONObject(idxDocs)

                title     = item?.getString(KEY_TITLE) ?: ""
                contents  = item?.getString(KEY_CONTENTS) ?: ""
                publisher = item?.getString(KEY_PUBLISHER) ?: ""
                date      = item?.getString(KEY_DATETIME) ?: ""
                price     = item?.getInt(KEY_PRICE) ?: 0
                thumbnail = item?.getString(KEY_THUMBNAIL) ?: ""

                if ( 10 < date.length ) {
                    date = date.substring(0, 9)
                }
                val authorsJson = item?.getJSONArray(KEY_AUTHORS)
                val authorCnt = authorsJson?.length() ?: 0
                for (idxAuthor in 0 until authorCnt) {
                    authors = when (idxAuthor) {
                        0 -> authorsJson?.getString(idxAuthor) ?: ""
                        else -> "${authors}, ${authorsJson?.getString(idxAuthor)}"
                    }
                }

                val bookInfo = BookInfo(
                    dataCnt + idxDocs + 1,
                    title,
                    contents,
                    authors,
                    publisher,
                    date,
                    price,
                    thumbnail
                )

                listData.add(bookInfo)
            }

            Log.d(TAG, "onBookInfoResponseBookInfo data process end!!")

            if (0 < docsJson.length()) {
                Thread.sleep(1000)
                handler.sendEmptyMessage(DATA_REFRESH)
            }
        }
    }

    private fun hideKeypad() {
        editSearch?.clearFocus()
        val imm = editSearch?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editSearch?.windowToken, 0)
    }

    fun selectItem(position: Int) {
        mainActivity?.goDetial(listData[position])
    }

    fun search(isNext: Boolean = false) {
        val keyword = editSearch?.text.toString()

        if ( 0 != prevKeyword.compareTo(keyword) || (0 == prevKeyword.compareTo(keyword) && !isNext) ) {
            clear()
        }

        when {
            keyword.isNotEmpty() -> {
                showProgress(View.VISIBLE)

                request
                    ?.setPage(pageIndex)
                    ?.setKeyword(keyword)
                    ?.request()
            }
            else -> clear()
        }

        prevKeyword = keyword
    }

    private fun clear() {
        pageIndex = 1
        val size = listData.size
        listData.clear()
        adapter?.notifyItemRangeRemoved(0, size)
    }

    fun showProgress(visibility: Int) {

        progressBar?.visibility = visibility
    }
}