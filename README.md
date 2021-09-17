# Event Logging XML Schema

[![Build Status](https://travis-ci.org/gchq/event-logging-schema.svg?branch=master)](https://travis-ci.org/gchq/event-logging-schema)

_Event Logging_ is an XML Schema for describing the auditable events generated by computer systems, hardware devices and access control systems. 
It is intended to act as a common standard for describing auditable events.
Either systems can generate events that conform to this schema or bespoke logging formats (such as Apache web server or Linux auditd logs) can be translated into it, using a tool such as [Stroom](https://github.com/gchq/stroom).

The aim of the schema is to normalise logging data into a common form meaning that analysts or analytical tools do not need to understand multiple log formats all with their own unique features. 
Also normalised log events from a bespoke system can be understood years after the event was created and when the development team responsible for the bespoke system have all disappeared.

This schema has been in use since 2009 and has evolved over that time to cater for the wide variety of auditable events seen on production systems. 
It is used as the common language for event logging for hundreds of applications, operating systems and hardware devices, ranging from commercial off-the-shelf products to bespoke in-house applications. 

For bespoke in-house systems the schema serves as common approach to creating auditable events, and reduces the need for the developers to re-invent a new log event structure and taxonomy. 
Java applications can make use of our [java API for the event-logging schema](https://github.com/gchq/event-logging), which is also available as a Maven/Gradle dependency on [Maven Central](https://mvnrepository.com/artifact/uk.gov.gchq.eventlogging/event-logging).


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

The documentation includes a number of complete example XML documents.
These xml documents are validated as part of the build process to ensure the examples are valid against the latest schema and to highlight examples that are no longer valid when the schema is changed.
This validation is done against a variant of the schema that is specific for validating the example XML.
This schema should be identical to the generated full schema except that it has a static version number so the example XML documents don't have to change with each release.


### Documentation in the JAXB java API

The _event-logging_ repository has a process to generate a java API from the event-logging-schema XMLSchema.
During this process, any schema annotations, i.e. `<xs:annotation><xs:documentation></xs:annotation></xs:documentation>`, will be added to the Javadoc in the java API.
This allows us to make the schema as self describing as possible without having to repeat the work in the generated java code.
While the production of additional Javadoc has no direct bearing on the design of this schema, it is worth bearing in mind when adding annotations to ensure that they will appear in a sensible place in the java code.

Annotations on complex types (named or annonymous) will be added to the class level Javadoc.

```xml
<xs:complexType name="MyComplexType">
  <xs:annotation>
    <xs:documentation>This will be added to the class level javadoc</xs:documentation>
  </xs:annotation>
  <xs:sequence>
```

Javadoc will be added to property getters/setters and Builder add/with methods using the annotation from the corresponding element or from its complex type if there is no annotation on the element.

```xml
<xs:element name="MyElement" minOccurs="0" maxOccurs="1">
  <xs:annotation>
    <xs:documentation>This will be added to the getters/setters and Builder methods</xs:documentation>
  </xs:annotation>
  <xs:complexType>
    <xs:annotation>
      <xs:documentation>This will be added to the class level javadoc. This will also be added to the getters/setters and Builder methods if the above annotation doesn't exist.</xs:documentation>
    </xs:annotation>
    <xs:sequence minOccurs="1" maxOccurs="1">
```

In the above example there is documentation at both the element and annonymous complex type leve that essentiall describes the same thing as the type has one one use. If only the complex type annotation is used then some schema editors, e.g. OxygenXML, will not display the annotation. Thus for clarity both positons should be used.


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

## Impact on event-logging

[event-logging](https://github.com/gchq/event-logging) is the java library for creating events conforming to this schema.
It is **STRONGLY** advised that when making changes to this schema that the build for _event-logging_ is run to establish what impact the schema changes on the generated Java code.
Changes to the schema that would have no impact on XML documents, e.g. a complext type name change would result in a breaking change to the _event-logging_ library.
See the _README.md_ for _event-logging_ for more details on how to build it.


## Process for releasing a new version of the schema

When you are ready to release a new version of the schema ensure you have done the following: 

1. Ensure the CHANGELOG.md file has details of all changes since the last released version under a heading for the version that is about to be released. 
    Also add the appropriate github compare url link to the bottom of the file. e.g. 

    ```
    ## [Unreleased]


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

1. Run the build to ensure the versions are all valid and the example XML is all valid against the schema.

    `./gradlew clean build -Pversion=vX.Y.Z`

1. Commit any changes.

1. Then run the following script to tag Git with an appropriate commit message and thus trigger a Travis release build which will, if successful, release the schema to [GitHub releases](https://github.com/gchq/event-logging-schema/releases).

    `./tag_release.sh`

1. Once the release has been tagged and built Update the versions in the schema to be something like `X.Y.0-SNAPSHOT` where X.Y.0 is the next minor version number. 
    This makes it clear that the version in source control is not a release version.
