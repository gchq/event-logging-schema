---
title: "Contributing"
linkTitle: "Contributing"
weight: 10
date: 2022-02-09
tags: 
description: >
  How to make contributions to the event-logging schema.

---

We love pull requests and we want to make it as easy as possible to contribute changes.


## Getting started

* Make sure you have a {{< external-link "GitHub account" "https://github.com/" >}}.
* Maybe create a GitHub {{< external-link "issue" "https://github.com/gchq/event-logging-schema/issues" >}}.
  Is this a comment or documentation change?
  Does an issue already exist?
  If you need an issue then describe it in as much detail as you can, e.g. step-by-step to reproduce.
* Fork the repository {{< external-link "repository" "https://github.com/gchq/event-logging-schema" >}} on GitHub.
* Clone your fork of the repository.
* Create a branch for your change, probably from the master branch.
  Please don't work on master.
  Convention is for the branch name to include the issue number like so: `git checkout -b gh-1234-fix-thing-x`.


## Making changes

* New elements should have `<xs:annotation>` elements attached to them to provide documentation about the element.
* You should also update the documentation for any changes.
* The revised schema should be valid XML and successfully validate against the XMLSchema standard.
* Be very mindful of making [breaking changes]({{< relref "release-process#release-considerations" >}}).
* New elements should in most cases be optional as not all source systems can provide all data fields.
  Enforcing mandatory data can be done outside of the schema.


## Submitting changes

* Sign the Contributor Licence Agreement.
* Push your changes to your fork.
* Submit a {{< external-link "pull request" "https://github.com/gchq/event-logging-schema/pulls" >}}.
* We'll look at it pretty soon after it's submitted, and we aim to respond within one week. 


## Getting it accepted

Here are some things you can do to make this all smoother:
* If you think it might be controversial then discuss it with us beforehand, via a GitHub issue.
* Write a {{< external-link "good commit message" "http://chrio/posts/git-commit/" >}}).
