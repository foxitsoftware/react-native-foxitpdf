# react-native-foxitpdf [![npm version](https://img.shields.io/npm/v/@foxitsoftware/react-native-foxitpdf.svg?style=flat)](https://www.npmjs.com/package/@foxitsoftware/react-native-foxitpdf)

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

## Integration for iOS

1. Unzip Foxit PDF SDK for iOS and copy `libs` folder into the component’s `<PROJECT_ROOT>/ios` folder.
```
<PROJECT_ROOT>/ios/libs/<frameworks>
```
Please use foxitpdfsdk_(version_no)_ios.zip from https://developers.foxitsoftware.com/pdf-sdk/ios/

2.Create a `FoxitPDF.podspec` file and place it in the `libs` folder

```
pod spec create FoxitPDF
```
```
<PROJECT_ROOT>/ios/libs/FoxitPDF.podspec
```
FoxitPDF.podspec example 
```ruby
# coding: utf-8
# Copyright (c) Foxit Software Inc..

Pod::Spec.new do |s|
s.name           = 'FoxitPDF'
s.version        = '7.1.0'
s.summary        = 'Foxit PDF SDK provides high-performance libraries to help any software developer add robust PDF functionality to their enterprise, mobile and cloud applications across all platforms (includes Windows, Mac, Linux, Web, Android, iOS, and UWP), using the most popular development languages and environments. Application developers who use Foxit PDF SDK can leverage Foxit’s powerful, standard-compliant PDF technology to securely display, create, edit, annotate, format, organize, print, share, secure, search documents as well as to fill PDF forms. Additionally, Foxit PDF SDK includes a built-in, embeddable PDF Viewer, making the development process easier and faster. For more detailed information, please visit the website https://developers.foxitsoftware.com/pdf-sdk/'
s.author         = 'Foxit Software Incorporated'
s.homepage       = 'https://developers.foxitsoftware.com/pdf-sdk/ios/'
s.platform       = :ios, '9.0'
s.license        = 'MIT'
s.source         = { :git => '' }
s.subspec 'FoxitRDK' do |ss|
ss.source_files  = 'FoxitRDK.framework/Headers/**.h'
ss.public_header_files =  'FoxitRDK.framework/Headers/**.h'
ss.vendored_frameworks = 'FoxitRDK.framework'
end
s.subspec 'uiextensionsDynamic' do |ss|
ss.source_files  =  'uiextensionsDynamic.framework/Headers/**.h',
ss.public_header_files =   'uiextensionsDynamic.framework/Headers/**.h'
ss.vendored_frameworks =  'uiextensionsDynamic.framework'
ss.dependency 'FoxitPDF/FoxitRDK'
end
end
```
3. Add `FoxitPDF` to `<PROJECT_ROOT>/ios/Podfile` 
```ruby
pod 'FoxitPDF', :path=>'./libs/FoxitPDF.podspec'
```
4.
```
cd <PROJECT_ROOT>/ios && pod install
```

## Integration for Android

1. Download foxit_mobile_pdf_sdk_android_en.zip from [https://developers.foxitsoftware.com/pdf-sdk/android/] (Please use Foxit PDF SDK for Android 7.1.0 )

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

In your root `android/app/build.gradle`:
```diff
    ```
    android {
         ```
         defaultConfig {
             applicationId "xxx.xxx.xxx"
             minSdkVersion rootProject.ext.minSdkVersion
             targetSdkVersion rootProject.ext.targetSdkVersion
             versionCode 1
             versionName "1.0"
+            multiDexEnabled true
         }
         ```
    }
    
    ```
    dependencies {
        implementation fileTree(dir: "libs", include: ["*.jar"])
        implementation "com.facebook.react:react-native:+"  // From node_modules
+       implementation 'com.android.support:multidex:1.0.+'
    
        if (enableHermes) {
          def hermesPath = "../../node_modules/hermesvm/android/";
          debugImplementation files(hermesPath + "hermes-debug.aar")
          releaseImplementation files(hermesPath + "hermes-release.aar")
        } else {
          implementation jscFlavor
        }
    }
    ```
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
>[v7.1.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.1)

>[v7.0.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.0)

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
