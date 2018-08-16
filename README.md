# react-native-foxitpdf [![npm version](https://img.shields.io/npm/v/react-native-foxitpdf.svg?style=flat)](https://www.npmjs.com/package/react-native-foxitpdf)

Foxit PDF components for iOS + Android

## Installation

First, download the library from npm:

```
npm install @foxitsoftware/react-native-foxitpdf --save
```

Link automatically

```
react-native link @foxitsoftware/react-native-foxitpdf
```

### iOS

1.  Unzip Foxit iOS sdk and copy libs folder into iOS folder.  (Please use FoxitRDK 6.0 )
2.  Add dynamic framework "FoxitRDK.framework" and also in Xcode’s Embedded Binaries
3.  Add the Resource files that are needed for the built-in UI implementations to the pdfreader project. Right-click the project, and select Add Files to "your project"… to add the Resource files. Find and choose the folder "libs/uiextensions_src/uiextensions/Resource".
4.  Add following line into AppDelegate.m

```objc
#import "FoxitRDK/FSPDFObjC.h"
```

5.  At the end of `didFinishLaunchingWithOptions` function, add the following lines.

```objc
NSString *sn = @"xxx";
NSString *key = @"xxx";
FSErrorCode eRet = [FSLibrary initialize:sn key:key];
if (FSErrSuccess != eRet) {
    return NO;
}
```

### Android

1.  Unzip Foxit android sdk and copy libs folder into android folder(Please use `FoxitRDK 6.1` ).
2.  Add the following into project-level build.gradle file(android/build.gradle).

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

3. 
- Add `uses-permission` outside `application` element in `AndroidManifest.xml`.
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:tools="http://schemas.android.com/tools"
          package="your package name">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.RUN_INSTRUMENTATION"/>
    <uses-permission android:name="android.permission.CAMERA" />
    
    <application .../>
</manifest>
```

- Add `sn`, `key`, and `PDFReaderActivity` inside `application` element in `AndroidManifest.xml`. 

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
- Be sure to add `android:allowBackup` in `tools:replace` for `application` element and `xmlns:tools="http://schemas.android.com/tools"` in root `manifest` element.

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
## General Usage

```js
import FoxitPDF from '@foxitsoftware/react-native-foxitpdf';
```

or

```js
var FoxitPDF = require('@foxitsoftware/react-native-foxitpdf');
```

Open PDF Reader

```js
FoxitPDF.openPDF('sample.pdf');
```

the parameter is filepath
in iOS you need put a pdf filepath under app Document
in Android you need put a real pdf filepath.  example FoxitPDF.openPDF('/sdcard/0/download/xxx.pdf')

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
