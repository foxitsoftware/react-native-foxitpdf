#import "AppDelegate.h"

#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#ifdef FB_SONARKIT_ENABLED
#import <FlipperKit/FlipperClient.h>
#import <FlipperKitLayoutPlugin/FlipperKitLayoutPlugin.h>
#import <FlipperKitUserDefaultsPlugin/FKUserDefaultsPlugin.h>
#import <FlipperKitNetworkPlugin/FlipperKitNetworkPlugin.h>
#import <SKIOSNetworkPlugin/SKIOSNetworkAdapter.h>
#import <FlipperKitReactPlugin/FlipperKitReactPlugin.h>

static void InitializeFlipper(UIApplication *application) {
  FlipperClient *client = [FlipperClient sharedClient];
  SKDescriptorMapper *layoutDescriptorMapper = [[SKDescriptorMapper alloc] initWithDefaults];
  [client addPlugin:[[FlipperKitLayoutPlugin alloc] initWithRootNode:application withDescriptorMapper:layoutDescriptorMapper]];
  [client addPlugin:[[FKUserDefaultsPlugin alloc] initWithSuiteName:nil]];
  [client addPlugin:[FlipperKitReactPlugin new]];
  [client addPlugin:[[FlipperKitNetworkPlugin alloc] initWithNetworkAdapter:[SKIOSNetworkAdapter new]]];
  [client start];
}
#endif

#define DOCUMENT_PATH [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]
@implementation AppDelegate

+ (void)initialize {
  [self copyPDFFromResourceToDocuments:@"sample" overwrite:NO];
}

+ (BOOL)copyPDFFromResourceToDocuments:(NSString *)filename overwrite:(BOOL)overwrite {
  NSFileManager *fileManager = [NSFileManager defaultManager];
  
  NSString *fromPath = [[NSBundle mainBundle] pathForResource:filename ofType:@"pdf"];
  if (!fromPath)
    return NO;
  
  NSString *toPath = [DOCUMENT_PATH stringByAppendingPathComponent:[filename stringByAppendingString:@".pdf"]];
  if ([fileManager fileExistsAtPath:toPath]) {
    if (overwrite) {
      if (![fileManager removeItemAtPath:toPath error:nil]) {
        return NO;
      }
    } else {
      return NO;
    }
  }
  
  NSError *error = nil;
  if ([fileManager copyItemAtPath:fromPath toPath:toPath error:&error]) {
    return YES;
  } else {
    NSLog(@"Fail to copy %@. %@", filename, [error localizedDescription]);
    return NO;
  }
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
#ifdef FB_SONARKIT_ENABLED
  InitializeFlipper(application);
#endif

  RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];
  RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:bridge
                                                   moduleName:@"Example"
                                            initialProperties:nil];

  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
}

-(void)showError:(NSString *)errMsg {
  UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"Error message"
                                                                 message:errMsg
                                                          preferredStyle:UIAlertControllerStyleAlert];
  UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault
                                                        handler:^(UIAlertAction * action) {
                                                        }];
  [alert addAction:defaultAction];
  UIViewController *rootController = UIApplication.sharedApplication.delegate.window.rootViewController;
  [rootController presentViewController:alert animated:YES completion:nil];
}
- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

@end
