const singleSpaAngularWebpack = require('single-spa-angular/lib/webpack').default;

module.exports = (config, options) => {
  // Let single-spa-angular handle the default configuration
  // It outputs UMD format which SystemJS can load
  const singleSpaWebpackConfig = singleSpaAngularWebpack(config, options);

  return singleSpaWebpackConfig;
};
