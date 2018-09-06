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
  
  NSString *sn = @"VQv/htdRKu1rjhk90MTKF+/aCs9he3raJNE2HZ6uxJ0wKcGVsU6+GQ==";
  NSString *key = @"ezKXj1/GvGh39zvoP2Xsb3F6ZiHkO0GJfoWNMeLhSZ63JJ3dieBQl2mCL24l19hVo9Zxkh3xQ4cP5rcm840iAyOxewEnt45P6moo5+QVRwdFHCkwPXmKaPdQZJFMsVc/D5ob5a2o1MeaPrm+4q9DbwQfa6RWD14GSs/cBjlvKNEbS/UBxbEzbVaYXzdQPxWY8/GaIh5Zrnvve3FHuus56p76lRyKgrV/mY1KlcUAU+dFhRNY39WIsXu/OlACBPgC+s0SHZrH8491AwX+bf3AvpZfIlPHXUH9v6dwptxNmsn+WuleSNrMboCjR1Rf8JHSCvOefKQXWyYrJaziZfTnfX4SnjJS3dD9kVInt4wVQEfn4OmCErUJ2KcLsdceKTUbGKTPiRyzIYp0jKtaZxKQXutUrynOPepJbg82XCyb21kwF4nlrTcKRNCzXb1OuHMR3wFaheyPMX0NII2HgLDXZmDQz9BRXn/leilW8uPKq80xCFqi23/wgh3XQVp/rR55OOJrjeIElWupI93KernRAwjXHcjznQa7eT2j9Bh68VKfzD5WveuQvIcXfprdNYXUfQEYQdc0YdotWM5cO0qsf/+pYlC01LEhHCy5v6YbIwceN0j3awucrcZCLVspA8phyZnq9B2KV0nJQZPaVIUTeNjxOXACFLmxeqbtDw17IlgMc5aFEbLTv/vsg/atXlwtftwvS9UQxkSk6LjL1Htmz8dT94t/tCbdZTR0OwbDPS8UifWRmk/FdEuIFTMl/sOV8cWlKm013CmM0MBURbggNrovO+8gKvG+iq85BR36S7+YFh2r4bv1zuv9KYNTp2Fu1zXTRmvcAJ6/bMS9gibpjtcYc3vE/BFhyRZx2Rl5povNBSLMuVXp8QuOPGuPNKCYve2ysTBSzXNVTD+oyK2qlaLykUNBaj5ip3xf/gCASwSA1RmMBoXANxVv+9FLOmuuMrMkMfItX10ygvy5H9XmZ5Rsrw1hp9o6+gdtSp87pArFjzkKHWERakrHf3iOySoVXyCJRI7UL0PpM1HWYw0Lz7qA47qN+eJlfzcF+8LXppMoTzhk+u9pdApg0mTjh1C8ClEVQqID+kXLPEuoQIkOhLn0ddvAQTzC41tStW7PG23X3ZD4RsoJ7HPM9p0tcgiqGpgjxp3cO7KbhWcgrT1A6dgDBvssTryLW19W5h41jg2Upaj3Ic7F1t0t9qvA2bM=";
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
