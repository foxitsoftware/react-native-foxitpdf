require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name           = 'FoxitPDF'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.author         = package['author']
  s.homepage       = "https://github.com/andrew9032/react-native-foxit-pdf#readme"
  s.license        = "MIT"
  s.source         = { :git => 'https://github.com/andrew9032/react-native-foxit-pdf' }
  s.platform       = :ios, '11.0'
  s.source_files  = "lib/ios/FoxitPDF/*.{h,m}"
  s.dependency 'React/Core'
end