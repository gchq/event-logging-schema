---
title: About the event-logging XML Schema
linkTitle: About
menu:
  main:
    weight: 10
description: >
  A common standard for auditable events.
---

## What is it?

An {{< external-link "XML Schema" "https://www.w3.org/XML/Schema" >}} for describing and recording events for the purposes of auditing the actions of users and systems.

Each action performed by a user, system or application component during the course of operation produces one or more events.
Some of these events need to be recorded for the purposes of audit.
This XML Schema provides a standard language for doing that.


## History

The _event-logging_ XML Schema was born out of a need to standardise the format of events being logged by hundreds of different systems.
Events were being logged in a large variety of different formats (csv, fixed-width, structured, etc.) and with a variety of different data fields.
This made it very difficult to process, analyse or search the events in a common way.

The _event-logging_ XML Schema was created to provide a common language that events could either be written in or be normalised into.
The result is a set of events from many different systems that can all be processed in a common way.


## What it captures

The Schema aims to capture four main things:


### When did it happen?

The time that the event happened in a single format ({{< glossary "ISO-8601" >}}) and time zone ({{< glossary "UTC" >}}).
Recording all events in UTC ensures a consistent view of the data.


### Who or what caused it?

Which user or device triggered the event.
This is typically a unique identifier for the user or an {{< glossary "FQDN" >}} or an IP address for a device.


### Where did it happen?

Details of where the user or device was physically located when the event occurred.
This may also include the time-zone of the user/device.


### What happened? 

Details of the event that occurred.
This includes recording the action (i.e. verb) of the event, e.g. creating, deleting, viewing, etc. with action specific detail.


## Event decoration

Events will typically provide minimal information about the user/device that triggered the event, e.g. just a user ID.
The Schema allows for additional data to be added into the event record by performing lookups in databases using key identifiers in the event.
Examples of decoration include populating the event record with Human Resources data such as full name and job role, or populating the physical location of a device by looking up its IP address in an asset management database.
This allows for much richer events to be stored.


