# coding: utf-8
# Copyright (c) Foxit Software Inc..
#
# This source code is licensed under the MIT license found in the
# LICENSE file in the root directory of this source tree.
require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name           = 'RNFoxitPDF'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.author         = package['author']
  s.homepage       = "https://github.com/foxitsoftware/react-native-foxitpdf#readme"
  s.license        = "MIT"
  s.source         = { :git => 'https://github.com/foxitsoftware/react-native-foxitpdf.git' }
  s.platform       = :ios, '10.0'
  s.source_files   = "lib/ios/RNFoxitPDF/*.{h,m}"
  s.header_dir     = "RNFoxitPDF"
  s.dependency 'React'
  s.dependency 'FoxitPDF'
end