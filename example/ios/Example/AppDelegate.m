/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#import "AppDelegate.h"

#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#import <FoxitPDF/PDFManager.h>


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
  
  NSString *sn = @"bqtPqJXM3tVmLYMw3mgUwjLPx0UOd3Cbqg2CNIkgu0KuUPonSVmDxQ==";
  NSString *key = @"ezJvj18mvB539PsXZqXcIklsLeajS1uJbsdKB3VmELeRxklqf9iSxqwvpPpwG3DJeVUCVBNz+EQlthgUzBkbNgWhSLL6Ukv/FGJjTBrm642ffdWUWWKEaWbWQ1srEw4+8f72amQFJHhZo7d53A5FgqTw7x39i+Xl63DLOSHG2QVCIZO8lLs7bE3fwuFD6Klx/Qzrn7s3oT81fEpP5UgMTDttZINOaL8LWTCho5phYqqiRAQ5XUfgRoFlqK57cq8jQGLcLULEh2nJCn7UhtW9UY/6SbEf95LWtOTHGI6S1sunR35i8PWeBmAJThuFuBYpoJl8JSl8eixd2jAiUQ8EwwjXCwQeEkZUxVFSuTfkGsFdyPGbvNqFqiQb7w74F8aaXc3Vl36iRvQNesl+9ebxCR77uSMg15U94LoEdK0P+JWLS60QKR7d/LyjzckKZ5lfPZ2qAvR1+4zaAw78aaXUwlwN7P8luig+XU564NmNfFqZAd+TvfLzfFKsEEoJ1B/H3r4MEJ6p5w6hiigAnFQQN5mksJblt+1msoYKnTflcYcaf7tv3sv1MKvLz8+SgTi1BfBhOd2WEXqeqmqUxetz2XHgSDbf0i0egh/XzPFeEafPLkZCjfjaA8LcWxZkhetSuqzqNuI+rBtPsrsIQbcu9xTH/HskFXEi3UHsX2LGm5zNa2vaTJQKO/Lyic57DbNh+SDvpAmIUuQeTMt9mvcZzFIkKuc0D/Ufbf/vfdd1mmxvFN0t9OjBZvknAvcROYdzDHYWsytXR7EvTrS2BI7KHTaEVRPIETDcmj5R5GebPx1bgZZVIcdTckA99rgxbv93LO/598Orblr04d/yvUmAOL/DEyaxNcOJx7HcvAHiCTYP6B0FidABKMadgt73gVDIZDguWGrt3QG6vDIEMQzRuxCTP5md4pNdNezkHDwxWGTUr95PXbGYxcRNqecePhHUqXyxCTUfGAGkwWdwHQU9oIzYt7eODzCBJZedynsrTKFpNQpvbx4LZlIZ56Wis3CmAQ7fKbf0qFva+fU3mqII6/mARtg+URnz6NqcK/kqsD7et5uuYr96YomISyeBtLSUplEflEokObf4XsNl/c779p5qZs79DEYYyME6z9NBswhxjkkxsq4Se5RYQKbFTAS3wrwqXJ4qywXiRHgFAPyrwdw0KXRYT3/IiFJ+ygI+vaypfK1BHdGvHi3BAGeaKLzwRFhFJ90kQArQBzabuWHzj8f/9Hs=";
  FSErrorCode eRet = [RNTPDFManager initialize:sn key:key];
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
