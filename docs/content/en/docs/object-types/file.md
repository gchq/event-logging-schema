---
title: "File"
linkTitle: "File"
weight: 90
date: 2022-05-05
tags: 
  - object-type
object_types:
  - file
description: >
  Any kind of file on a file system.
---

This is used to describe any kind of file on a file system or file repository, e.g. a data file, log file, backup file, etc.
The `<Media>` element can be used to describe the type of media it is stored on.
The hash of the file can also be recorded using `<Hash>` so the actual version of the file interacted with is known.
If a hash is supplied then the name of the hashing algorithm should also be supplied in the `Type` attribute.

The object type [`<Document>`]({{< relref "document" >}}) may be more appropriate unless the file is being referred to in its generic capacity as a file.
