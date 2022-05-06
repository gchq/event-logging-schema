---
title: "Approval"
linkTitle: "Approval"
#weight:
date: 2022-05-06
tags:
  - schema-action
schema_actions:
  - approval
objectTypes:
  - banner
  - document
description: >
  Describes events relating to the approval (or rejection) of things, or the requests for approval.
---

This schema action describes events that fit the following use cases:

* **Acceptance** - A user clicking accepting an acceptable use terms banner
* **Approval Request** - User A requesting that a more senior user approves something (e.g. approving a document or a timesheet)
* **Approval** - User B approving (or rejecting) the approval requested by user A for some thing or action (e.g. approving a document or timesheet)

## Acceptance

An example of a user accepting a confirmation popup about the use of cookies.

``` xml
<EventDetail>
  <TypeId>AcceptCookies</TypeId>
  <Description>User has accepted use of cookies</Description>
  <Approval>
    <Action>Accept</Action>
    <Subject>
      <Banner>
        <Type>AccepiableUseBanner</Type>
        <Description>Cookies enhance your online experience. Simply click 'Accept and continue to site' to agree to the use of all cookies, or change them in your settings. See our Cookie policy at http://some-domain/policies/cookies.html for more information.</Description>
      </Banner>
    </Subject>
    <User>
      <Id>CN=Some Person (sperson), OU=people, O=Some Org, C=GB</Id>
    </User>
  </Approval>
</EventDetail>
``` 

## Approval Request

An example of a user requesting approval from a more senior user (or another user with approval rights):

``` xml
<EventDetail>
  <TypeId>RequestTimesheetApproval</TypeId>
  <Description>User has request the approval of their timesheet</Description>
  <Approval>
    <Action>Request Approval</Action>
    <Subject>
      <Document>
        <Title>jbloggs_20190125_timesheet</Title>
      </Document>
    </Subject>
    <Approvers>
      <Approver>
        <!-- the user with approval rights -->
        <Id>CN=John Smith (jsmith), OU=people, O=Some Org, C=GB</Id>
      <Approver>
    </Approvers>
  </Approval>
</EventDetail>
``` 

## Approval

An example of a user approving a timesheet for another user:

``` xml
<EventDetail>
  <TypeId>ApproveTimesheet</TypeId>
  <Description>User is approving the timesheet for jbloggs</Description>
  <Approval>
    <Action>Approve</Action>
    <Subject>
      <Document>
        <Title>jbloggs_20190125_timesheet</Title>
      </Document>
    </Subject>
    <Requestors>
      <Requestor>
        <!-- the user with approval rights -->
        <Id>CN=Joe Bloggs (jbloggs), OU=people, O=Some Org, C=GB</Id>
      <Requestor>
    </Requestors>
  </Approval>
</EventDetail>
``` 

## Rejection

An example of a user rejecting a timesheet for another user:

``` xml
<EventDetail>
  <TypeId>RejectTimesheet</TypeId>
  <Description>User is approving the timesheet for jbloggs</Description>
  <Approval>
    <Action>Reject</Action>
    <Subject>
      <Document>
        <Title>jbloggs_20190125_timesheet</Title>
      </Document>
    </Subject>
    <Requestors>
      <Requestor>
        <!-- the user with approval rights -->
        <Id>CN=Joe Bloggs (jbloggs), OU=people, O=Some Org, C=GB</Id>
      <Requestor>
    </Requestors>
    <Reason>Claimed too many hours</Reason>
  </Approval>
</EventDetail>
``` 
