---
title: "Object Types"
linkTitle: "Object Types"
#weight:
date: 2022-05-05
tags: 
description: >
  Object types used to describe the entity or entities that are the subject of an event.

---

When describing what happened in an event there is usually an entity or entities involved in the event, e.g. a user deleting a document, sending an email, creating a virtual session, etc.
The majority of the schema actions support acting on one or more instances of the MultiObjectComplexType, this structure describes a choice of the following entity types:

Where the entity involved in the event cannot be described by one of the predefined categories in this structure then the `<Object>` structure can be used as a last resort.


## Common Structural Elements

The following elements are common to all of the above object types;

* **Type** - The type of the object in question and specific to the object type from the list above, e.g. a `<Resource>` object may have a type such as 'image' or 'script'
* **Id** - An identifier for the object, e.g a document ID in a document management system
* **Name** - The name of the object, e.g. a filename
* **Description** - Human readable description of what the object is
* **Classification** - Any classification or restrictions placed on the object, e.g. for commercially sensitive reports or user health records (see [Basic Structure](../basicStructure/README.md))
* **State** - Any state information about the object, e.g. 'Archived' 
* **Groups** - Any groups associated with the object, e.g. group membership of a user account
* **Permissions** - Any permissions associated with the object, e.g. write access being granted to a list of named users or groups
* **Data** - An extensible structure for additional unstructured data as described in [Unstructured Data](../unstructuredData.md)
* **Meta** - An extensible structure for structured meta data as described in [Meta Element](../meta.md)
