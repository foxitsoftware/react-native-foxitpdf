//
//  PDFManager.m
//  FoxitReader
//
//  Created by user on 4/10/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <FoxitRDK/FSPDFViewControl.h>
#import <uiextensionsDynamic/uiextensionsDynamic.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>

static FSErrorCode errorCode = FSErrUnknown;
static NSString *initializeSN;
static NSString *initializeKey;

@interface PDFViewController : UIViewController
@property (nonatomic, weak) UIExtensionsManager *extensionsManager;
@end

@interface RNTPDFManager : NSObject <RCTBridgeModule, UIExtensionsManagerDelegate, IDocEventListener>
@property (nonatomic, strong) FSPDFViewCtrl *pdfViewCtrl;
@property (nonatomic, strong) PDFViewController *pdfViewController;
@property (nonatomic, strong) UINavigationController *rootViewController;
@property (nonatomic, strong) UIExtensionsManager *extensionsManager;
@end

@implementation RNTPDFManager {

    NSDictionary* _toolbarConfig;
    NSDictionary* _panelConfig;
    NSDictionary* _viewSettingsConfig;
    NSDictionary* _viewMoreConfig;
}

+ (FSErrorCode)initialize:(NSString *)sn
                      key:(NSString *)key{
    if (![initializeSN isEqualToString:sn] || ![initializeKey isEqualToString:key]) {
        if (errorCode == FSErrSuccess) [FSLibrary destroy];
        errorCode = [FSLibrary initialize:sn key:key];
        if (errorCode != FSErrSuccess) {
            return errorCode;
        }else{
            initializeSN = sn;
            initializeKey = key;
        }
    }
    return errorCode;
}

+ (BOOL)requiresMainQueueSetup{
    return YES;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orientationDidChange) name:UIDeviceOrientationDidChangeNotification object:nil];
    }
    return self;
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIDeviceOrientationDidChangeNotification object:nil];
}

RCT_EXPORT_MODULE(PDFManager)

@synthesize bridge = _bridge;

RCT_EXPORT_METHOD(initialize:(NSString *)sn key:(NSString *)key){
    if ([RNTPDFManager initialize:sn key:key] != FSErrSuccess ) {
        [self showError:@"Check your sn or key"];
    }
}

RCT_EXPORT_METHOD(openDocument:(NSString *)src
                  password:(NSString *)password
                  uiConfig:(NSDictionary *)uiConfig) {
        [self openPDF:src
             password:password
             uiConfig:uiConfig];
}

RCT_EXPORT_METHOD(openDocFromUrl:(NSString *)url
                  password:(NSString *)password
                  uiConfig:(NSDictionary *)uiConfig
                  /*errorBlock:(RCTResponseErrorBlock)errorBlock*/) {
    if (uiConfig == NULL) {
        NSString *configPath = [[NSBundle bundleForClass:[self class]]  pathForResource:@"uiextensions_config" ofType:@"json"];
        NSData *configData = [NSData dataWithContentsOfFile:configPath];
        uiConfig = [NSJSONSerialization JSONObjectWithData:configData options:NSJSONReadingMutableContainers error:nil];
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        
        [self uiConfig:uiConfig];
        
        NSURL *targetURL = [NSURL URLWithString:url];
        if (targetURL == nil) {
            [self showError:@"file not found in Document directory!"];
        }else{
            __weak typeof(self) weakSelf = self;
            [self.pdfViewCtrl openDocFromURL:targetURL password:password cacheOption:nil httpRequestProperties:nil completion:^(FSErrorCode errorCode) {
                NSError *error;
                NSDictionary *userInfo;
                if (errorCode == FSErrSuccess) {
                    if (!weakSelf.rootViewController.presentingViewController) {
                        weakSelf.rootViewController.modalPresentationStyle = UIModalPresentationFullScreen;
                        [[weakSelf topMostViewController] presentViewController:weakSelf.rootViewController animated:YES completion:^{

                        }];
                    }
                } else if (errorCode == FSErrDeviceLimitation) {
                    [weakSelf showError:@"Exceeded the limit on the number of devices allowed"];
                    userInfo = [NSDictionary dictionaryWithObject:@"Exceeded the limit on the number of devices allowed" forKey:NSLocalizedDescriptionKey];
                } else if (errorCode == FSErrCanNotGetUserToken) {
                    [weakSelf showError:@"Please Sign in first"];
                    userInfo = [NSDictionary dictionaryWithObject:@"Please Sign in first" forKey:NSLocalizedDescriptionKey];
                } else if (errorCode == FSErrInvalidLicense) {
                    [weakSelf showError:@"You are not authorized to use this add-on module， please contact us for upgrading your license. "];
                    userInfo = [NSDictionary dictionaryWithObject:@"You are not authorized to use this add-on module， please contact us for upgrading your license. " forKey:NSLocalizedDescriptionKey];
                } else {
                    [weakSelf showError:@"Failed to open the document"];
                    userInfo = [NSDictionary dictionaryWithObject:@"Failed to open the document" forKey:NSLocalizedDescriptionKey];

                }
                if (userInfo) {
                    error = [NSError errorWithDomain:@"FSErrorCode" code:errorCode userInfo:userInfo];
                }
                /*errorBlock(error);*/
            }];
            
            self.extensionsManager.goBack = ^{
                [weakSelf.rootViewController dismissViewControllerAnimated:YES completion:nil];
            };
        }
    });
}

