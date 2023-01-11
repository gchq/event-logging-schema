---
title: "Process"
linkTitle: "Process"
#weight:
date: 2022-05-06
tags: 
  - schema-action
schema_actions:
  - process
object_types:
  - file
description: >
  A process such as running a shell script or a scheduled job.
---

Processes can be recorded for audit purposes such as:
* Operating system start-up/shutdown.
* Event logging system start-up/shutdown.
* Some services start-up/shutdown/install/uninstall.
* Some applications start-up/shutdown/install/uninstall.
* Scheduled jobs or batch processes such as shell scripts

Each of these events can be described using the `<Process>` element, see the following example.

``` xml
<EventDetail>
  <TypeId>1234</TypeId>
  <Process>
    <Action>Startup</Action>
    <Type>Application</Type>
    <Command>MsWord.exe</Command>
  </Process>
</EventDetail>
``` 
The following is an example of recoding the input and output to a batch process.

``` xml
<EventDetail>
  <TypeId>SplitData</TypeId>
  <Process>
    <Action>Execute</Action>
    <Type>Application</Type>
    <Command>script.sh</Command>
    <Input>
      <File>
        <Path>/sourcePath/sourceFile.txt</Path>
      </File>
    </Input>
    <Output>
      <File>
        <Path>/destPath/destFile.txt</Path>
      </File>
    </Output>
  </Process>
</EventDetail>
``` 
