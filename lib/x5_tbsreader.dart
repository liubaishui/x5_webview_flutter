import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

typedef void X5TbsReaderCreatedCallback(X5TbsReaderController controller);
//typedef void PageFinishedCallback();
//typedef void ShowCustomViewCallback();
//typedef void HideCustomViewCallback();
//typedef void ProgressChangedCallback(int progress);
//typedef void MessageReceived(String name, String data);

class X5TbsReader extends StatefulWidget {
  final filePath;
  final X5TbsReaderCreatedCallback onTbsReaderCreated;
  //final PageFinishedCallback onPageFinished;
  //final ShowCustomViewCallback onShowCustomView;
  //final HideCustomViewCallback onHideCustomView;
  //final ProgressChangedCallback onProgressChanged;
  //final bool javaScriptEnabled;

  const X5TbsReader(
      {Key key,
      this.filePath,
      //this.javaScriptEnabled = false,
      this.onTbsReaderCreated,
      //this.onPageFinished,
      //this.onShowCustomView,
      //this.onHideCustomView,
      //this.onProgressChanged
      })
      : super(key: key);

  @override
  _X5TbsReaderState createState() => _X5TbsReaderState();
}

class _X5TbsReaderState extends State<X5TbsReader> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'com.cjx/x5TbsReader',
        onPlatformViewCreated: _onPlatformViewCreated,
        creationParamsCodec: const StandardMessageCodec(),
        creationParams: _CreationParams.fromWidget(widget).toMap(),
        layoutDirection: TextDirection.rtl,
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      //TODO 添加ios WebView
      return Container();
    } else {
      return Container();
    }
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onTbsReaderCreated == null) {
      return;
    }
    final X5TbsReaderController controller = X5TbsReaderController._(id, widget);
    widget.onTbsReaderCreated(controller);
  }
}

class X5TbsReaderController {
  X5TbsReader _widget;

  X5TbsReaderController._(
    int id,
    this._widget,
  ) : _channel = MethodChannel('com.cjx/x5TbsReader_$id') {
    _channel.setMethodCallHandler(_onMethodCall);
  }

  final MethodChannel _channel;

  Future<void> loadFile(String filePath) async {
    assert(filePath != null);
    return _channel.invokeMethod('loadFile', {
      'filePath': filePath
    });
  }

  Future _onMethodCall(MethodCall call) async {
    switch (call.method) {
      case "onPageFinished":
        print('onPageFinished');
        break;
      default:
        throw MissingPluginException(
            '${call.method} was invoked but has no handler');
        break;
    }
  }
}

class _CreationParams {
  _CreationParams({this.filePath});

  static _CreationParams fromWidget(X5TbsReader widget) {
    return _CreationParams(
        filePath: widget.filePath);
  }

  final String filePath;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'filePath': filePath
    };
  }
}