RCT_EXPORT_METHOD(openPDF:(NSString *)src
                  password:(NSString *)password
                  uiConfig:(NSDictionary *)uiConfig) {
    if (uiConfig == NULL) {
        NSString *configPath = [[NSBundle bundleForClass:[self class]]  pathForResource:@"uiextensions_config" ofType:@"json"];
        NSData *configData = [NSData dataWithContentsOfFile:configPath];
        uiConfig = [NSJSONSerialization JSONObjectWithData:configData options:NSJSONReadingMutableContainers error:nil];
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        
        [self uiConfig:uiConfig];
        
        NSURL *targetURL = nil;
        if (targetURL == nil) {
            targetURL = [self fileFromDocumentsDirectoryURL:src];
        }
        
        if (targetURL == nil) {
            [self showError:@"file not found in Document directory!"];
        }else{
            __weak typeof(self) weakSelf = self;
            [self.pdfViewCtrl openDoc:targetURL.path password:password completion:^(FSErrorCode error) {
                if (error == FSErrSuccess) {
                    if (!weakSelf.rootViewController.presentingViewController) {
                        weakSelf.rootViewController.modalPresentationStyle = UIModalPresentationFullScreen;
                        [[weakSelf topMostViewController] presentViewController:weakSelf.rootViewController animated:YES completion:^{

                        }];
                    }
                } else if (error == FSErrDeviceLimitation) {
                    [weakSelf showError:@"Exceeded the limit on the number of devices allowed"];
                } else if (error == FSErrCanNotGetUserToken) {
                    [weakSelf showError:@"Please Sign in first"];
                } else if (error == FSErrInvalidLicense) {
                    [weakSelf showError:@"You are not authorized to use this add-on module， please contact us for upgrading your license. "];
                } else {
                    [weakSelf showError:@"Failed to open the document"];
                }
            }];
            
            self.extensionsManager.goBack = ^{
                [weakSelf.rootViewController dismissViewControllerAnimated:YES completion:nil];
            };
        }
    });
}

- (void)uiConfig:(NSDictionary *)uiConfig{
    if (errorCode != FSErrSuccess) {
        [self showError:@"Check your sn or key"];
        return;
    }
    
    self.pdfViewCtrl = [[FSPDFViewCtrl alloc] initWithFrame:[UIScreen mainScreen].bounds];
    [self.pdfViewCtrl setRMSAppClientId:@"972b6681-fa03-4b6b-817b-c8c10d38bd20" redirectURI:@"com.foxitsoftware.com.mobilepdf-for-ios://authorize"];
    [self.pdfViewCtrl registerDocEventListener:self];

    self.pdfViewController = [[PDFViewController alloc] init];
    self.pdfViewController.automaticallyAdjustsScrollViewInsets = NO;
    
    self.pdfViewController.view = self.pdfViewCtrl;
    self.rootViewController = [[UINavigationController alloc] initWithRootViewController:self.pdfViewController];
    self.rootViewController.modalPresentationStyle = UIModalPresentationFullScreen;
    self.rootViewController.navigationBarHidden = YES;
    
    if ( uiConfig != NULL) {
        NSData* configData = [NSJSONSerialization dataWithJSONObject:uiConfig
                                                             options:NSJSONWritingPrettyPrinted
                                                               error: nil];
        
        self.extensionsManager = [[UIExtensionsManager alloc] initWithPDFViewControl:self.pdfViewCtrl configuration:configData];
    } else {
        
        self.extensionsManager = [[UIExtensionsManager alloc] initWithPDFViewControl:self.pdfViewCtrl];
    }
    self.pdfViewController.extensionsManager = self.extensionsManager;

    self.extensionsManager.delegate = self;
    
    NSDictionary *toolBars = uiConfig[@"toolBars"];
    
    BOOL enableTopToolbar  = YES;
    if (toolBars[@"enableTopToolbar"]) {
        enableTopToolbar = [toolBars[@"enableTopToolbar"] boolValue];
    }
    
    BOOL enableBottomToolbar = YES;
    if (toolBars[@"enableBottomToolbar"]) {
        enableBottomToolbar = [toolBars[@"enableBottomToolbar"] boolValue];
    }

    [self.extensionsManager enableBottomToolbar:enableBottomToolbar];
    [self.extensionsManager enableTopToolbar:enableTopToolbar];
    
    [self setPanelConfig:uiConfig[@"panels"]];
    [self setViewMoreConfig:uiConfig[@"menus"][@"moreMenus"]];
    [self setViewSettingsConfig:uiConfig[@"menus"][@"viewMenus"]];
    [self setToolbarConfig:toolBars[@"toolItems"]];
    
    self.pdfViewCtrl.extensionsManager = self.extensionsManager;
}

