import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { NativeModules } from 'react-native';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
var PDFManager = NativeModules.PDFManager;

export default class FoxitPDF {
  static openPDF(src, password, uiConfig)  {
       PDFManager.openPDF(src,password,uiConfig);
  }

  static initialize(SN, key){
       PDFManager.initialize(SN, key);
  }

  static openDocument(src, password, uiConfig)  {
       PDFManager.openPDF(src,password,uiConfig);
  }

}
