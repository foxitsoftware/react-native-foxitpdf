//
//  PDFManager.m
//  FoxitReader
//
//  Created by user on 4/10/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FoxitRDK/FSPDFViewControl.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import "uiextensions/UIExtensionsManager.h"

@interface RNTPDFManager : NSObject <RCTBridgeModule, UIExtensionsManagerDelegate, IDocEventListener>
@end

@implementation RNTPDFManager {
    FSPDFViewCtrl* pdfViewCtrl;
    UIExtensionsManager* extensionsManager;
    NSDictionary* _topToolbarConfig;
    NSDictionary* _bottomToolbarConfig;
    NSDictionary* _panelConfig;
    NSDictionary* _viewSettingsConfig;
    NSDictionary* _viewMoreConfig;
    NSArray *topToolbarVerticalConstraints;
    
    UINavigationController *rootViewController;
    UIViewController *pdfViewController;
}

RCT_EXPORT_MODULE(PDFManager)

@synthesize bridge = _bridge;

RCT_EXPORT_METHOD(openPDF:(NSString *)src
                  password:(NSString *)password
                  extensionConfig:(NSDictionary *)extensionConfig
                  enableTopToolbar:(BOOL)enableTopToolbar
                  enableBottomToolbar:(BOOL)enableBottomToolbar
                  topToolbarConfig:(NSDictionary *)topToolbarConfig
                  bottomToolbarConfig:(NSDictionary *)bottomToolbarConfig
                  panelConfig:(NSDictionary *)panelConfig
                  viewSettingsConfig:(NSDictionary *)viewSettingsConfig
                  viewMoreConfig:(NSDictionary *)viewMoreConfig) {
    pdfViewCtrl = [[FSPDFViewCtrl alloc] initWithFrame:[UIScreen mainScreen].bounds];
    [pdfViewCtrl registerDocEventListener:self];
    
    //    [pdfViewCtrl openDoc:pdfPath password:nil completion:nil];
    //    pdfViewCtrl.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    pdfViewController = [[UIViewController alloc] init];
    pdfViewController.automaticallyAdjustsScrollViewInsets = NO;
    pdfViewController.view = pdfViewCtrl;
    rootViewController = [[UINavigationController alloc] initWithRootViewController:pdfViewController];
    rootViewController.navigationBarHidden = YES;
    
    if ( extensionConfig != NULL) {
        NSData* configData = [NSJSONSerialization dataWithJSONObject:@{
                                                                       @"modules": extensionConfig
                                                                       }
                                                             options:NSJSONWritingPrettyPrinted
                                                               error: nil];
        
        extensionsManager = [[UIExtensionsManager alloc] initWithPDFViewControl:pdfViewCtrl configuration:configData];
    } else {
        extensionsManager = [[UIExtensionsManager alloc] initWithPDFViewControl:pdfViewCtrl];
    }
    
    extensionsManager.delegate = self;
    

    [extensionsManager enableBottomToolbar:enableBottomToolbar];
    [extensionsManager enableTopToolbar:enableTopToolbar];
    
    [self setPanelConfig:panelConfig];
    [self setViewMoreConfig:viewMoreConfig];
    [self setViewSettingsConfig:viewSettingsConfig];
    [self setTopToolbarConfig:topToolbarConfig];
    [self setBottomToolbarConfig:bottomToolbarConfig];
    
    pdfViewCtrl.extensionsManager = extensionsManager;
    [self wrapTopToolbar];
    topToolbarVerticalConstraints = @[];
    
    NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:src]];
    FSPDFDoc *pdfdoc  = [[FSPDFDoc alloc] initWithMemory:data];
    [pdfdoc load:nil];
    //FSPDFDoc* pdfdoc = [[FSPDFDoc alloc] initWithHandler:(nonnull id<FSFileReadCallback>)];
    // Load the unencrypted document content.
    //if(e_errSuccess != [pdfdoc load:nil]) {
    //  return; }
    
    // Set the document to view control.
    [pdfViewCtrl setDoc:pdfdoc];
    
    [[[UIApplication sharedApplication].delegate window].rootViewController presentViewController:rootViewController animated:YES completion:^{
        
    }];
}

