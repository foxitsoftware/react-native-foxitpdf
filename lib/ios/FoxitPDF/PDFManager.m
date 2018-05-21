//
//  PDFManager.m
//  FoxitReader
//
//  Created by user on 4/10/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RNTPDF.h"
#import <FoxitRDK/FSPDFViewControl.h>
#import <React/RCTBridge.h>
#import <React/RCTViewManager.h>

@interface RNTPDFManager : RCTViewManager
@end

@implementation RNTPDFManager

RCT_EXPORT_MODULE()

@synthesize bridge = _bridge;

- (UIView *)view
{
  return [[RNTPDF alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(src, NSString);
RCT_EXPORT_VIEW_PROPERTY(password, NSString);
RCT_EXPORT_VIEW_PROPERTY(extensionConfig, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(enableTopToolbar, BOOL);
RCT_EXPORT_VIEW_PROPERTY(enableBottomToolbar, BOOL);
RCT_EXPORT_VIEW_PROPERTY(topToolbarConfig, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(bottomToolbarConfig, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(panelConfig, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(viewSettingsConfig, NSDictionary);
RCT_EXPORT_VIEW_PROPERTY(viewMoreConfig, NSDictionary);
@end
