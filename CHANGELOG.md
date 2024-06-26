# Change Log

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

i.e. `<Major version>.<Minor version>.<Patch version>`

The namespace of the schema includes the major version, e.g. _event-logging:3_.
This is to reflect the fact that a change to the major version number is a breaking change and thus a different namespace should be used.
Similarly the filename of the schema as a release artifact will include the major version number to reflect a breaking change.

Minor and patch versions will be backwards compatible with other versions at the same major version number.
The minor and patch version numbers are included in the _version_ and _id_ attributes of the _xs:schema_ element.

Minor version changes may included new optional elements or attributes.
They may also include changes to such things as enumerations or patterns that are additive in nature.

Patch version changes will typically include cosmetic changes (e.g. _xs:documentation_ changes).


## [Unreleased]

~~~
DO NOT ADD CHANGES HERE - ADD THEM USING log_change.sh
~~~


## [v4.1.0] - 2024-06-19

* Issue **#86** : Change `PermittedOrganisation` to be unbounded within `PermittedOrganisations`.

* Issue **#85** : Add `In` enumeration to `TermConditionSimpleType` to support the SQL `IN` condition.

* Issue **#84** : Change `Hash` in `BaseFileComplexType` to be unbounded to allow multiple hashes for a file to be recorded.

* Issue **#80** : Change `Location.Floor` from `xs:integer` to `xs:string` to allow for floors with names, e.g. `Ground`.

* Issue **#75** : Add `Changes` element to `Update` to allow recording of a change where the before/after state is not known or is too large to record, e.g. adding a user to an long allow-list.

* Issue **#62** : Add the `Id` attribute to `AnyContentComplextType`, e.g. `Meta/@Id`. This is to distinguish between multiple sibling `Meta` elements.

* Issue **#67** : Add optional `Outcome` to `EventDetail/Unknown` to allow the outcome of the event to be recorded.

* Issue **#74** : Add `Date` to `EmailComnplexType`.

* Issue **#69** : Add optional unbounded `Data` element to the following elements or complex types: `AntiMalwareThreatComplexType`, `Door`, `EventTimeComplexType`, `LocationComplexType`, `NetworkOutcomeComplexType` and `SystemComplexType`.

* Refactor element `EventDetail.Unknown` into `UnknownComplexType`. Doesn't impact validity of XML documents.

* Issue **#76** : Add `Data` element to `Permission` to allow for non-enumerated permission types. Add `Create`, `Delete` and `Use` to `PermissionAttributeSimpleType`.


## [v4.0.2] - 2023-11-28

* Fix build. No schema changes.


## [v4.0.1] - 2023-05-10

* Fix missing build artefacts. No change to schema.


## [v4.0.0] - 2023-11-20

* Issue **#68** : Add `<SecutiryDomain>` to `<System>`.

* Add `<SharingData>` element to `<Events>` and `<Event>`.


## [v4.0-beta.10] - 2022-05-26

* Fix automated build.


## [v4.0-beta.9] - 2022-05-26

* Fix automated build.


## [v4.0-beta.8] - 2022-05-25

* Fix automated build.


## [v4.0-beta.7] - 2022-05-25

* Fix automated build scripts.


## [v4.0-beta.6] - 2022-05-25

* Fix spelling mistakes in schema annotations.


## [v4.0-beta.5] - 2021-09-17

* Fix version number in schema.


## [v4.0-beta.4] - 2021-09-17

* Add safe variant of the schema.


## [v4.0-beta.3] - 2021-03-23

* Fix version numbers in schema.


## [v4.0-beta.2] - 2021-03-23

* Tidy up annotations for Copy, Move, Import & Export complex types.

* Issue **#71** : Create `HashComplexType` for `Hash` element.

* Uplift gradle and shadow pluing versions.


## [v4.0-beta.1] - 2020-12-16

* Issue **#58** : Remove `Event/Id`, add `EventSource/EventId` and `EventSource/SessionId`. Improve annotations for `EventChain` and `Activity`.

