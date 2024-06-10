---
title: "Search Results"
linkTitle: "Search Results"
weight: 150
date: 2022-05-06
tags: 
  - object-type
object_types:
  - search-results
description: >
  A set of search results that are independent of the query that generated them.

---

This element is used to describe a set of search results that have a life beyond the execution of the query that generated them.
For example where the execution of the query forms one event and the viewing of the results happens some time later and forms another event. 

This element can be used to describe a single result within a result set, a page of results from a larger result set or a full result set.

The separate event for the execution of the search/query that produced the results should be modelled using the [`<Search>`]({{< relref "/docs/schema-actions/search" >}}) schema action.

See [Complete Examples]({{< relref "/docs/complete-examples/xml/object-types/search-results.xml.md" >}}) for example events.
