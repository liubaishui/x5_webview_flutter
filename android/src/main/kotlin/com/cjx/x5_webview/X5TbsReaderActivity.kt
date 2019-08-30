package com.cjx.x5_webview

import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.PixelFormat
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

class X5TbsReaderActivity : Activity(), TbsReaderView.ReaderCallback  {
    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var mTbsReaderView: TbsReaderView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR)
        window.setFormat(PixelFormat.TRANSLUCENT)
        mTbsReaderView = TbsReaderView(this, this)
        setContentView(mTbsReaderView)
        initView()
    }

    override fun onDestroy() {
        mTbsReaderView.onStop()
        super.onDestroy()
    }

    private fun initView() {
        title = intent.getStringExtra("title") ?: ""
        var filePath = intent.getStringExtra("filePath")
        val bundle = Bundle()
        bundle.putString("filePath", filePath)
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().path)
        val result = mTbsReaderView.preOpen(parseFormat(filePath), false)
        if (result) {
            mTbsReaderView.openFile(bundle)
        }else{
            Toast.makeText(this, "不支持的文件类型", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun parseFormat(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf(".") + 1)
    }
}