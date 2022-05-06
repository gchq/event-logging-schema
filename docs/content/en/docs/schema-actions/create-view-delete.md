---
title: "Create / View / Delete"
linkTitle: "Create / View / Delete"
#weight:
date: 2022-05-06
tags: 
  - schema-action
schema_actions:
  - create
  - view
  - delete
object_types:
  - document
  - user
  - group
description: >
  The action of creating, viewing or deleting an entity or object.
---

Whenever data is created, viewed and deleted, an event can be described for audit purposes.

Although 'Read' is usually used to describe the act of viewing information it is also associated with invisible system operations where processes are reading data. For the purposes of logging auditable events it was thought that 'View' was a more appropriate term.

The `<Create>`, `<View>` and `<Delete>` events all have the same content model that describes the data in question. An example event generated when viewing a document is shown in the following example.

```xml
<EventDetail>
  <TypeId>ViewDocument</TypeId>
  <View>
    <Document>
      <Permissions>
        <Permission>
          <User>
            <Id>Some Author</Id>
          </User>
          <Allow>Author</Allow>
        </Permission>
      </Permissions>
      <Title>Some document.</Title>
      <Path>/Some path/some document.txt</Path>
    </Document>
  </View>
</EventDetail>
``` 

As with all data related events, other types of data can be described within this structure, e.g. files, folders, configuration data and web pages. Other objects can be described using the `<Object>` element with an appropriate `<Type>` to describe what they are.

## Role/Permission Management
Roles, permissions, communities and access groups are all treated as types of group that a user or other system actor can belong to. Groups that are added and removed from a system are described in CRUD operations the same way as any other entity. The following example demonstrates removing an access control group.

```xml
<EventDetail>
  <TypeId>RemoveGroup</TypeId>
  <Description>Removing an LDAP group.</Description>
  <Delete>
    <Group>
      <Type>LDAP Group</Type>
      <Id>DEF</Id>
    </Group>
  </Delete>
</EventDetail>
``` 

## User Management
Users that are added and removed from a system are described in CRUD operations the same way as any other entity. The following example demonstrates adding a user.

```xml
<EventDetail>
  <TypeId>AddUser</TypeId>
  <Description>Adding a User</Description>
  <Create>
    <User>
      <Id>CN=New User (nuser), OU=people, O=Some Org, C=GB</Id>
    </User>
  </Create>
</EventDetail>
``` 

