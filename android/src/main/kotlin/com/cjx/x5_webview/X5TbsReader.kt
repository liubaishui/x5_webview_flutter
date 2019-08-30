package com.cjx.x5_webview

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tencent.smtt.sdk.TbsReaderView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class X5TbsReader(private val context: Context, val id: Int, val params: Map<String, Any>, val messenger: BinaryMessenger? = null) : PlatformView, MethodChannel.MethodCallHandler {
    private val mTbsReaderView: TbsReaderView
    private val channel: MethodChannel = MethodChannel(messenger, "com.cjx/x5TbsReader_$id")

    init {
        channel.setMethodCallHandler(this)
        mTbsReaderView = TbsReaderView(context,
                TbsReaderView.ReaderCallback{ p0, p1, p2 -> Log.d("X5TbsReader","文件浏览服务日志输出:$p0 $p1 $p2")})

        val filePath = params["filePath"].toString()
        val bundle = Bundle()
        bundle.putString("filePath", filePath)
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().path)
        val result = mTbsReaderView.preOpen(parseFormat(filePath), false)
        if (result) {
            mTbsReaderView.openFile(bundle)
        }else{
            Toast.makeText(context, "不支持的文件类型", Toast.LENGTH_LONG).show()
        }


    }

    override fun getView(): View {
        return mTbsReaderView
    }

    override fun dispose() {
        channel.setMethodCallHandler(null)
        mTbsReaderView.onStop()
    }

    private fun parseFormat(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf(".") + 1)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "loadFile" -> {
                val arg = call.arguments as Map<String, Any>
                val filePath = arg["filePath"].toString()
                val bundle = Bundle()
                bundle.putString("filePath", filePath)
                bundle.putString("tempPath", Environment.getExternalStorageDirectory().path)
                val result2 = mTbsReaderView.preOpen(parseFormat(filePath), false)
                if (result2) {
                    mTbsReaderView.openFile(bundle)
                    result.success(null)
                }else{
                    Toast.makeText(context, "不支持的文件类型", Toast.LENGTH_LONG).show()
                    result.success("不支持的文件类型")
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }
}