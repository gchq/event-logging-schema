---
title: "Meta Element"
linkTitle: "Meta Element"
#weight:
date: 2022-05-04
tags: 
  - meta
description: >
  An element to allow extensibility of events.
---

The `<Meta>` element appears in a number of places in the schema.
The purpose of the `<Meta>` element is to allow for extensibility of events with meta data whose structure is defined outside of this schema.
Such content could be site specific and thus not warrant inclusion in the _event-logging_ schema.
It may also be used for decorating events post-receipt.

The `<Meta>` element is of type `evt:AnyContentComplexType` which is basically a wrapper around an `xs:any` element so it can support different data structures such as JSON, YAML, XML (conforming to another schema), CSV, etc.

A `<Meta>` element can look like this:

``` xml
<Meta ContentType="XML:MyMeta" Version="1.2">
  <MyMeta xmlns="http://myorg.mydomain.mymeta">
    <ElementA></ElementA>
    <ElementB></ElementB>
  </MyMeta>
</Meta>
```

The `ContentType` attribute is used to provide a computer readable name or key for the type of data held within `<Meta>`.
This will allow to the system reading the event to be able to appropriately parse the content, if it is able to.
The value of the attribute would be specific to the use of that type of meta data.

The `Version` attribute is used to optional specify the version of the structure used inside the `<Meta>` element.
Some data formats are self describing when it comes to their version so this attribute may not be required.

Multiple `<Meta>` elements are supported allowing for various different types of meta data to be attached to the event.