* Issue **57** : Refactor the schema to improve the xjc generated java code. Remove deprecated elements.

    * Extract new complex type `AuthenticateComplexType` from `Authenticate` element.

    * Extract new complex type `AuthoriseComplexType` from `Authorise` element.

    * Extract new complex type `CopyComplexType` from `CopyMoveComplexType`.

    * Extract new complex type `MoveComplexType` from `CopyMoveComplexType`.

    * Remove complex type `CopyMoveComplexType`.

    * Extract new complex type `CreateComplexType` from `Create` element.

    * Extract new complex type `ViewComplexType` from `View` element.

    * Extract new complex type `DeleteComplexType` from `Delete` element.

    * Extract new complex type `ProcessComplexType` from `Process` element.

    * Extract new complex type `PrintComplexType` from `Print` element.

    * Extract `InstallationGroup` from `InstallComplexType`.

    * Refactor `InstallComplexType` to use `InstallationGroup`.

    * Extract `UninstallComplexType` from `Uninstall` element.

    * Extract new complex type `NetworkEventActionComplexType` from `Network` element.

    * Remove deprecated `AntiMalware` element.

    * Extract new complex type `AlertComplexType` from `Alert` element.

    * Extract `SendReceiveGroup` from `SendReceiveComplexType`.

    * Extract `SendComplexType` from `Send` element.

    * Extract `ReceiveComplexType` from `Receive` element.

    * Extract `MetaDataTagsComplexType` from `Tags` element.

    * Remove `AntiMalwareComplexType`.

    * Merge `BaseAntiMalwareComplexType` into `AntiMalwareThreatComplexType`.

    * Remove `BaseAdvancedQueryItemComplexType`.

    * Extract `BaseMultiObjectGroup` from `BaseMultiObjectComplexType`.

    * Rename `NetworkSrcDstComplexType` to `NetworkLocationComplexType`.

    * Rename `NetworkSrcDstTransportProtocolSimpleType` to `NetworkProtocolSimpleType`.

    * Remove deprecated `SearchResult` and `SearchResultComplexType`.

    * Remove deprecated `EventDetail/Classification`.

    * Add additional `annotation/documentation` elements.

    * Remove unused `FromComplexType`.

    * Add `Keyboard`,`Mouse` and `Webcam` to `HardwareTypeSimpleType`.

    * Add `MemoryCard` to `MediaTypeSimpleType`.

    * Remove deprecated `LocationComplexType/TimeZone`.


## [v3.5.2] - 2020-11-02

* No changes to the schema.

* Fix version enumeration in scheam.


## [v3.5.1] - 2020-10-30

* No changes to the schema.

* Change GitHUb auth token


## [v3.5.0] - 2020-10-30

* Issue **#63**: Allow `EventSource/Door` to be combined with `Device`, `Client` and `Server`.

* Issue **#64**: Change `Door` sub-element constraints to be consistent with documentation.

* Issue **#65**: Add enum values to `AuthenticateActionSimpleType` and `AuthenticateLogonTypeSimpleType` to better describe physical access events


## [v3.4.2] - 2019-04-16

* Issue **#54** : Rename NetworkComplexType to NetworkOutcomeComplexType. Add new NetworkComplexType that just extends BaseNetworkComplexType.

* Add example XML for Network/Close and Alert/Network.


## [v3.4.1] - 2019-04-05

* Change all `Base....` complex types to be `abstract="true"`


## [v3.4.0] - 2019-04-05

* No changes to the schema.

* Add additional junit test for regex escaping.


## [v3.4-beta.1] - 2019-02-04

* Move complete examples into individual files that are validated as part of the build.

* Issue **#10** : Add `SearchResults` to `BaseMultiObjectComplexType` to allow for use cases like `View/SearchResults`. 

* Issue **#10** : Add `Id`, `Name` and `Description` to `QueryComplexType` to allow the linking of query to results.

* Issue **#39** : Add `TimeZoneName` element to `LocationComplexType` to improve the recording of time zone information.

* Issue **#44** : Add `Approval` schema action.

* Issue **#47** : Add `CachedInteractive`, `CachedRemoteInteractive`, `Proxy` and `Other` logon types to `AuthenticateLogonTypeSimpleType`.

* Issue **40**: Add `State`, `City` and `Town` elements to provide more Location detail.

* Improve documentation

* Issue **#49** : Fix broken link to _Illustrative Examples_ in root README.

* Issue **#3** : Add `Type` attribute to `Hash` element in `BaseFileComplexType`.

* Issue **#35** : Add `Meta` element to `Event` and `BaseObjectGroup` to allow extension/decoration.

* Issue **#31** : Add `Tags` element to `BaseObjectGroup`.

* Issue **#37** : Add `Tags` element to `SystemComplexType`.


## [v3.3.1] - 2019-01-23

* No changes to the schema.

* Change the schema generator to appy the version of the generated schema to the id attribute and the filename.


## [v3.3.0] - 2019-01-14