- (UIViewController*) topMostViewController {
    UIViewController *presentingViewController = [self getForegroundActiveWindow].rootViewController;
    while (presentingViewController.presentedViewController != nil) {
        presentingViewController = presentingViewController.presentedViewController;
    }
    return presentingViewController;
}

- (UIWindow *)getForegroundActiveWindow{

    UIWindow *originalKeyWindow = nil;
    #if __IPHONE_OS_VERSION_MAX_ALLOWED >= 130000
    if (@available(iOS 13.0, *)) {
        NSSet<UIScene *> *connectedScenes = [UIApplication sharedApplication].connectedScenes;
        for (UIScene *scene in connectedScenes) {
            if ([scene isKindOfClass:[UIWindowScene class]]) {
                UIWindowScene *windowScene = (UIWindowScene *)scene;
                if (windowScene.activationState == UISceneActivationStateUnattached){
                    originalKeyWindow = windowScene.windows.firstObject;
                }
                if (scene.activationState == UISceneActivationStateForegroundActive) {
                    for (UIWindow *window in windowScene.windows) {
                        if (window.isKeyWindow) {
                            originalKeyWindow = window;
                            break;
                        }
                    }
                }
            }

        }
        if (originalKeyWindow) {
            return originalKeyWindow;
        }
    }
    #endif
    return [[[UIApplication sharedApplication] delegate] window];
}

-(void)showError:(NSString *)errMsg {
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"Error message"
                                                                   message:errMsg
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault
                                                          handler:^(UIAlertAction * action) {
                                                              //响应事件
                                                              NSLog(@"action = %@", action);
                                                          }];
    
    [alert addAction:defaultAction];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[self topMostViewController] presentViewController:alert animated:YES completion:nil];
    });
    
}

- (NSURL *)fileFromDocumentsDirectoryURL:(NSString *) filename {
    if (filename.length == 0 ) {
        return nil;
    }
    NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
    if (documentsDirectory == nil) {
        return nil;
    }
    NSString *filePath = [documentsDirectory stringByAppendingPathComponent: filename];
    if(![[NSFileManager defaultManager] fileExistsAtPath: filePath]) {
        return nil;
    }
    return [NSURL fileURLWithPath:filePath];
}

//panelConfig
- (void) setPanelConfig: (NSDictionary *) config
{
    _panelConfig = config;
    
    if (_panelConfig != NULL) {
        id val = [_panelConfig objectForKey:@"readingBookmark"] ;
        
        if (val != NULL) {
            [self.extensionsManager.panelController setPanelHidden:![val boolValue] type:FSPanelTypeReadingBookmark];
        }
        
        val = [_panelConfig objectForKey:@"outline"] ;
        
        if (val != NULL) {
            [self.extensionsManager.panelController setPanelHidden:![val boolValue] type:FSPanelTypeOutline];
        }
        
        val = [_panelConfig objectForKey:@"annotation"] ;
        
        if (val != NULL) {
            [self.extensionsManager.panelController setPanelHidden:![val boolValue] type:FSPanelTypeAnnotation];
        }
        
        val = [_panelConfig objectForKey:@"attachments"] ;
        
        if (val != NULL) {
            [self.extensionsManager.panelController setPanelHidden:![val boolValue] type:FSPanelTypeAttachment];
        }
        
        val = [_panelConfig objectForKey:@"signature"] ;
        
        if (val != NULL) {
            [self.extensionsManager.panelController setPanelHidden:![val boolValue] type:FSPanelTypeDigitalSignature];
        }
    }
}

