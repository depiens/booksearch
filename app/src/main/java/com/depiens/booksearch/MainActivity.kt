package com.depiens.booksearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private val TAG = this@MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFragment()
    }

    private fun setFragment() {
        val listFragment = BookListFragment()

        val tran = supportFragmentManager.beginTransaction()
        tran.add(R.id.frameLayout, listFragment)
        tran.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        goBack()
        return true
    }

    fun goDetial(bookInfo: BookInfo) {
        val detailFragment = BookDetailFragment()
        detailFragment.setBookInfo(bookInfo)

        val tran = supportFragmentManager.beginTransaction()
        tran.add(R.id.frameLayout, detailFragment)
        tran.addToBackStack("detail")
        tran.commit()

        supportActionBar?.title = getString(R.string.detail_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun goBack() {
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        onBackPressed()
    }
}
