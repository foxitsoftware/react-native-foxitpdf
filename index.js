import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  StyleSheet,
  requireNativeComponent,
  NativeModules,
  View,
  ViewPropTypes,
  Image,
} from 'react-native';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';

const RCTPDF = requireNativeComponent('RNTPDF', null);

export default class FoxitPDFView extends Component {
  constructor(props) {
    super(props);

    this.defaultUIExtension = {
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
    };
  }

  render() {
    const source = resolveAssetSource(this.props.source) || {};

    let uri = source.uri || '';
    if (uri && uri.match(/^\//)) {
      uri = `file://${uri}`;
    }

    const nativeProps = Object.assign({}, this.props);
    Object.assign(nativeProps, { src: uri });

    // if (this.props.enableBottomToolbar === undefined) {
    //     Object.assign(nativeProps, {
    //         enableBottomToolbar: true
    //     })
    // }
    // if (this.props.enableTopToolbar === undefined) {
    //     Object.assign(nativeProps, {
    //         enableTopToolbar: true
    //     })
    // }

    // if (this.props.extensionConfig === undefined) {
    //     const extensionConfig = this.defaultUIExtension;
    //     Object.assign(nativeProps, {
    //         src,
    //         extensionConfig
    //     });
    // } else {
    //     Object.assign(nativeProps, {
    //         src
    //     });
    // }

    // alert(JSON.stringify(nativeProps));

    return <RCTPDF {...nativeProps} />;
  }
}

FoxitPDFView.prototypse = {
  source: PropTypes.oneOfType([
    PropTypes.shape({
      uri: PropTypes.string,
    }),
    // Opaque type returned by require('./video.mp4')
    PropTypes.number,
  ]),
  password: PropTypes.string,
  // ui extensions
  extensionConfig: PropTypes.shape({
    readingbookmark: PropTypes.bool,
    thumbnail: PropTypes.bool,
    attachment: PropTypes.bool,
    signature: PropTypes.bool,
    search: PropTypes.bool,
    pageNavigation: PropTypes.bool,
    form: PropTypes.bool,
    encryption: PropTypes.bool,
    tools: PropTypes.shape({
      Insert: PropTypes.bool,
      Eraser: PropTypes.bool,
      Line: PropTypes.bool,
      Arrow: PropTypes.bool,
      Selection: PropTypes.bool,
      Underline: PropTypes.bool,
      Replace: PropTypes.bool,
      StrikeOut: PropTypes.bool,
      Note: PropTypes.bool,
      Highlight: PropTypes.bool,
      Squiggly: PropTypes.bool,
      Oval: PropTypes.bool,
      Freetext: PropTypes.bool,
      Stamp: PropTypes.bool,
      Rectangle: PropTypes.bool,
      Pencil: PropTypes.bool,
      Textbox: PropTypes.bool,
      Polygon: PropTypes.bool,
      Cloud: PropTypes.bool,
      Image: PropTypes.bool,
      Distance: PropTypes.bool,
    }),
  }),
  // show hide bottom, top toolbar
  enableTopToolbar: PropTypes.bool,
  enableBottomToolbar: PropTypes.bool,
  // hidden config for top tool bar
  topToolbarConfig: {
    ItemMore: PropTypes.bool,
    ItemBack: PropTypes.bool,
    ItemBookmark: PropTypes.bool,
    ItemSearch: PropTypes.bool,
  },
  // hidden config for bottom bar
  bottomToolbarConfig: {
    ItemAnnot: PropTypes.bool,
    ItemPanel: PropTypes.bool,
    ItemReadmore: PropTypes.bool,
    ItemSignature: PropTypes.bool,
  },
  //panel config
  panelConfig: {
    readingBookmark: PropTypes.bool,
    outline: PropTypes.bool,
    annotation: PropTypes.bool,
    attachments: PropTypes.bool,
  },
  //Customizing to show/hide the UI elements in the View setting bar
  viewSettingsConfig: {
    singlePage: PropTypes.bool,
    continuousPage: PropTypes.bool,
    thumbnail: PropTypes.bool,
    autoBrightness: PropTypes.bool,
    night: PropTypes.bool,
    reflow: PropTypes.bool,
    screenLock: PropTypes.bool,
    crop: PropTypes.bool,
    panAndZoom: PropTypes.bool,
    facing: PropTypes.bool, // iPad only
  },
  viewMoreConfig: {
    groupFile: PropTypes.bool,
    groupProject: PropTypes.bool,
    groupForm: PropTypes.bool,
    groupFileConfig: PropTypes.shape({
      itemFileInfo: PropTypes.bool,
      itemReduceFileSize: PropTypes.bool,
      itemWirelessPrint: PropTypes.bool,
      itemCrop: PropTypes.bool,
    }),
    groupProjectConfig: PropTypes.shape({
      itemPassword: PropTypes.bool,
    }),
    groupFormConfig: PropTypes.shape({
      itemResetForm: PropTypes.bool,
      itemImportForm: PropTypes.bool,
      itemExportForm: PropTypes.bool,
    }),
  },
};
