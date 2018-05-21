//
//  RNTPDF.h
//  FoxitReader
//
//  Created by user on 4/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <React/RCTView.h>


@class RCTEventDispatcher;

@interface RNTPDF : UIView

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher;

@end