//panelConfig
- (void) setPanelConfig: (NSDictionary *) config
{
    _panelConfig = config;
    
    if (_panelConfig != NULL) {
        id val = [_panelConfig objectForKey:@"readingBookmark"] ;
        
        if (val != NULL) {
            [extensionsManager.panelController setPanelHidden:[val boolValue] type:FSPanelTypeReadingBookmark];
        }
        
        val = [_panelConfig objectForKey:@"outline"] ;
        
        if (val != NULL) {
            [extensionsManager.panelController setPanelHidden:[val boolValue] type:FSPanelTypeOutline];
        }
        
        val = [_panelConfig objectForKey:@"annotation"] ;
        
        if (val != NULL) {
            [extensionsManager.panelController setPanelHidden:[val boolValue] type:FSPanelTypeAnnotation];
        }
        
        val = [_panelConfig objectForKey:@"attachments"] ;
        
        if (val != NULL) {
            [extensionsManager.panelController setPanelHidden:[val boolValue] type:FSPanelTypeAttachment];
        }
    }
}

//viewMoreConfig
- (void) setViewMoreConfig: (NSDictionary *) config
{
    _viewMoreConfig = config;
    
    if (_viewMoreConfig != NULL) {
        id val = [_viewMoreConfig objectForKey:@"groupFile"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:1 hidden:[val boolValue]];
        }
        
        val = [_viewMoreConfig objectForKey:@"groupProject"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:4 hidden:[val boolValue]];
        }
        
        val = [_viewMoreConfig objectForKey:@"groupForm"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:7 hidden:[val boolValue]];
        }
        
        NSDictionary *groupFileConfig = [_viewMoreConfig objectForKey:@"groupFileConfig"];
        
        // TAG_GROUP_FILE Config
        if (groupFileConfig != NULL) {
            val = [groupFileConfig objectForKey:@"itemFileInfo"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:10 hidden:[val boolValue]];
            }
            
            val = [groupFileConfig objectForKey:@"itemReduceFileSize"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:11 hidden:[val boolValue]];
            }
            
            val = [groupFileConfig objectForKey:@"itemWirelessPrint"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:14 hidden:[val boolValue]];
            }
            
            val = [groupFileConfig objectForKey:@"itemCrop"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:16 hidden:[val boolValue]];
            }
        }
        
        NSDictionary *groupProjectConfig = [_viewMoreConfig objectForKey:@"groupProjectConfig"];
        // TAG_GROUP_PROJECT
        if (groupProjectConfig != NULL) {
            val = [groupProjectConfig objectForKey:@"itemPassword"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:17 hidden:[val boolValue]];
            }
        }
        
        NSDictionary *groupFormConfig = [_viewMoreConfig objectForKey:@"groupFormConfig"];
        // TAG_GROUP_FORM CONFIG
        if (groupFormConfig != NULL) {
            val = [groupFormConfig objectForKey:@"itemResetForm"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:24 hidden:[val boolValue]];
            }
            
            val = [groupFormConfig objectForKey:@"itemImportForm"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:25 hidden:[val boolValue]];
            }
            
            val = [groupFormConfig objectForKey:@"itemExportForm"] ;
            
            if (val != NULL) {
                [extensionsManager setToolbarItemHiddenWithTag:26 hidden:[val boolValue]];
            }
        }
    }
}

//view Settings config
- (void) setViewSettingsConfig: (NSDictionary *) config
{
    _viewSettingsConfig = config;
    
    if (_viewSettingsConfig != NULL) {
        id val = [_viewSettingsConfig objectForKey:@"singlePage"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:SINGLE hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"continuousPage"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:CONTINUOUS hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"thumbnail"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:THUMBNAIL hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"autoBrightness"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:BRIGHTNESS hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"night"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:NIGHTMODE hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"reflow"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:REFLOW hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"screenLock"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:LOCKSCREEN hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"crop"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:CROPPAGE hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"panAndZoom"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:PANZOOM hidden:[val boolValue]];
        }
        
        val = [_viewSettingsConfig objectForKey:@"facing"] ;
        if (val != NULL) {
            [extensionsManager.settingBar setItem:DOUBLEPAGE hidden:[val boolValue]];
        }
        
    }
}

- (void) setTopToolbarConfig: (NSDictionary *) config
{
    _topToolbarConfig = config;
    
    if (_topToolbarConfig != NULL) {
        id val = [_topToolbarConfig objectForKey:@"ItemMore"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_TOPBAR_ITEM_MORE_TAG hidden:[val boolValue]];
        }
        
        val = [_topToolbarConfig objectForKey:@"ItemBack"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_TOPBAR_ITEM_BACK_TAG hidden:[val boolValue]];
        }
        
        val = [_topToolbarConfig objectForKey:@"ItemBookmark"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_TOPBAR_ITEM_BOOKMARK_TAG hidden:[val boolValue]];
        }
        
        val = [_topToolbarConfig objectForKey:@"ItemSearch"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_TOPBAR_ITEM_SEARCH_TAG hidden:[val boolValue]];
        }
    }
}

