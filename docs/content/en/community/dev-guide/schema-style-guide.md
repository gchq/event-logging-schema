---
title: "Schema Style Guide"
linkTitle: "Schema Style Guide"
weight: 20
date: 2022-05-11
tags: 
  - style
description: >
  A style guide for developers contributing to the design of the schema.

---

## Breaking changes

When making changes to the Schema be mindful of the impact of those changes on XML documents that were valid against the previous version of the Schema.
See [Release considerations]({{< relref "release-process#release-considerations" >}}).


## Naming conventions

All elements, attributes and types should be named in UpperCamelCase, e.g. `EventSource`, `GroupComplextType`, etc.

Any initialisms that form part of a name can be in all capitals, e.g. `MAC` in `DeviceMACAddressSimpleType`.


### Groups

Groups should be suffixed with `Group`, e.g. `InstallationGroup`.


### Simple types

Simple types should be suffixed with `SimpleType`, e.g. `LatitudeSimpleType`.


### Complex types

Complex types should be suffixed with `ComplexType`, e.g. `PrintComplexType`.


## Elements vs attributes

Elements are preferred.


## Cardinality

Mandatory elements and attributes should be used with caution.
In addition to the fact that adding a new mandatory element/attribute means a breaking change it also imposes a requirement for an event to have the data to populate that element/attribute.
The Schema has to support events coming from a wide variety of source systems which may only supply minimal information and whose log output is not capable of being changed.
For this reason the vast majority of elements are optional to not impose requirements that cannot be met by source system's data.

Similarly use of constraints should be avoided as these would impose unreasonable restrictions.


## Use of anonymous complex types

When adding child content the use of `<xs:complexType>` elements should be avoided as this causes problems for the auto generated Java code produced for the event-logging Java library.
Instead create a new named complex type which will ensure the type is mapped to a Java class for a cleaner Java {{< glossary "API" >}}.


## Order

### Top level schema items

When adding new top level elements, e.g. a root element or a complex type, they should be added in alphabetical order and following the following grouping and order of groups:

1. Root elements
1. Groups
1. Complex types
1. Simple types


### Sequences

Elements in a sequence are in no particular order.
They should however be ordered in a logical manor with the most frequently used elements first.
If the sequence includes a `Data` element of type `evt:DataComplexType` then this should be the last element and be optional as it is used for extensibility.


## Annotations

The Schema should be self-documented as far as possible with `<xs:annotation>` elements added to all elements, attributes and types to describe their purpose.
These annotations are pulled through into the Javadoc of the _event-logging_ Java library so the more information that can be provided, the better the Java {{< glossary "API" >}} will be.

## Data elements

`Data` elements of type `evt:DataComplexType` should be included in most places in the Schema to allow for extensibility of all aspects of the Schema.

