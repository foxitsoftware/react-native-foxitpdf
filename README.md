# react-native-foxitpdf [![npm version](https://img.shields.io/npm/v/@foxitsoftware/react-native-foxitpdf.svg?style=flat)](https://www.npmjs.com/package/@foxitsoftware/react-native-foxitpdf)

react-native-foxitpdf is Foxit's first React Native PDF component for iOS and Android. It uses Foxit PDF SDK for Android/iOS technology to view, render and edit PDFs easily, and it can open pdf files on local or url.

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

2. Add `FoxitPDF` to `<PROJECT_ROOT>/ios/Podfile` 
```ruby
pod 'FoxitPDF', :path=>'./libs/FoxitPDF.podspec'
```
3.
```
cd <PROJECT_ROOT>/ios && pod install
```

## Integration for Android

1. Download foxit_mobile_pdf_sdk_android_en.zip from [https://developers.foxitsoftware.com/pdf-sdk/android/] (Please use the latest version)

2. Unzip `foxitpdfsdk_(version_no)_android.zip` and copy libs folder into the component android folder.
<PROJECT_ROOT>/android/

3. In your root `android/build.gradle`:
```diff
    buildscript {
        ...
    }
        
+   allprojects {
+       repositories {
+           flatDir {
+               dirs project(':foxitsoftware_react-native-foxitpdf').file("$rootDir/libs")           
+           }
+           maven {
+                url 'https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1'
+           }
+        }
+   }
```  

4. Add `tools:replace` to your `android/app/src/main/AndroidManifest.xml`.
```diff
   <manifest xmlns:android="http://schemas.android.com/apk/res/android"
+             xmlns:tools="http://schemas.android.com/tools"
              package="com.foxitreact">
   
       <uses-permission android:name="android.permission.INTERNET"/>
   
       <application
           android:name=".MainApplication"
           android:allowBackup="false"
           android:icon="@mipmap/ic_launcher"
           android:label="@string/app_name"
           android:roundIcon="@mipmap/ic_launcher_round"
           android:theme="@style/AppTheme"
+          tools:replace="android:allowBackup,icon,theme,label,name">
           
           <activity
               android:name=".MainActivity"
               android:configChanges="keyboard|keyboardHidden|orientation|screenSize|uiMode"
               android:exported="true"
               android:label="@string/app_name"
               android:launchMode="singleTask"
               android:windowSoftInputMode="adjustResize">
               <intent-filter>
                   <action android:name="android.intent.action.MAIN"/>
                   <category android:name="android.intent.category.LAUNCHER"/>
               </intent-filter>
           </activity>
       </application>
   </manifest>
```

## General Usage

In your App.tsx file, you can import the component using the following code:

1.Import FoxitPDF
```js
import FoxitPDF from '@foxitsoftware/react-native-foxitpdf';
```

2.Initialize the library.  The `foxit_sn` is `rdk_sn`, `foxit_key` is `rdk_key` and they can be found in the libs folder of Foxit PDF SDK.
```js
FoxitPDF.initialize("foxit_sn","foxit_key");
```

3.Once the component is initialized, call the function below to open document from local path:
```js
FoxitPDF.openDocument('sample.pdf');
```

In the openDocument function parameter, add the path to the file you wish to open.

If you are using iOS version: Add the name of the PDF file, but make sure it is located under app Document folder

If you are using Android version: `Please input the absolute path of the file in the devices, e.g., FoxitPDF.openDocument('/storage/emulated/0/xxx/xxx.pdf')`


4.Call the function below to open document from URL：
```js
FoxitPDF.openDocFromUrl(url);
```
e.g.
```js
FoxitPDF.openDocFromUrl('https://developers.foxitsoftware.com/resources/pdf-sdk/FoxitPDFSDK_QuickGuide(PDFium).pdf');
```


In `App.tsx`:
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
   //open doc from local path
    FoxitPDF.openDocument('/sample.pdf');
    
    // open doc from url
    //FoxitPDF.openDocFromUrl('');
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

**Open a pdf document from local**

	FoxitPDF.openDocument(String, String) // path and password

**Open a pdf document from url**

	FoxitPDF.openDocFromUrl(String, String) // path and password
	
## Versions
>[v9.0.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/V9.0.0)

>[v8.4.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/v8.4.0)

>[v8.3.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/V8.3.0)

>[v8.2.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/V8.2.0)

>[v8.1.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/V8.1.0)

>[v8.0.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/v8.0)

>[v7.5.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.5)

>[v7.4.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.4)

>[v7.3.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.3)

>[v7.2.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.2)

>[v7.1.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.1)

>[v7.0.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/7.0)

>[v6.4.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.4.0)

>[v6.3.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.3)

>[v6.2.1](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.2.1)

>[v6.2](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.2)

>[v6.1](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.1)

>[v6.0](https://github.com/foxitsoftware/react-native-foxitpdf/tree/release/6.0)

## License

     Copyright (c) 2023 Foxit Corporation

     Licensed under the The MIT License (MIT) (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        https://raw.githubusercontent.com/foxitsoftware/react-native-foxitpdf/master/LICENSE

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