//viewMoreConfig
- (void) setViewMoreConfig: (NSDictionary *) moreMenus
{
    _viewMoreConfig = moreMenus;
    
    if (_viewMoreConfig != NULL) {
        id val = [moreMenus objectForKey:@"protect"];
        if (val && [val isKindOfClass:[NSDictionary class]]) {
            id redaction = [val objectForKey:@"redaction"];
            if (redaction) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_PROTECT andItemTag:TAG_ITEM_REDACTION hidden:![redaction boolValue]];
            }
            id encryption = [val objectForKey:@"encryption"];
            if (encryption) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_PROTECT andItemTag:TAG_ITEM_PASSWORD hidden:![encryption boolValue]];
            }
            id trustedCertificates = [val objectForKey:@"trustedCertificates"];
            if (trustedCertificates) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_PROTECT andItemTag:TAG_ITEM_CERTIFICATE hidden:![trustedCertificates boolValue]];
            }
        }
        val = [moreMenus objectForKey:@"commentsAndFields"];
        if (val && [val isKindOfClass:[NSDictionary class]]) {
            id importComment = [val objectForKey:@"importComment"];
            if (importComment) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_COMMENT_FIELD andItemTag:TAG_ITEM_IMPORTCOMMENT hidden:![importComment boolValue]];
            }
            id exportComment = [val objectForKey:@"exportComment"];
            if (exportComment) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_COMMENT_FIELD andItemTag:TAG_ITEM_EXPORTCOMMENT hidden:![exportComment boolValue]];
            }
            id summarizeComment = [val objectForKey:@"summarizeComment"];
            if (summarizeComment) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_COMMENT_FIELD andItemTag:TAG_ITEM_SUMARIZECOMMENT hidden:![summarizeComment boolValue]];
            }
            id resetForm = [val objectForKey:@"resetForm"];
            if (resetForm) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_COMMENT_FIELD andItemTag:TAG_ITEM_RESETFORM hidden:![resetForm boolValue]];
            }
            id importForm = [val objectForKey:@"importForm"];
            if (importForm) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_COMMENT_FIELD andItemTag:TAG_ITEM_IMPORTFORM hidden:![importForm boolValue]];
            }
            id exportForm = [val objectForKey:@"exportForm"];
            if (exportForm) {
                [self.extensionsManager.more setMoreViewItemHiddenWithGroup:TAG_GROUP_COMMENT_FIELD andItemTag:TAG_ITEM_EXPORTFORM hidden:![exportForm boolValue]];
            }
        }
        val = [moreMenus objectForKey:@"saveAs"];
        if (val) {
            [self.extensionsManager.more setIndividualMenuItemHiddenWithItemTag:TAG_ITEM_SAVE_AS hidden:![val boolValue]];
        }
        val = [moreMenus objectForKey:@"reduceFileSize"];
        if (val) {
            [self.extensionsManager.more setIndividualMenuItemHiddenWithItemTag:TAG_ITEM_REDUCEFILESIZE hidden:![val boolValue]];
        }
        val = [moreMenus objectForKey:@"print"];
        if (val) {
            [self.extensionsManager.more setIndividualMenuItemHiddenWithItemTag:TAG_ITEM_WIRELESSPRINT hidden:![val boolValue]];
        }
        val = [moreMenus objectForKey:@"flatten"];
        if (val) {
            [self.extensionsManager.more setIndividualMenuItemHiddenWithItemTag:TAG_ITEM_FLATTEN hidden:![val boolValue]];
        }
        val = [moreMenus objectForKey:@"crop"];
        if (val) {
            [self.extensionsManager.more setIndividualMenuItemHiddenWithItemTag:TAG_ITEM_SCREENCAPTURE hidden:![val boolValue]];
        }
    }
}

