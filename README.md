# react-native-foxitpdf [![npm version](https://img.shields.io/npm/v/react-native-foxitpdf.svg?style=flat)](https://www.npmjs.com/package/react-native-foxitpdf)

react-native-foxitpdf is Foxit's first React Native PDF component for iOS and Android. It uses Foxit PDF SDK for Android/iOS technology to view, render and edit PDFs easily. 

## Installation

First, download the library from npm and install inside your current project folder

```
npm install @foxitsoftware/react-native-foxitpdf --save
```

Link the project to the library automatically

```
react-native link @foxitsoftware/react-native-foxitpdf
```

### iOS

1.  Unzip Foxit PDF SDK for iOS and copy libs folder into the component ios folder.  (Please use Foxit PDF SDK for iOS 6.2 )
2.  Add dynamic framework "FoxitRDK.framework" and "uiextensionsDynamic.framework" to framework folder and also to Xcode’s Embedded Binaries
3.  Add the Resource files that are needed for the built-in UI implementations to the pdfreader project. Right-click the project, and select Add Files to "your project"… to add the Resource files. Find and choose the folder "libs/uiextensions_src/uiextensions/Resource".
4.  Add following line into AppDelegate.m

```objc
#import <FoxitPDF/PDFManager.h>
```

5.  At the end of `didFinishLaunchingWithOptions` function, add the following lines:

```objc
NSString *sn = @"xxx";
NSString *key = @"xxx";
FSErrorCode eRet = [RNTPDFManager initialize:sn key:key];
if (FSErrSuccess != eRet) {
    return NO;
}
```

### Android

1. Download foxit_mobile_pdf_sdk_android_en.zip from [https://developers.foxitsoftware.com/pdf-sdk/android/] (Please use foxit_mobile_pdf_sdk_android_en.zip for version 6.2.1 )
2. Unzip `foxit_mobile_pdf_sdk_android_en.zip` and copy libs folder into the component android folder.
3. Add the following code into the project-level build.gradle file (android/build.gradle).
    ```gradle
    allprojects {
        repositories {
            ...
            flatDir {
                dirs project(':@foxitsoftware_react-native-foxitpdf').file("$rootDir/libs")
            }
        }
    }
    ```
4. - Add `uses-permission` tag outside `application` tags in `AndroidManifest.xml`.
    ```xml
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              package="your package name">
           <uses-permission android:name="android.permission.INTERNET"/>
           <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
           <uses-permission android:name="android.permission.VIBRATE"/>
           <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
           <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
           <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
           <uses-permission android:name="android.permission.RUN_INSTRUMENTATION"/>
           <uses-permission android:name="android.permission.CAMERA"/>
           <uses-permission android:name="android.permission.RECORD_AUDIO"/>
        
        <application .../>
    </manifest>
    ```
    - Add `sn`, `key`, and `PDFReaderActivity` inside `application` tags in `AndroidManifest.xml`. You may find your sn and key in Foxit PDF SDK for Android download package folder.
    ```xml
    <application
        ...
        tools:replace="android:allowBackup,icon,theme,label,name">
        <meta-data
            android:name="foxit_sn"
            android:value="xxx"/>
        <meta-data
            android:name="foxit_key"
            android:value="xxx"/>
        <activity
            android:name="com.foxitreader.PDFReaderActivity"
            android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
            android:screenOrientation="fullSensor"/>
    ...
    ```
    - Make sure you've added `android:allowBackup` in `tools:replace` inside `application` tags and `xmlns:tools="http://schemas.android.com/tools"` in root `manifest` element.
    ```xml
    <manifest
        ...
        xmlns:tools="http://schemas.android.com/tools">
        ...
      
        <application
            ...
            tools:replace="android:allowBackup,icon,theme,label,name">
            ...
        </application>
        ...
    </manifest>          
    ```
5. Please update you Android Gradle plugin to `3.1.0+`, and update the version of Gradle to `4.4+`, you can refer to [https://developer.android.com/studio/releases/gradle-plugin].


## General Usage

In your App.js file, you can import the component using the following code:

```js
import FoxitPDF from '@foxitsoftware/react-native-foxitpdf';
```

or

```js
var FoxitPDF = require('@foxitsoftware/react-native-foxitpdf');
```

Once the component is initialized, call the function below to open the PDF Reader:

```js
FoxitPDF.openPDF('sample.pdf');
```

In the openPDF function parameter, add the path to the file you wish to open.

If you are using iOS version: Add the name of the PDF file, but make sure it is located under app Document folder

If you are using Android version: `Please input the absolute path of the file in the devices, e.g., FoxitPDF.openPDF('/mnt/sdcard/xxx/xxx.pdf')`

## License

     Copyright (c) 2018 Foxit Corporation

     Licensed under the The MIT License (MIT) (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        https://raw.githubusercontent.com/foxitsoftware/react-native-foxitpdf/master/LICENSE

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
