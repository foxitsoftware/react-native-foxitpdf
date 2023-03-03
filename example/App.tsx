/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
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
    FoxitPDF.openDocument('/sample.pdf','',uiextensions_config);
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
