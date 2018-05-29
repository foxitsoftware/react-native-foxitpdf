import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { NativeModules } from 'react-native';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
var PDFManager = NativeModules.PDFManager;

export default class FoxitPDF {
  static openPDF(
    src,
    password,
    extensionConfig,
    enableTopToolbar,
    enableBottomToolbar,
    topToolbarConfig,
    bottomToolbarConfig,
    panelConfig,
    viewSettingsConfig,
    viewMoreConfig
  ) {
    const source = resolveAssetSource(src) || {};

    let uri = source.uri || '';
    if (uri && uri.match(/^\//)) {
      uri = `file://${uri}`;
    }

    if (arguments.length < 4) {
      enableTopToolbar = true;
    }
    if (arguments.length < 5) {
      enableBottomToolbar = true;
    }
    PDFManager.openPDF(
      uri,
      password,
      extensionConfig,
      enableTopToolbar,
      enableBottomToolbar,
      topToolbarConfig,
      bottomToolbarConfig,
      panelConfig,
      viewSettingsConfig,
      viewMoreConfig
    );
  }
}
