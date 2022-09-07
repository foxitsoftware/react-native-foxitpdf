/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

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
import uiextensions_config from './uiextensions_config.json';

type Props = {};
export default class App extends Component<Props> {

  constructor(props) {
     super(props);

     FoxitPDF.initialize("foxit_sn","foxit_key");
  }

  onPress() {
    //1: open  local path
    FoxitPDF.openDocument('/sample.pdf','',uiextensions_config);
    //FoxitPDF.openDocFromUrl('/storage/emulated/0/xxx/xxx.pdf','',uiextensions_config); // Android --  should input the absolute path of the file in the devices.

    //2:open url
    // FoxitPDF.openDocFromUrl('https://developers.foxitsoftware.com/resources/pdf-sdk/FoxitPDFSDK_QuickGuide(PDFium).pdf','',uiextensions_config);
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
