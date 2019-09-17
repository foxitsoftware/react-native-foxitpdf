module.exports = {
  // config for a library is scoped under "dependency" key
  dependency: {
    platforms: {
      ios:{
        podspecPath : `${__dirname}/RNFoxitPDF.podspec`,
      },
      android: {
        sourceDir: './lib/android',
      }, // projects are grouped into "platforms"
    },
  },
};