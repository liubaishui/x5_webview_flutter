package com.cjx.x5_webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.sdk.TbsVideo
import com.tencent.smtt.sdk.WebView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File

class X5WebViewPlugin(var context: Context, var activity: Activity) : MethodCallHandler {
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "com.cjx/x5Video")
            channel.setMethodCallHandler(X5WebViewPlugin(registrar.context(), registrar.activity()))
            setCallBack(channel, registrar.activity() )

            registrar.platformViewRegistry().registerViewFactory("com.cjx/x5WebView", X5WebViewFactory(registrar.messenger(), registrar.activeContext()))
            registrar.platformViewRegistry().registerViewFactory("com.cjx/x5TbsReader", X5TbsReaderFactory(registrar.messenger(), registrar.activeContext()))

        }

        private fun setCallBack(channel: MethodChannel, activity: Activity) {
            QbSdk.setTbsListener(object : TbsListener {
                override fun onInstallFinish(p0: Int) {
                    activity.runOnUiThread {
                        channel.invokeMethod("onInstallFinish", null)
                    }
                }

                override fun onDownloadFinish(p0: Int) {
                    activity.runOnUiThread {
                        channel.invokeMethod("onDownloadFinish", null)
                    }
                }

                override fun onDownloadProgress(p0: Int) {
                    activity.runOnUiThread {
                        channel.invokeMethod("onDownloadProgress", p0)
                    }
                }
            })
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "init" -> {
                QbSdk.initX5Environment(context.applicationContext, object : QbSdk.PreInitCallback {
                    override fun onCoreInitFinished() {

                    }

                    override fun onViewInitFinished(p0: Boolean) {
                        //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                        result.success(p0)
                    }

                })
            }
            "canUseTbsPlayer" -> {
                //返回是否可以使用tbsPlayer
                result.success(TbsVideo.canUseTbsPlayer(context))
            }
            "openVideo" -> {
                val url = call.argument<String>("url")
                val screenMode = call.argument<Int>("screenMode") ?: 103
                val bundle = Bundle()
                bundle.putInt("screenMode", screenMode)
                TbsVideo.openVideo(context, url, bundle)
                result.success(null)
            }
            "openFileReader" -> {
                val filePath = call.argument<String>("filePath")
                val title = call.argument<String>("title")
                if (!File(filePath).exists()) {
                    Toast.makeText(context, "文件不存在,请确认$filePath 是否正确", Toast.LENGTH_LONG).show()
                    result.success("文件不存在,请确认$filePath 是否正确")
                    return
                }
                val intent = Intent(activity, X5TbsReaderActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("filePath", filePath)
                activity.startActivity(intent)
                result.success(null)
            }
            "openFile" -> {
                val filePath = call.argument<String>("filePath")
                val params = hashMapOf<String, String>()
                params["local"] = call.argument<String>("local") ?: "false"
                params["style"] = call.argument<String>("style") ?: "0"
                params["topBarBgColor"] = call.argument<String>("topBarBgColor") ?: "#2CFC47"
                var menuData = call.argument<String>("menuData")
                if (menuData != null) {
                    params["menuData"] = menuData
                }
                if (!File(filePath).exists()) {
                    Toast.makeText(context, "文件不存在,请确认$filePath 是否正确", Toast.LENGTH_LONG).show()
                    result.success("文件不存在,请确认$filePath 是否正确")
                    return
                }
                QbSdk.canOpenFile(activity, filePath) { canOpenFile ->
                    if (canOpenFile) {
                        QbSdk.openFileReader(activity, filePath, params) { msg ->
                            result.success(msg)
                        }
                    } else {
                        Toast.makeText(context, "X5Sdk无法打开此文件", Toast.LENGTH_LONG).show()
                        result.success("X5Sdk无法打开此文件")
                    }
                }


//                val screenMode = call.argument<Int>("screenMode") ?: 103
//                val bundle = Bundle()
//                bundle.putInt("screenMode", screenMode)
//                TbsVideo.openVideo(context, url, bundle)
//                result.success(null)
            }

            "openWebActivity" -> {
                val url = call.argument<String>("url")
                val title = call.argument<String>("title")
                val intent = Intent(activity, X5WebViewActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("title", title)
                activity.startActivity(intent)
                result.success(null)
            }
            "getCarshInfo" -> {
                val info = WebView.getCrashExtraMessage(context)
                result.success(info)
            }
            "setDownloadWithoutWifi" -> {
                val isWithoutWifi = call.argument<Boolean>("isWithoutWifi")
                QbSdk.setDownloadWithoutWifi(isWithoutWifi ?: false)
                result.success(null)
            }

            else -> {
                result.notImplemented()
            }
        }
    }
}
