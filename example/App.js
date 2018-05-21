/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View } from 'react-native';
import FoxitPDFView from 'react-native-foxit-pdf';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <Text style={styles.instructions}>To get started, edit App.js</Text>
        <Text style={styles.instructions}>{instructions}</Text>
        <FoxitPDFView
          style={{ flex: 1 }}
          password={''}
          source={require('./sample.pdf')}
          enableTopToolbar={true}
          extensionConfig={{
            readingBookmark: true,
            thumbnail: true,
            attachment: true,
            signature: true,
            search: true,
            pageNavigation: true,
            form: true,
            encryption: true,
            tools: {
              Insert: true,
              Eraser: true,
              Line: true,
              Arrow: true,
              Selection: true,
              Underline: true,
              Replace: true,
              StrikeOut: true,
              Note: true,
              Highlight: true,
              Squiggly: true,
              Oval: true,
              Freetext: true,
              Stamp: true,
              Rectangle: true,
              Pencil: true,
              Textbox: true,
              Polygon: true,
              Cloud: true,
              Image: true,
              Distance: true,
            },
          }}
          topToolbarConfig={{
            ItemMore: false,
            ItemBack: false,
            ItemBookmark: false,
            ItemSearch: false,
          }}
          bottomToolbarConfig={{
            ItemAnnot: false,
            ItemPanel: false,
            ItemReadmore: false,
            ItemSignature: false,
          }}
          panelConfig={{
            readingBookmark: false,
            outline: false,
            annotation: true,
            attachments: true,
          }}
          viewSettingsConfig={{
            singlePage: false,
            continuousPage: false,
            thumbnail: true,
            autoBrightness: true,
            night: false,
            reflow: false,
            screenLock: true,
            crop: true,
            panAndZoom: true,
            facing: true,
          }}
          viewMoreConfig={{
            groupFile: true,
            groupFormConfig: {
              itemResetForm: true,
            },
          }}
        />
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
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
