module.exports = {
  // config for a library is scoped under "dependency" key
  dependency: {
    platforms: {
      ios: {},
      android: {
        sourceDir: './lib/android',
      }, // projects are grouped into "platforms"
    },
  },
};