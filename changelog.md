# Changelog
All notable changes to `cnj-k8s-tools` will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - YYYY-MM-DD
### Added
### Changed
### Fixed

## [5.0.0] - 2023-11-09
### Added
- added new Mojos for helm lint, helm package, helm push and helm pull commands.
### Changed
- upgraded to Java 17
- upgraded all dependencies not related to Maven
- Goal deploy now prints complete helm command line with redacted sensitive arguments

## [4.0.0] - 2021-10-20
### Added
### Changed
- switched to Java 11.
- upgraded to Maven 3.6.1 API.

## [3.0.0] - 2021-04-15
### Added
### Changed
- moved to new AWS CloudTrain environment.

## [2.0.1] - 2020-02-19
### Added
### Changed
- goal `uninstall` does not require configuration parameter `chartDirectory` anymore.

## [2.0.0] - 2020-02-17
### Added
### Changed
- upgraded to Helm 3 causing breaking changes to plugin configuration.