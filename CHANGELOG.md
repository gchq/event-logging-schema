# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

i.e. `<Major version>.<Minor version>.<Patch version>`

The namespace of the schema includes the major version, e.g. _event-logging:3_. This is to reflect the fact that a change to the major version number is a breaking change and thus a different namespace should be used. Similarly the filename of the schema as a release artifact will include the major version number to reflect a breaking change.

Minor and patch versions will be backwards compatible with other versions at the same major version number. The minor and patch version numbers are included in the _version_ and _id_ attributes of the _xs:schema_ element.

Minor version changes may included new optional elements or attributes. They may also include changes to such things as enumerations or patterns that are additive in nature.

Patch version changes will typically include cosmetic changes (e.g. _xs:documentation_ changes).

## [Unreleased]


## [v3.3.1] - 2019-01-23

* No changes to the schema.

### Changed

* Change the schema generator to appy the version of the generated schema to the id attribute and the filename.


## [v3.3.0] - 2019-01-14

### Added

* Issue **#33** : Add content to ClassificationComplexType to support richer protective marking schemes

### Changed

* Change `name` to `pipelineName` in Schema Generator `configuration.yml`.

* Change `suffix` to `outputSuffix` in Schema Generator `configuration.yml`.

* Add `outputBaseName` to Schema Generator `configuration.yml` to allow the filename and if of the output schema to be changed.


## [v3.2.4] - 2018-02-13

### Changed

* Add the pipeline suffix to the end of `id` attribute value on the `schema` element. This provides a means of differentiating the different forms of the schema.


## [v3.2.3] - 2018-01-30

* No changes to the schema.

### Changed

* Change transformer to support pipeline inheritance

* Add diff-ing of released and generated schemas to build script


## [v3.2.2] - 2018-01-12

### Changed

* Fix broken junit test in the transformer code

* No changes to the schema.


## [v3.2.1] - 2018-01-12

### Changed

* Change transformer code to accept the sourceSchema path as an argument to the jar rather than in the configuration yml.

* No changes to the schema.


## [v3.2.0] - 2017-12-21

### Added

* Issue **#23** : Added optional `Coordinates` element to `LocationComplexType` to capture lat/long

* Add a build step to zip up the XSLTs used by the transformer

* Add the transformer fat jar and transformations zip to the github release artifacts

### Changed

* Improve logging and error handling in the schema transformer


## [v3.1.2] - 2017-11-14

### Added

* Add a client version of the schema for use in the [event-logging jaxb library](https://github.com/gchq/event-logging)

* Add a transformation pipeline process for running multiple XSLTs against the schema

## [v3.1.1] - 2017-07-17


### Changed

* Issue **#18** : Remove `pattern` from `VersionSimpleType` as this is trumped by the enumerations. Add past versions as enumerations.


## [v3.1.0] - 2017-07-12

### Added

* Issue **#16** : Add _Data_ element to _PrintSettings_ element

* Issue **#13** : Add _Group_ to the list of items an _Authenticate_ action can occur on

* Issue **#12** : Add _ElevatePrivilege_ and _Other_ to list of _Authenticate_ Actions

* Issue **#6** : Add _PauseJob_, _ResumeJob_, _FailedPrint_ and _Other_ to _PrintActionSimpleType_

* Issue **#4** : Extend _ObjectOutcomeComplexType_ to have _Data_ sub elements

### Changed

* Issue **#5** : Change certain instances of _xs:positiveInteger_ to _xs:nonNegativeInteger_ to allow zero values


## [v3.0.0] - 2016-10-31

### Added

* Initial open source release

[Unreleased]: https://github.com/gchq/event-logging-schema/compare/v3.3.1...HEAD
[v3.3.1]: https://github.com/gchq/event-logging-schema/compare/v3.3.0...v3.3.1
[v3.3.0]: https://github.com/gchq/event-logging-schema/compare/v3.2.4...v3.3.0
[v3.2.4]: https://github.com/gchq/event-logging-schema/compare/v3.2.3...v3.2.4
[v3.2.3]: https://github.com/gchq/event-logging-schema/compare/v3.2.2...v3.2.3
[v3.2.2]: https://github.com/gchq/event-logging-schema/compare/v3.2.1...v3.2.2
[v3.2.1]: https://github.com/gchq/event-logging-schema/compare/v3.2.0...v3.2.1
[v3.2.0]: https://github.com/gchq/event-logging-schema/compare/v3.1.2...v3.2.0
[v3.1.2]: https://github.com/gchq/event-logging-schema/compare/v3.1.1...v3.1.2
[v3.1.1]: https://github.com/gchq/event-logging-schema/compare/v3.1.0...v3.1.1
[v3.1.0]: https://github.com/gchq/event-logging-schema/compare/v3.0.0...v3.1.0
[v3.0.0]: https://github.com/gchq/event-logging-schema/compare/v3.0.0...v3.0.0
