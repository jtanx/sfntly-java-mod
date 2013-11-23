## sfntly (Java version) with mods
This repository contains a modified version of [sfntly](http://code.google.com/p/sfntly/) that is hopefully more resilient and robust to loading a wider range of fonts from around the web. It also aims to be better at subsetting fonts, with better compliance with the OpenType spec. 

## Builds (binaries)
See [here](https://github.com/jtanx/sfntly-java-mod/releases) for builds. These have been built with debugging enabled, so the stack traces are useful when something goes wrong.

## Building
Use ant to build sfntly (as per original install instructions). Since the libs folder is not included in this repository, you need to download and install the files located in [this](http://code.google.com/p/sfntly/source/browse/#svn%2Ftrunk%2Fjava%2Flib) folder.
