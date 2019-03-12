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
  
// If you are registered here, at last js is called foxitpdf.initialize
//  NSString *sn = @"Fu8yheC/oCxkbT2pouHUENUTDXWKLiHppKRziXCt5ZdHpT6XnshZmA==";
//  NSString *key = @"ezJvjl8mvB539PsXZqXcImlssh9qveZTXws5NuYVPpyTgJ1qz4F0lwleIbZghw4S59+RR7ShR0UltvoUSp4TysQT6Ucy4DXcbz1oy7ZmZJs+5Bm3TRCEaeOKikhTNWhhmJD2b4nJHO1pTWuvrea7dVNbmDoz4hqC5EMgs16YClN4Navw8PO1CaQ0YqKxIEz1okdg6WHWRAIsZwQ4oPpZPl8ymJ3wQ8+rY/LJBe0O2xeGUViK5Lul1hKSSfi4186E6RBUO0iAzh/r4v9eiPCl3Ki9RLW255R11aleBCS8JwnqjWLu62hDVe/nUnmuHpKz3YN80LiSCja4Nxwv2Yq96S8eGr2gqwKXt4itJs2uBU477yfTKgKRqikdyAIVXJqF3nPmEG2PKCGVDBpm8ov/HLfqmXfHVic//HetDblejmXp9gDtY4H/t1cNY9cG2K9IywVMx+fhDnk5O6MgPQSXziyTc6VaFvrFj4TeuAUe6cMDurPkVWvqknARiRqtXekfxAeERx2SX4dteHO2EXGDLNdlaqCJYF56Bv+JdhR5wz9/WzKDgymOwP4fYRYix3DJSBYXjyZeBilIOEFOnkRhft9QuOZprW9qN0I0PeVGh5wSUScSLvqKJE6UOnvHAqc1GI442X9boWrvUkCPR9yL/MgjUev2O34gajED8jOKzoLLb6QsB9u4O1jQUl265CtEvZyWXeqKwPA/gWQRGX918syZhyR9GtBeUsRhLuSlYagHwaf5BQP+phsepStgn2Dh6897jZ5XcZFZtSAiLedZbvzUJPl0oPqVu2CukIzGHp3lMHFMFwdivtbonGiZ+JLDeuvyA2PqtCn3gHorGmhEWoaSVNiqGFkxP+MMHbZH8z8YLUk1EoMlJqecDWOS4i4w6KwCfvU+BEcyLQvdVsKZunFd4hVi5dx8kZYmGKi7BK6EqqBIpZPdcBHGy92uXBbQOft7XZtA2boHif9GxfCgV77Yb9r0PZ6h8fakIyKXHVf8xiCQhhakc0Mz3nThE2Qbvq2aZJhpu4JRVi6BySJr2BaUPI3cdgyzqJPbFJ0m1TINZDCRiEoBD3Ihk0E1E7PLXWxhlIfWpBJHDeSPNhm6Zm+k6EFaeadf+RR5nGn/lVC7lvMdYRtn9lBC/7shJE7VhKt6v1WTkrAo5CRS1VGWIZteoHqUWFzzBPAYm6Oz4ViEPV+142ymut9oXIPOKXOzebpyoDJmUMkv8TyHnmdQZF5lCkjW485NUbei";
//  FSErrorCode eRet = [RNTPDFManager initialize:sn key:key];
//  if (FSErrSuccess != eRet) {
//    [self showError:@"Check your sn or key"];
//    return NO;
//  }
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
