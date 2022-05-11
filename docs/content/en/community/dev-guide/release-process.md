---
title: "Release Process"
linkTitle: "Release Process"
weight: 30
date: 2022-05-04
tags: 
description: >
  How to release a new version of the schema.

---

## Release considerations

Before releasing a new version of the schema you need to consider the impact of the changes in the new release.
A change to the schema can make documents that were valid against the current version no longer valid when validated with the new version.
This would be classed as a breaking change.

A non-exhaustive list of examples of changes that are considered breaking with respect to XML documents being validated against the schema:

* New mandatory `xs:element` or `xs:attribute`
* Removal of an existing `xs:element` or `xs:attribute`
* Removal of an existing `xs:choice` item
* Changing an existing `xs:element` to be mandatory
* Changing the position of an element within an `xs:sequence`
* Changing the position of an attribute within an `xs:element`
* Renaming an `xs:element` or `xs:attribute`
* Change to an element/attribute's enumeration/pattern that would invalidate formally valid values

An indirect breaking change also occurs when the schema is changed in a way that results in a breaking change to the {{< external-link "event-logging" "https://github.com/gchq/event-logging" >}} Java library.
The event-logging Java library consists of Java code that is auto-generated from the schema.
If for example a complex type in the schema is renamed then this would result in a Java class being renamed and this would be a breaking change for any client systems that use the library.

A non-exhaustive list of examples of changes that are considered breaking with respect to the event-logging Java library are:

* All changes listed above that are breaking for an XML document
* Renaming a complex type
* Renaming a simple type


## Version number

The changes made to the schema will dictate the version number.
The event-logging schema follows {{< external-link "Semantic Versioning" "https://semver.org" >}}, i.e. `MAJOR.MINOR.PATCH`.
A _MAJOR_ change is one that is breaking in terms of XML documents that validate with the schema, e.g. the removal of an element.
A _MINOR_ change is a non-breaking structural change, e.g. the addition of an optional element.
A _PATCH_ change is a very minor non-structural change, e.g. adding/updating an element's annotation.

A change that would break the event-logging Java library but not XML documents, e.g. the rename of a complex type, does not require a change to the _MAJOR_ version part.
In this instance the event-logging library would be released with its version indicating a _MAJOR_ change.

The version number should be of one of these forms
* `v<MAJOR>.<MINOR>.<PATCH>` - e.g. `v4.1.2`
* `v<MAJOR>.<MINOR>-beta.NN` - e.g. `v4.2-beta.1`

Beta versions can be used when you need to release a version to trial new changes.
With a beta version it is accepted that each iteration of the MAJOR.MINOR beta may be breaking with respect to previous iterations of that beta, e.g. one beta iteration adds a new element then a subsequent iteration removes it.


### Namespace version

Strictly speaking the namespace version in the schema (e.g. `event-logging:3`) should be changed for each _MAJOR_ version change, however changing the namespace currently causes significant pain within Stroom so for now the namespace version will stay at `3` and be out of step with the _MAJOR_ version.


## Documentation

Any changes to the schema should be fully documented.
This includes documentation in the schema in the form of element annotations and changes/additions to the documentation site in `./docs`.
The whole documentation site should be applicable to one minor version of the schema, so any additions/changes/removals to the schema should be reflected in the documentation.
The annotations in the schema are used to generate Javadoc in the event-logging Java library.


## Release branches

To allow us to support multiple versions of the schema there will be a git branch for each minor release, e.g. `4.0`, `4.1`, `5.0`, etc.
The latest releases will always be made from `master`.
The release branch can be created retrospectively, e.g. if you need to make a _PATCH_ change to `v4.1.3` but `v4.2.0` has been released on master then create a `4.1` branch off the `v4.1.3` tag and release from there.


## Release Steps

1. Decide what the new version number will be based on the changes made, see [Version number]({{< relref "#version-number" >}}) above.
1. Ensure all required changes have been made to `event-logging.xsd` with any new elements suitably annotated.
    1. Change the `version` attribute to the intended version number, e.g. `4.1.2`
    1. Change the `id` attribute to the intended version number, e.g. `event-logging-v4.1.2`
    1. Change the single enumeration in `VersionSimplType` to match the new version number.
1. Ensure all required changes have been made to the documentation.
1. Run the build for a set version number to ensure the CI build will pass, e.g.
   {{< command-line >}}
   ./gradlew clean build -Pversion=v4.1.2
   {{</ command-line >}}
   When the build runs it will do the following:
   * Validate the version numbers in the master schema file against the gradle version argument.
   * Run the transformer pipelines to generate the various schema varients (including validating them).
   * Compare the generated schemas with the ones from the latest release so you can check what has changed.
1. Ensure the change entries have been created using `./log_change.sh`.
1. Commit and push all the changes.
1. Tag the release using `./tag_release.sh` which will initiate the CI release process.
1. Add the new schema to stroom-content
    1. Copy the new schema file into `stroom-content/source/event-logging-xml-schema/stroomContent/XML Schemas/event-logging/` naming it something like `event-logging v4.1.2.XMLSchema.data.xsd`.
    1. Copy the latest `.XMLSchema.xml` files into one named for the new version, e.g. `event-logging v4.1.2.XMLSchema.xml`
    1. Edit this new `.XMLSchema.xml` file:
        1. Update the `<name>` tag to reflect the new version number
        1. If the major version number has changed update the `<namespaceURI>` tag with the new major version number
        1. Update the `<systemId>` tag to reflect the new version number
        1. Replace the content of the `<uuid>` tag with a newly generated UUID.  You can use the linux binary `uuidgen` to generate a new UUID.
    1. Update the `CHANGELOG.md` file in `stroom-content/source/event-logging-xml-schema/`, probably copying the content from the CHANGELOG in the event-logging-schema git repo.
    1. Run the build to build the new pack
    1. Commit and push the changes
    1. In GitHUb create a new release for the updated pack.
1. Update the version numbers in `event-logging.xsd`
    1. Change the `version` attribute to the next intended version number with a SNAPSHOT suffix, e.g. `4.2.0-SNAPSHOT`
    1. Change the `id` attribute to the next intended version number with a SNAPSHOT suffix, e.g. `event-logging-v4.2.0-SNAPSHOT`
    1. Commit and push the change
