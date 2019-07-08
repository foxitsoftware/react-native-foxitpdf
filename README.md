# react-native-foxitpdf [![npm version](https://img.shields.io/npm/v/react-native-foxitpdf.svg?style=flat)](https://www.npmjs.com/package/react-native-foxitpdf)

react-native-foxitpdf is Foxit's first React Native PDF component for iOS and Android. It uses Foxit PDF SDK for Android/iOS technology to view, render and edit PDFs easily. 

- [Installation](#installation)
- [Integration for iOS](#integration-for-ios)
- [Integration for Android](#integration-for-android)
- [General Usage](#general-usage)
- [API Reference](#api-reference)
- [Versions](#versions)
- [License](#license)

## Installation

First, download the library from npm and install inside your current project folder

```
npm install @foxitsoftware/react-native-foxitpdf
```

Link the project to the library automatically

```
react-native link @foxitsoftware/react-native-foxitpdf
```

## Integration for iOS

1. Unzip Foxit PDF SDK for iOS and copy `libs` folder into the component’s `<PROJECT_ROOT>/ios` folder.
```
<PROJECT_ROOT>/ios/libs/<frameworks>
```
Please use foxitpdfsdk_(version_no)_ios.zip from https://developers.foxitsoftware.com/pdf-sdk/ios/

2. Target -> General -> Embedded Binaries

   Add dynamic framework "FoxitRDK.framework" and "uiextensionsDynamic.framework" to framework folder and Xcode’s Embedded Binaries

3. Target -> General -> Linked Frameworks and Libraries ->  +  -> WebKit.framework
  
4.  Add following line into AppDelegate.m

```objc
#import <FoxitPDF/PDFManager.h>
```


## Integration for Android

1. Download foxit_mobile_pdf_sdk_android_en.zip from [https://developers.foxitsoftware.com/pdf-sdk/android/] (Please use Foxit PDF SDK for Android 6.4.0 )

2. Unzip `foxitpdfsdk_(version_no)_android.zip` and copy libs folder into the component android folder.
/xxx/platforms/android/

3. In your root `android/build.gradle`:
```diff
    allprojects {
        repositories {
            mavenLocal()
+           google()
            jcenter()
            maven {
                // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
                url "$rootDir/../node_modules/react-native/android"
            }
+            flatDir {
+               dirs project(':@foxitsoftware_react-native-foxitpdf').file("$rootDir/libs")
+           }
        }
    }
```  

4. Add `uses-permission` ,`PDFReaderActivity` and `tools:replace` to your `android/app/src/main/AndroidManifest.xml`.
```diff
   <manifest xmlns:android="http://schemas.android.com/apk/res/android"
+             xmlns:tools="http://schemas.android.com/tools"
              package="com.foxitreact">
   
+      <uses-permission android:name="android.permission.INTERNET"/>
+      <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
+      <uses-permission android:name="android.permission.VIBRATE"/>
+      <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
+      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
+      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
+      <uses-permission android:name="android.permission.RUN_INSTRUMENTATION"/>
+      <uses-permission android:name="android.permission.CAMERA"/>
+      <uses-permission android:name="android.permission.RECORD_AUDIO"/>
   
       <application
           android:name=".MainApplication"
           android:allowBackup="false"
           android:icon="@mipmap/ic_launcher"
           android:label="@string/app_name"
           android:roundIcon="@mipmap/ic_launcher_round"
           android:theme="@style/AppTheme"
+          tools:replace="android:allowBackup,icon,theme,label,name">
           
+          <activity
+              android:name="com.foxitreader.PDFReaderActivity"
+              android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
+              android:screenOrientation="fullSensor"/>
           <activity
               android:name=".MainActivity"
               android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
               android:label="@string/app_name"
               android:windowSoftInputMode="adjustResize">
               <intent-filter>
                   <action android:name="android.intent.action.MAIN"/>
                   <category android:name="android.intent.category.LAUNCHER"/>
               </intent-filter>
           </activity>
           <activity android:name="com.facebook.react.devsupport.DevSettingsActivity"/>
       </application>
   </manifest>
```

5. Please update you Android Gradle plugin to `3.1.0+`, and update the version of Gradle to `4.4+`, you can refer to [https://developer.android.com/studio/releases/gradle-plugin].


## General Usage

In your App.js file, you can import the component using the following code:

1.Import FoxitPDF
```js
import FoxitPDF from '@foxitsoftware/react-native-foxitpdf';
```

2.Initialize the library.  The `foxit_sn` is `rdk_sn`, `foxit_key` is `rdk_key` and they can be found in the libs folder of Foxit PDF SDK.
```js
FoxitPDF.initialize("foxit_sn","foxit_key");
```

3.Once the component is initialized, call the function below to open document:
```js
FoxitPDF.openDocument('sample.pdf');
```

In the openDocument function parameter, add the path to the file you wish to open.

If you are using iOS version: Add the name of the PDF file, but make sure it is located under app Document folder

If you are using Android version: `Please input the absolute path of the file in the devices, e.g., FoxitPDF.openDocument('/mnt/sdcard/xxx/xxx.pdf')`



In `App.js`:
```javascript
import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  NativeModules,
  TouchableOpacity,
} from 'react-native';

import FoxitPDF from '@foxitsoftware/react-native-foxitpdf';

type Props = {};
export default class App extends Component<Props> {

  constructor(props) {
     super(props);

     FoxitPDF.initialize("foxit_sn","foxit_key");
  }

  onPress() {
    FoxitPDF.openDocument('/sample.pdf');
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.onPress}>
          <Text>Open PDF</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});
```

## API Reference
**Initialize Foxit PDF SDK**

	FoxitPDF.initialize(String, String); // foxit_sn and foxit_key

**Open a pdf document**

	FoxitPDF.openDocument(String, String) // path and password
## Versions

>[v6.4.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.4.0)

>[v6.3.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.3)

>[v6.2.1](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.2.1)

>[v6.2](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.2)

>[v6.1](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.1)

>[v6.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.0)

## License

     Copyright (c) 2019 Foxit Corporation

     Licensed under the The MIT License (MIT) (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        https://raw.githubusercontent.com/foxitsoftware/react-native-foxitpdf/master/LICENSE

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
