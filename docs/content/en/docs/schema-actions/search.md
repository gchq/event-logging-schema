---
title: "Search"
linkTitle: "Search"
#weight:
date: 2022-05-06
tags: 
  - schema-action
schema_actions:
  - search
object_types:
  - search-results
description: >
  The action of executing a query, filter or search.
---

## Performing a Search

Whenever a user performs a search the details of the search may be recorded for audit purposes.
The types of searches that could be performed by a user include searching the file system, remote file shares, emails, public folders and searches for data within applications.

There are several formats of search query supported by the schema.
They include:

* `<Advanced>` – The structure enables complex queries to be built that include combinatorial term logic.
* `<Simple>` – Records the terms that the search is looking for and the ones to explicitly exclude.
* `<Raw>` – Used for record the executed query string entered by a user or SQL applied to a database.

The ability to create a complex query may be limited by the application or device creating the event log.
Where this is the case it may be necessary to use the simple or raw query format.
Even if `<Advanced>` or `<Simple>` are used, the inclusion of the raw query string is desirable as it describes the search string that was actually executed.

An example search event with a raw query is shown:

``` xml
<EventDetail>
  <TypeId>BasicSearch</TypeId>
  <Search>
    <Query>
      <Id>query-236</Id>
      <Raw>select * from table_x where user='jbloggs'</Raw>
    </Query>
  </Search>
</EventDetail>
``` 

The following example shows a search event with an advanced query where the query is (location != site1 AND (user==jBloggs OR user==jDoe)):

``` xml
<EventDetail>
  <TypeId>BasicSearch</TypeId>
  <Search>
    <Query>
      <Id>364587</Id>
      <Advanced>
        <And>
          <Term>
            <Name>location</Name>
            <Condition>NotEquals</Condition>
            <Value>site1</Value>
          </Term>
          <Or>
            <Term>
              <Name>user</Name>
              <Condition>Equals</Condition>
              <Value>jBloggs</Value>
            </Term>
            <Term>
              <Name>user</Name>
              <Condition>Equals</Condition>
              <Value>jDoe</Value>
            </Term>
          </Or>
        </And>
      </Advanced>
      <Raw>select * from table_x where user in ('jbloggs', 'jbloggs') and location <> 'site1'</Raw>
    </Query>
  </Search>
</EventDetail>
``` 

### Synchronous vs Asynchronous Searches

Some search events may be synchronous, where the results are know as part of the search action.
For synchronous searches, details of the results can be included within the `<Search>` element.
Some searches however are asynchronous, where the results are not know at the time the search is executed.
For these asynchronous searches, the execution of the search should use the `<Search>` schema action and the retreival of the results would be modelled as separate `<View>/<SearchResults>` event.
To link the `<Search>` event with the `<some schema action>/<SearchResults>` event the `<Query>/<Id>` or `<Query>/<Name>`element can be used, as shown in the following example.


``` xml
<!-- Search event -->
<EventDetail>
  <TypeId>AsynchronousUserQuery</TypeId>
  <Search>
    <Query>
      <!-- ID to link the query execution with the subsequent viewing of the results -->
      <Id>3648595</Id>
      <Raw>select cv_blob from candidates where user='jbloggs'</Raw>
    </Query>
  </Search>
</EventDetail>

<!-- View results event -->
<EventDetail>
  <TypeId>ViewUserQueryResults</TypeId>
  <View>
    <SearchResults>
      <Query>
      <!-- ID to link the query execution with the subsequent viewing of the results -->
      <Id>3648595</Id>
      <!-- If the query detail was recorded in the initial Search event then it could be omitted here -->
      </Query>
      <TotalResults>1</TotalResults>
      <Results>
        <Document>
          <Title>J Bloggs CV</Title>
          <Path>/tmp/jbloggs_cv.pdf</Path>
        </Document>
      </Results>
    <SearchResults>
  </View>
</EventDetail>
```

## Search Results

The schema allows for results that are returned from a search to be recorded.

The total number of results can be recorded along with a list of the displayed results.
Where results are paged (e.g. in a web search) details of the currently displayed page can also be described.

The list of results can describe the type of result and provide appropriate details, see the following example.

``` xml
<Search>
  ...
  <ResultPage>
    <TotalPages>10</TotalPages>
    <PerPage>10</PerPage>
    <PageNumber>2</PageNumber>
    <From>11</From>
    <To>20</To>
  </ResultPage>
  <TotalResults>100</TotalResults>
  <Results>
    <Document>
      <Title>Some Document 1</Title>
      <Path>file://Some_Path/Some%20Document.doc</Path>
    </Document>
    <WebPage>
      <URL>http://Some_Path/Some%20HTML%20Page.htm</URL>
    </WebPage>
  </Results>
</Search>
``` 

See [Complete Examples]({{< relref "../complete-examples" >}}) for example events.
