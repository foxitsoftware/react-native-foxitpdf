//
//  PDFManager.h
//  FoxitPDF
//
//  Created by 叶志强 on 2018/12/4.
//  Copyright © 2018年 Foxit Corporation. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FoxitRDK/FSPDFViewControl.h>
#import <uiextensionsDynamic/uiextensionsDynamic.h>

NS_ASSUME_NONNULL_BEGIN

@interface RNTPDFManager : NSObject
+ (FSErrorCode)initialize:(NSString *)sn key:(NSString *)key;
@end

NS_ASSUME_NONNULL_END
