---
title: "Chat"
linkTitle: "Chat"
#weight:
date: 2022-05-05
tags: 
  - object-type
object_types:
  - chat
description: >
  A single chat message in an instant message application.

---

This is used to describe a single message in an instant messaging application.
The `<SessionId>` element can be populated to allow multiple events for the same chat session to be associated at a later date.
This is used for direct user to user chat as opposed to [`<GroupChat>`]({{< relref "group-chat" >}}) which is used for chatting to a group of users via a chat room.

A chat message is typically modelled within a [`<Create>`]({{< relref "/docs/schema-actions/create-view-delete" >}}) schema action.
