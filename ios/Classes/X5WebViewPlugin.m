#import "X5WebViewPlugin.h"
#import <x5_webview/x5_webview-Swift.h>

@implementation X5WebViewPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftX5WebViewPlugin registerWithRegistrar:registrar];
}
@end
