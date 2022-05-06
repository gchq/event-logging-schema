---
title: "Import / Export"
linkTitle: "Import / Export"
#weight:
date: 2022-05-06
tags: 
  - schema-action
schema_actions:
  - import
  - export
object_types:
  - file
description: >
  The action of importing or exporting something.
---

The `<Import>` and `<Export>` elements are used to describe import and export operations.
They both contain the same element structure and describe the source and destination for the import/export event.
This allows for the source and destination to be of different types, e.g. exporting a search result to a file.

Examples:

* Import
  * Data being imported into an application, such that the application now controls that data.
  * Data being imported onto a host (and thereby the host's network) from removable media.
* Export
  * Data being exported from an application, e.g. exporting a record as a PDF document.
  * Data being exported onto removable media.

The following example shows an export event that has failed due to a lack of necessary permissions.

``` xml
<EventDetail>
  <TypeId>ExportDocument</TypeId>
  <Export>
    <Source>
      <Document>
        <Id>23894728937</Id>
        <Title>My Big Document</Title>
        <Path>/path/in/document/management/system/MyBigDocument.odf</Path>
        <Pages>10</Pages>
      </Document>
    </Source>
    <Destination>
      <File>
        <Path>/file/system/path/MyBigDoc.pdf</Path>
      </File>
    </Destination>
    <Outcome>
      <Permitted>false</Permitted>
      <Description>User does not have write permissions to /file/system/path/</Description>
    </Outcome>
  </Export>
</EventDetail>
``` 
