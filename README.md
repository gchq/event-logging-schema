# Event Logging XML Schema

[![Build Status](https://travis-ci.org/gchq/event-logging-schema.svg?branch=master)](https://travis-ci.org/gchq/event-logging-schema)

_Event Logging_ is an XML Schema for describing the auditable events generated by computer systems, hardware devices and access control systems. 
It is intended to act as a common standard that bespoke logging formats (such as Apache web server or Linux auditd logs) can be translated into, using a tool such as [Stroom](https://github.com/gchq/stroom).

The aim of the schema is to normalise logging data into a common form meaning that analysts do not need to understand multiple log formats all with their own unique features. 
Also normalised log events from a bespoke system can be understood years after the event was created and when the development team responsible for the bespoke system have all disappeared.

This schema has been in use since 2009 and has evolved over that time to cater for the wide variety of auditable events seen on production systems. 
It is used as the common language for event logging for hundreds of applications, operating systems and hardware devices, ranging from commercial off-the-shelf products to bespoke in-house applications. 

For bespoke in-house systems the schema serves as common approach to creating auditable events, and reduces the need for the developers to re-invent a new log event structure and taxonomy. 
Java applications can make use of our [java API for the event-logging schema](https://github.com/gchq/event-logging), which is also available as a Maven/Gradle dependency on [Bintray](https://bintray.com/stroom/event-logging/event-logging).

## Schema Versions

The formal releases of the schema can be found [here](https://github.com/gchq/event-logging-schema/releases). 
Each formal release currently includes two variants of the schema, e.g. 

* `event-logging-v3.xsd` - Complete schema.

* `event-logging-v3-client.xsd` - Schema for client system use.

The _client_ variant is effectively a sub-set of the full schema and is intended for use by client systems for recording auditable events. 
Certain elements are removed in the _client_ variant, e.g. the `<EventId>` element and the bulk of the elements in the `UserDetailsComplexType`. 
This is because these elements are expected to be added in post-receipt rather than being provided by the client system. 
The client version also adds `<Event>` as a root element to allow the sending of individual event objects.

## Documentation

See [here](https://gchq.github.io/event-logging-schema/) for the full documentation on the schema.

## Building the schema

The master version of the schema is located in the root of this repository (`event-logging.xsd`). 
This is the version that changes are made to. 
A build process exists to take this schema and apply a number of XSLT transformations to it to produce a releasable version, along with any other variants of it. 
Currently the build process takes the master version and applies a set of non-breaking transformations to it to maintain a consistent order of simple/complex type definitions and attributes. 
This cleaned version is the version that will be released. 
The build process also creates the _client_ variant of the schema.

The transformations are configured in the file `event-logging-transformations/pipelines/configuration.yml`. 
Each item in the _pipelines_ section will produce an XMLSchema file intended for release. 
The master version of the schema is not intended for release.

To build the variants of the schema run the following command from the root of the repository:

`./gradlew clean build`

To test a build with a release version applied:

`./gradlew clean build -Pversion=vX.Y.Z`

## Process for releasing a new version of the schema

When you are ready to release a new version of the schema ensure you have done the following: 

1. Ensure the CHANGELOG.md file has details of all changes since the last released version under a heading for the version that is about to be released. 
    Also add the appropriate github compare url link to the bottom of the file. e.g. 

    ```
    ## [Unreleased]

    ### Added

    ### Changed


    ## [v4.0.0] - 2020-12-25

    ### Added

    * Add new ABC element

    ### Changed

    * Removed XYZ element

    ```

    and

    ```
    [v4.0.0]: https://github.com/gchq/event-logging-schema/compare/v3.2.3...v4.0.0
    ```

1. Update the enumeration in the _VersionSimpleType_ in the master schema (`event-logging.xsd`) with the version number that is about to be released.

1. Update the following attributes in the schema (if applicable):

    * `schema/@targetNamespace`, e.g. `event-logging:3` -> `event-logging:4`

    * `schema/@version`, e.g. `3.2.3` -> `4.0.0`

    * `schema/@id`, e.g. `event-logging-v3.2.3` -> `event-logging-v4.0.0`

1. Update the version information in all the example xml in `docs/completeExamples/**/*.{xml.md,xml}`.

    Each example xml document will have contain information about the version of the schema that it has been written against.
    E.g.:

    ```xml
    <Events
      xmlns="event-logging:3"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="event-logging:3 file://event-logging-v3.4.0.xsd"
      Version="3.4.0">
    ```

    The `xmlns`, `xsi:schemaLocation`, and `Version` attributes need to be changed to reflect the new schema version.
    The change can be achieved with some global search replace, e.g.:

    ```bash
    find docs/completeExamples/ \( -name "*.xml.md" -o -name "*.xml" \) \
      | xargs sed -i'' 's#file://event-logging-v3.4.0.xsd#file://event-logging-v3.4.1.xsd#'

    find docs/completeExamples/ \( -name "*.xml.md" -o -name "*.xml" \) \
      | xargs sed -i'' 's#Version="3.4.0"#Version="3.4.1"#'
    ```
1. Run the build to ensure the versions are all valid and the example XML is all valid against the schema.

    `./gradlew clean build`

1. Commit any changes.

1. Then run the following script to tag Git with an appropriate commit message and thus trigger a Travis release build which will, if successful, release the schema to [GitHub releases](https://github.com/gchq/event-logging-schema/releases).

    `./tag_release.sh`

1. Once the release has been tagged and built Update the versions in the schema to be something like `X.Y.0-SNAPSHOT` where X.Y.0 is the next minor version number. 
    This makes it clear that the version in source control is not a release version.