//view Settings config
- (void) setViewSettingsConfig: (NSDictionary *) config
{
    _viewSettingsConfig = config;
    
    if (_viewSettingsConfig != NULL) {
        id val = [_viewSettingsConfig objectForKey:@"single"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:SINGLE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"continuous"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:CONTINUOUS hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"facingPage"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:DOUBLEPAGE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"coverPage"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:COVERPAGE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"reflow"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:REFLOW hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"day"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:DAYMODE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"night"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:NIGHTMODE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"pageColor"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:PAGECOLOR hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"fitPage"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:FITPAGE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"fitWidth"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:FITWIDTH hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"cropPage"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:CROPPAGE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"speak"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:SPEECH hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"autoFilp"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:AUTOFLIP hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"rotate"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:ROTATE hidden:![val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"panZoom"] ;
        if (val != NULL) {
            [self.extensionsManager.settingBar setItem:PANZOOM hidden:![val boolValue]];
        }
        
    }
}

- (void) setToolbarConfig: (NSDictionary *) config
{
    _toolbarConfig = config;
    
    if (config != NULL) {
        id val = [config objectForKey:@"more"];
        if (val != NULL) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_MORE hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"back"] ;
        if (val != NULL) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_BACK hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"bookmark"] ;
        if (val != NULL) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_READING_BOOKMARK hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"search"] ;
        if (val != NULL) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_SEARCH hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"panel"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_PANEL hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"thumbnail"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_THUMBNAIL hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"home"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_HOME hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"edit"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_EDIT hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"comment"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_COMMENT hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"drawing"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_DRAWING hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"view"] ;
        if (val) {
            if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
                [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_VIEW_SETTINGS hidden:![val boolValue]];
            }else{
                [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_VIEW hidden:![val boolValue]];
            }
        }
        
        val = [config objectForKey:@"form"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_FORM hidden:![val boolValue]];
        }
        
        val = [config objectForKey:@"fillSign"] ;
        if (val) {
            [self.extensionsManager setToolbarItemHiddenWithTag:FS_TOOLBAR_ITEM_TAG_SIGN hidden:![val boolValue]];
        }
    }
}


// MARK: - UIExtensionsManagerDelegate
#pragma mark - UIExtensionsManagerDelegate
-(BOOL)uiextensionsManager:(UIExtensionsManager *)uiextensionsManager openNewDocAtPath:(NSString *)path{
    FSPDFDoc *doc = [[FSPDFDoc alloc] initWithPath:path];
    if (FSErrSuccess == [doc load:nil]) {
        __weak typeof(self) weakSelf = self;
        [self.pdfViewCtrl openDoc:path
                         password:nil
                       completion:^(FSErrorCode error) {
                           if (error == FSErrSuccess) {
                               if (weakSelf.rootViewController.presentingViewController) {
                                    [weakSelf.rootViewController dismissViewControllerAnimated:NO completion:nil];
                               }
                               weakSelf.rootViewController.modalPresentationStyle = UIModalPresentationFullScreen;
                               [[weakSelf topMostViewController] presentViewController:weakSelf.rootViewController animated:NO completion:^{

                               }];
                           } else if (error == FSErrDeviceLimitation) {
                               [weakSelf showError:@"Exceeded the limit on the number of devices allowed"];
                           } else if (error == FSErrCanNotGetUserToken) {
                               [weakSelf showError:@"Please Sign in first"];
                           } else if (error == FSErrInvalidLicense) {
                               [weakSelf showError:@"You are not authorized to use this add-on module， please contact us for upgrading your license. "];
                           } else {
                               [weakSelf showError:@"Failed to open the document"];
                           }
                       }];
        return YES;
    }else{
        return NO;
    }
}

// MARK: - IDocEventListener
- (void)onDocOpened:(FSPDFDoc *)document error:(int)error {
    
}

- (void)onDocClosed:(FSPDFDoc *)document error:(int)error {
    
}

#pragma mark - rotate event

- (void)orientationDidChange{
    UIDeviceOrientation currentOri = [[UIDevice currentDevice] orientation];
    [self.extensionsManager didRotateFromInterfaceOrientation:(UIInterfaceOrientation)currentOri];
}

@end

@implementation PDFViewController
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return !self.extensionsManager.isScreenLocked;
}

- (BOOL)shouldAutorotate {
    return !self.extensionsManager.isScreenLocked;
}

- (BOOL)prefersStatusBarHidden {
    return self.extensionsManager.prefersStatusBarHidden;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskAll;
}

@end

@implementation NSObject (fix_bug)

+ (UIWindow *)fs_getForegroundAnyKeyWindow{
    
    UIWindow *originalKeyWindow = nil;
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0
    if (@available(iOS 13.0, *)) {
        NSSet<UIScene *> *connectedScenes = [UIApplication sharedApplication].connectedScenes;
        for (UIScene *scene in connectedScenes) {
            if ([scene isKindOfClass:[UIWindowScene class]]) {
                UIWindowScene *windowScene = (UIWindowScene *)scene;
                
                for (UIWindow *window in windowScene.windows) {
                    if (window) {
                        originalKeyWindow = window;
                        if (window.hidden) continue;
                        break;
                    }
                }
            }
        }
        if (originalKeyWindow) {
            return originalKeyWindow;
        }
    }
#endif
    return [[[UIApplication sharedApplication] delegate] window];
}

@end
