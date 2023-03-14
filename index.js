import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { NativeModules } from 'react-native';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
var PDFManager = NativeModules.PDFManager;

export default class FoxitPDF {
  static openPDF(path, password, uiConfig)  {
       PDFManager.openPDF(path, password, uiConfig);
  }

  static initialize(SN, key){
       PDFManager.initialize(SN, key);
  }

  static openDocument(path, password, uiConfig)  {
       PDFManager.openDocument(path, password, uiConfig);
  }

  static openDocFromUrl(url, password, uiConfig)  {
       PDFManager.openDocFromUrl(url, password, uiConfig);
  }

}
