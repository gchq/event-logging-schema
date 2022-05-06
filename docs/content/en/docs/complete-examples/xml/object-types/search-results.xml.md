---
title: "Search Results"
linkTitle: "Search Results"
#weight:
date: 2022-05-06
tags: 
  - complete-example
  - object-type
schema_actions:
  - view
object_types:
  - search-results
description: >
  example of a user _viewing_ a set of _search results_.

---

user is viewing a set of search results independently of the search event that generated them.
the query/id element can be used to link the two events together.

this object type can be used when the results of the query are not know at the time the search is executed and the search event is created.

a view event is used here simply to illustrate the use of searchresults.
searchresults can be used within other schema actions.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<events
  xmlns="event-logging:3"
  xmlns:xsi="http://www.w3.org/2001/xmlschema-instance"
  xsi:schemalocation="event-logging:3 file://event-logging-v999.99.9-documentation.xsd"
  version="999.99.9">

  <!-- view/searchresults event -->

  <event>

    <eventtime>
      <timecreated>2017-01-02t03:04:05.678z</timecreated>
    </eventtime>

    <eventsource>
      <!-- the source system specific unique id for this event -->
      <eventid>1024</eventid>
      <system>
        <name>rock sample database</name>
        <environment>space</environment>
        <organisation>acmecoolresearch</organisation>
        <version>r8.1</version>
      </system>
      <generator>db-query</generator>
      <device>
        <hostname>db56.serverfarm.mydomain.org</hostname>
        <ipaddress>191.181.171.161</ipaddress>
      </device>
      <client>
        <hostname>desktop4.moonbase-a.mydomain.org</hostname>
        <ipaddress>111.101.101.111</ipaddress>
      </client>
      <user>
        <id>jc101</id>
      </user>
      <interactive>true</interactive>
    </eventsource>

    <eventdetail>
      <typeid>viewsearchresults</typeid>
      <description>user is viewing a set of stored search results</description>
      <view>
        <searchresults>
          <query>
            <!-- 
            provides a link back to the search event generated when the 
            query was executed. the link can also be acheived using eventlinks below.
            -->
            <id>query-538393</id>
          </query>
          <totalresults>2</totalresults>
          <results>
            <object>
              <type>rock</type>
              <id>78121</id>
              <name>surpisingly heavy chunk</name>
            </object>
            <object>
              <type>rock</type>
              <id>11418</id>
              <name>possible gold ore</name>
            </object>
          </results>
        </searchresults>
      </view>
    </eventdetail>

    <!-- this event is linked back to its parent (the initial search) -->
    <eventchain>
      <activity>
        <!-- links this event to the one with eventsource/eventid[text()='1023'] -->
        <id>92832938</id>
        <name>search execution</name>
      </activity>
    </eventchain>

  </event>

</events>

```