* Issue **#33** : Add content to ClassificationComplexType to support richer protective marking schemes

* Change `name` to `pipelineName` in Schema Generator `configuration.yml`.

* Change `suffix` to `outputSuffix` in Schema Generator `configuration.yml`.

* Add `outputBaseName` to Schema Generator `configuration.yml` to allow the filename and if of the output schema to be changed.


## [v3.2.4] - 2018-02-13

* Add the pipeline suffix to the end of `id` attribute value on the `schema` element. This provides a means of differentiating the different forms of the schema.


## [v3.2.3] - 2018-01-30

* No changes to the schema.

* Change transformer to support pipeline inheritance

* Add diff-ing of released and generated schemas to build script


## [v3.2.2] - 2018-01-12

* Fix broken junit test in the transformer code

* No changes to the schema.


## [v3.2.1] - 2018-01-12

* Change transformer code to accept the sourceSchema path as an argument to the jar rather than in the configuration yml.

* No changes to the schema.


## [v3.2.0] - 2017-12-21

* Issue **#23** : Added optional `Coordinates` element to `LocationComplexType` to capture lat/long

* Add a build step to zip up the XSLTs used by the transformer

* Add the transformer fat jar and transformations zip to the github release artifacts

* Improve logging and error handling in the schema transformer


## [v3.1.2] - 2017-11-14

* Add a client version of the schema for use in the [event-logging jaxb library](https://github.com/gchq/event-logging)

* Add a transformation pipeline process for running multiple XSLTs against the schema


## [v3.1.1] - 2017-07-17

* Issue **#18** : Remove `pattern` from `VersionSimpleType` as this is trumped by the enumerations. Add past versions as enumerations.


## [v3.1.0] - 2017-07-12

* Issue **#16** : Add _Data_ element to _PrintSettings_ element

* Issue **#13** : Add _Group_ to the list of items an _Authenticate_ action can occur on

* Issue **#12** : Add _ElevatePrivilege_ and _Other_ to list of _Authenticate_ Actions

* Issue **#6** : Add _PauseJob_, _ResumeJob_, _FailedPrint_ and _Other_ to _PrintActionSimpleType_

* Issue **#4** : Extend _ObjectOutcomeComplexType_ to have _Data_ sub elements

* Issue **#5** : Change certain instances of _xs:positiveInteger_ to _xs:nonNegativeInteger_ to allow zero values


## [v3.0.0] - 2016-10-31

* Initial open source release

[Unreleased]: https://github.com/gchq/event-logging-schema/compare/v4.1.0...HEAD
[v4.1.0]: https://github.com/gchq/event-logging-schema/compare/v4.0.2...v4.1.0
[v4.0.2]: https://github.com/gchq/event-logging-schema/compare/v4.0.1...v4.0.2
[v4.0.1]: https://github.com/gchq/event-logging-schema/compare/v4.0.0...v4.0.1
[v4.0.0]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.10...v4.0.0
[v4.0-beta.10]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.9...v4.0-beta.10
[v4.0-beta.9]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.8...v4.0-beta.9
[v4.0-beta.8]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.7...v4.0-beta.8
[v4.0-beta.7]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.6...v4.0-beta.7
[v4.0-beta.6]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.5...v4.0-beta.6
[v4.0-beta.5]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.4...v4.0-beta.5
[v4.0-beta.4]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.3...v4.0-beta.4
[v4.0-beta.3]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.2...v4.0-beta.3
[v4.0-beta.2]: https://github.com/gchq/event-logging-schema/compare/v4.0-beta.1...v4.0-beta.2
[v4.0-beta.1]: https://github.com/gchq/event-logging-schema/compare/v3.5.1...v4.0-beta.1
[v3.5.2]: https://github.com/gchq/event-logging-schema/compare/v3.5.1...v3.5.2
[v3.5.1]: https://github.com/gchq/event-logging-schema/compare/v3.5.0...v3.5.1
[v3.5.0]: https://github.com/gchq/event-logging-schema/compare/v3.4.2...v3.5.0
[v3.4.2]: https://github.com/gchq/event-logging-schema/compare/v3.4.1...v3.4.2
[v3.4.1]: https://github.com/gchq/event-logging-schema/compare/v3.4.0...v3.4.1
[v3.4.0]: https://github.com/gchq/event-logging-schema/compare/v3.4-beta.1...v3.4.0
[v3.4-beta.1]: https://github.com/gchq/event-logging-schema/compare/v3.3.0...v3.4-beta.1
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
