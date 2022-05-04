---
title: "Unstructured Data"
linkTitle: "Unstructured Data"
#weight:
date: 2022-05-04
tags: 
description: >
  When producing schema compliant data, each and every data field from an event should populate the most appropriate element in the schema.
---

In some cases there may not be an adequate data structure for capturing the content of a field.
In these cases data can be captured using the `<Data>` element content model, see the following example.

``` xml
<EventDetail>
  <Data Name="infoKey" Value="Info"/>
  <Data Name="otherInfoKey" Value="OtherInfo"/>
</EventDetail>
``` 

`<Data>` elements can be nested to indicate hierarchy if necessary and can be included within many of the predefined data structures, for example.

``` xml
<EventDetail>
  <Data Name="user" Value="Flack">
    <Data Name="role" Value="manager"/>
    <Data Name="staff">
      <Data Name="user" Value="Cuthbert">
        <Data Name="role" Value="underling"/>
        <Data Name="staff"/>
      </Data>
      <Data Name="user" Value="Dibble">
        <Data Name="role" Value="underling"/>
        <Data Name="staff"/>
      </Data>
      <Data Name="user" Value="Grub">
        <Data Name="role" Value="underling"/>
        <Data Name="staff"/>
      </Data>
    </Data>
  </Data>
</EventDetail>
``` 

Extensive use of the unstructured Data elements can lead to difficulties in processing events unless the name and value attributes have been used in a consistent way.