- (void) setBottomToolbarConfig: (NSDictionary *) config
{
    _bottomToolbarConfig = config;
    
    if (_bottomToolbarConfig != NULL) {
        id val = [_bottomToolbarConfig objectForKey:@"ItemAnnot"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_BOTTOMBAR_ITEM_ANNOT_TAG hidden:[val boolValue]];
        }
        
        val = [_bottomToolbarConfig objectForKey:@"ItemPanel"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_BOTTOMBAR_ITEM_PANEL_TAG hidden:[val boolValue]];
        }
        
        val = [_bottomToolbarConfig objectForKey:@"ItemReadmore"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_BOTTOMBAR_ITEM_READMODE_TAG hidden:[val boolValue]];
        }
        
        val = [_bottomToolbarConfig objectForKey:@"ItemSignature"] ;
        
        if (val != NULL) {
            [extensionsManager setToolbarItemHiddenWithTag:FS_BOTTOMBAR_ITEM_SIGNATURE_TAG hidden:[val boolValue]];
        }
    }
}

// MARK: - UIExtensionsManagerDelegate
- (void)uiextensionsManager:(UIExtensionsManager *)uiextensionsManager setTopToolBarHidden:(BOOL)hidden {
    UIToolbar *topToolbar = extensionsManager.topToolbar;
    UIView *topToolbarWrapper = topToolbar.superview;
    id topGuide = pdfViewController.topLayoutGuide;
    assert(topGuide);
    
    [pdfViewCtrl removeConstraints:topToolbarVerticalConstraints];
    if (!hidden) {
        NSMutableArray *contraints = @[].mutableCopy;
        [contraints addObjectsFromArray:
         [NSLayoutConstraint constraintsWithVisualFormat:@"V:[topGuide]-0-[topToolbar(44)]"
                                                 options:0
                                                 metrics:nil
                                                   views:NSDictionaryOfVariableBindings(topToolbar, topGuide)]];
        [contraints addObjectsFromArray:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-0-[topToolbarWrapper]"
                                                                                options:0
                                                                                metrics:nil
                                                                                  views:NSDictionaryOfVariableBindings(topToolbarWrapper)]];
        topToolbarVerticalConstraints = contraints;
        
    } else {
        topToolbarVerticalConstraints = [NSLayoutConstraint constraintsWithVisualFormat:@"V:[topToolbarWrapper]-0-[topGuide]"
                                                                                options:0
                                                                                metrics:nil
                                                                                  views:NSDictionaryOfVariableBindings(topToolbarWrapper, topGuide)];
    }
    [pdfViewCtrl addConstraints:topToolbarVerticalConstraints];
    [UIView animateWithDuration:0.3
                     animations:^{
                         [pdfViewCtrl layoutIfNeeded];
                     }];
}

- (void)wrapTopToolbar {
    // let status bar be translucent. top toolbar is top layout guide (below status bar), so we need a wrapper to cover the status bar.
    UIToolbar *topToolbar = extensionsManager.topToolbar;
    [topToolbar setTranslatesAutoresizingMaskIntoConstraints:NO];
    
    UIView *topToolbarWrapper = [[UIToolbar alloc] init];
    [topToolbarWrapper setTranslatesAutoresizingMaskIntoConstraints:NO];
    [pdfViewCtrl insertSubview:topToolbarWrapper belowSubview:topToolbar];
    [topToolbarWrapper addSubview:topToolbar];
    
    [pdfViewCtrl addConstraints:
     [NSLayoutConstraint constraintsWithVisualFormat:@"H:|-0-[topToolbarWrapper]-0-|"
                                             options:0
                                             metrics:nil
                                               views:NSDictionaryOfVariableBindings(topToolbarWrapper)]];
    [topToolbarWrapper addConstraints:
     [NSLayoutConstraint constraintsWithVisualFormat:@"H:|-0-[topToolbar]-0-|"
                                             options:0
                                             metrics:nil
                                               views:NSDictionaryOfVariableBindings(topToolbar)]];
    [topToolbarWrapper addConstraints:
     [NSLayoutConstraint constraintsWithVisualFormat:@"V:[topToolbar]-0-|"
                                             options:0
                                             metrics:nil
                                               views:NSDictionaryOfVariableBindings(topToolbar)]];
}

// MARK: - IDocEventListener
- (void)onDocOpened:(FSPDFDoc *)document error:(int)error {
    
}

- (void)onDocClosed:(FSPDFDoc *)document error:(int)error {
    [rootViewController dismissViewControllerAnimated:YES completion:nil];
}

@end
