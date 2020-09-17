package com.depiens.booksearch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_book_detail.view.*
import java.lang.Exception
import java.net.URL
import java.text.NumberFormat
import java.util.*

class BookDetailFragment : Fragment() {
    private val TAG = this@BookDetailFragment::class.simpleName

    private var mainActivity: MainActivity? = null
    private var bookInfo: BookInfo? = null
    private var imageView: ImageView? = null
    private var imgProgressBar: ProgressBar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_book_detail, container, false)
        view.textTitle.text = bookInfo?.title
        view.textAuthors.text = bookInfo?.authors
        view.textPublisher.text = bookInfo?.publisher
        view.textContents.text = bookInfo?.contents

        val currencyAmount = NumberFormat.getCurrencyInstance(Locale("ko", "KR"))
        view.textPrice.text = currencyAmount.format(bookInfo?.price ?: 0)

        Glide.with(view).load(bookInfo?.thumbnail).into(view.imageView.imageView)
        view.imgProgressBar.visibility = View.GONE

        /*
        imageView      = view.imageView
        imgProgressBar = view.imgProgressBar

        downloadThumbnailImage()
        */

        return view
    }

    fun setBookInfo(bookInfo: BookInfo) {
        this.bookInfo = bookInfo
    }

    private fun downloadThumbnailImage() {
        val asyncTask = object: AsyncTask<String, Void, Bitmap?>() {
            override fun doInBackground(vararg params: String?): Bitmap? {
                val urlString = params[0]!!
                try {
                    val url    = URL(urlString)
                    val stream = url.openStream()

                    return BitmapFactory.decodeStream(stream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)

                when (result) {
                    null -> Toast.makeText(context, getString(R.string.fail_image_download), Toast.LENGTH_LONG).show()
                    else -> imageView?.setImageBitmap(result)
                }

                imgProgressBar?.visibility = View.GONE
            }
        }

        imgProgressBar?.visibility = View.VISIBLE

        asyncTask.execute(bookInfo?.thumbnail)
    }
}