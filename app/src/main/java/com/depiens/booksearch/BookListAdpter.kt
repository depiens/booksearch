package com.depiens.booksearch

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_book.view.*

class BookListAdpter(fragment: BookListFragment) : RecyclerView.Adapter<BookListHolder>() {
    private val TAG = this@BookListAdpter::class.simpleName

    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

    private var parentFragment: BookListFragment? = null
    private var onBottomReachedListener: OnBottomReachedListener? = null
    private var listData: MutableList<BookInfo>? = null

    init {
        parentFragment = fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookListHolder(view)
    }

    override fun getItemCount(): Int {
        return listData?.size ?: 0
    }

    override fun onBindViewHolder(holder: BookListHolder, position: Int) {
        val bookInfo = listData?.get(position)

        bookInfo?.let {
            holder.setBookInfo(position, bookInfo)

            holder.itemView.setOnClickListener {
                parentFragment?.selectItem(position)
            }

            val lastIndex = (listData?.size ?: 0) - 1

            if (lastIndex == position) {
                onBottomReachedListener?.onBottomReached(position)
            }
        }
    }

    fun setListData(data: MutableList<BookInfo>) {
        this.listData = data
    }

    fun setOnBottomReachedListener(bottomReachedListener: OnBottomReachedListener) {
        onBottomReachedListener = bottomReachedListener
    }
}

class BookListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setBookInfo(position: Int, book: BookInfo) {
        itemView.textNo.text = "${book.no}"
        itemView.textTitle.text = book.title
        itemView.textAuthors.text = book.authors
        itemView.textPublisher.text = book.publisher

        if ( 0 == position % 2 ) {
            itemView.setBackgroundColor(Color.parseColor("#F6F6F6"))
        } else  {
            itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }
}