# react-native-foxit [![npm version](https://img.shields.io/npm/v/react-native-foxit.svg?style=flat)](https://www.npmjs.com/package/react-native-foxit)

Foxit PDF components for iOS + Android

## Installation

First, download the library from npm:

```
npm install react-native-foxit --save
```

Link automatically

```
react-native link react-native-foxit
```

### iOS

1.  Unzip Foxit iOS sdk and copy libs folder into iOS folder.
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
FSErrorCode eRet = [FSLibrary init:sn key:key];
if (e_errSuccess != eRet) {
    return NO;
}
```

### Android

1.  Unzip Foxit Android sdk and copy libs folder into Android folder.
2.  Add the following into project-level build.gradle file(android/build.gradle).

```gradle
allprojects {
    repositories {
        ...
        flatDir {
            dirs project(':react-native-foxit').file("$rootDir/libs")
        }
    }
}
```

3.  Add the following into module-level gradle file(android/app/build.gradle).

```gradle
dependencies {
    ...
    compile project(':react-native-foxit') {
        android {
            sourceSets {
                main {
                    jniLibs.srcDirs = ['../libs']
                }
            }
        }
    }
}
```

4.  Add sn, key, and `PDFReaderActivity` inside `application` element in `AndroidManifest.xml`. Be sure to add `android:allowBackup` in `tools:replace` for `application` element and `xmlns:tools="http://schemas.android.com/tools"` in root `manifest` element.

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
    <activity android:name="com.foxitreader.PDFReaderActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenSize" />
...
```

## General Usage

```js
import FoxitPDF from 'react-native-foxit';
```

or

```js
var FoxitPDF = require('react-native-foxit');
```

Open PDF Reader

```js
FoxitPDF.openPDF(require('./sample.pdf'));
```

## License

     Copyright (c) 2018 Foxit Corporation

     Licensed under the The MIT License (MIT) (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        https://raw.githubusercontent.com/andrew9032/react-native-foxit/master/LICENSE

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
