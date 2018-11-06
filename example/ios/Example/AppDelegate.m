/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#import "AppDelegate.h"

#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#import <FoxitRDK/FSPDFObjC.h>

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
  NSURL *jsCodeLocation;

  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];

  RCTRootView *rootView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                      moduleName:@"Example"
                                               initialProperties:nil
                                                   launchOptions:launchOptions];
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  
  NSString *sn = @"DW6QUNWzT4IF0JluGTcsD/OOs12XAfWdNkVPejrfj72Mi6P3xjaElw==";
  NSString *key = @"ezJvj18mvB539PsXZqX8Iklssh9qvOZTXwsIO/MBC0bmgJ1qz4F0lwleIbZghw4SQD11gx1jqOJvZixkxBpuX+IYO08ZheJYkMQumnRHZ+eSysccHXuESTewCCK1K/MgHY/k54IqDvVbkdKIJu9QmzmxEJhP8zrulCE5v/XtHRKbNVNsvDpNop2HE1XwRtNon3s6YQ+j+c8BACgApAsRalO9SQP2GlkkBEAYqvY2JrrnRhfeZngd25kw9CZGd9p3QToervqkj64UV/3I4sqU/0arSotj32QPFCYAt9roaKCzAYoTeaE/l7zlnpd3dcZwf7NiYwSSrQ2LNpD+r2lHGi8WGr2hO7hwWtX9vdklMHOf/YFo+/05XxoVlnVAtYXRxx7S3MVeSEnhswujY+AswVbBgKGJRGRWzKnv7py3803X3DH5PGqRRayjTceiRq0ddSf7GiNtRQittqcRQSBYet43Rvyca+NyBxa5DddneG1VbBaVtM12C2Xv02/8HNLAf5AF3Vua/6O1gRi2ofIm0dqpk59lj19OiyqBIV6Ma/HZ93SwKLycOxDOHIcn2cVM1UtY+pF7ptUGz4Mq2V9YBaB2wxFB7I3mUEBjGOx1Y1ZAyvpWclWebMpIUct3ku4PmLsuSeX1uAd4iGOggcIFXPiUdYu7RytAbIlnWLtd5FZnwAfsyN/norSSmAtdZMHEv0xmh865YCFDkyo7Lw5ilhUICpZV2qJqqg6n757PdZcyO+M57r5bdcMto40q3M+lmiqOV8Wj/ui9v1h+UHOKQBvCti5TYvI5FWN/biCleETDEXUV1aMvVm/Zcyuu4njBWgL+0FMzCx72Lv0oIHsSl3THc2TS95YL9/3QpYQTAue6VpXdEAN1s3u4rzQVJCmT2QPK4FP/pznBYEP289VheUd1I521v93LZf9TWFDeIUIjE83bEGdtlJRdbqPR2fXccdtLWUeG+Ky97MqncQHy4REqjmBqNxjlo/gvEshBV7VOntNcUmpCLHKyZF+IupSlQ5zO0lJ9RaPShX+VkaI9rx17Oif8q0qvz29nA9s5XyBe87VjQm6BjA7b5hZnixsuZlv+R7ZhyWU5jaTh1BuLbz3zIDAO90rK9qnMP2hm5AFRmy962CqDi/vW0nyQISpgMlSJsGkPUxpg5TuhiGe13TEHMQyHVdBodOcMUBaO1sk4mdeYk7qUm78ek2VL4PhgZHZO3KE+B1ASiVG4iqGAbYiM";
  FSErrorCode eRet = [FSLibrary initialize:sn key:key];
  if (FSErrSuccess != eRet) {
    [self showError:@"Check your sn or key"];
    return NO;
  }
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
@end
