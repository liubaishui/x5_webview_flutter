#import "X5WebViewPlugin.h"

@implementation X5WebViewPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftX5WebViewPlugin registerWithRegistrar:registrar];
}
@end
