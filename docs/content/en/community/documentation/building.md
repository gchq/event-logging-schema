---
title: "Building the Documentation"
linkTitle: "Building the Documentation"
weight: 10
date: 2022-01-12
tags: 
  - TODO
description: >
  How to develop and build the documentation.
---


## Prerequisites

In order to build and contribute to the documentation you will need the following installed:

* bash
* {{< external-link "Docker" "https://docs.docker.com/get-docker/" >}} 

Docker is required as all the build steps are performed in docker containers to ensure a consistent and known build environment.
It also ensures that the local build environment matches that used in GitHub actions.

It is possible to build the docs without docker but you would need to install all the other dependencies that are provided in the docker images, hugo, npm, etc.


## Cloning the event-logging-schema git repository

The git repository for this site is {{< external-link "event-logging-schema" "https://github.com/gchq/event-logging-schema" >}}.
_event-logging-schema_ uses the Docsy theme (`themes/docsy/`) which is pulled in via Go modules.
To se

{{< command-line >}}
# Clone the repo
git clone https://github.com/gchq/event-logging-schema.git
(out)Cloning into 'event-logging-schema'...
(out)remote: Enumerating objects: 66006, done.
(out)remote: Counting objects: 100% (7916/7916), done.
(out)remote: Compressing objects: 100% (1955/1955), done.
(out)remote: Total 66006 (delta 3984), reused 7417 (delta 3603), pack-reused 58090
(out)Receiving objects: 100% (66006/66006), 286.61 MiB | 7.31 MiB/s, done.
(out)Resolving deltas: 100% (34981/34981), done.
cd event-logging-schema
{{</ command-line >}}


## Running a local server

The documentation can be built and served locally while developing it.
To build and serve the site run

{{< command-line >}}
./container_build/runInHugoDocker.sh server
{{</ command-line >}}

This uses Hugo to build the site in memory and then serve it from a local web server.
When any source files are changed or added Hugo will detect this and rebuild the site as required, including automatically refreshing the browser page to update the rendered view.

Once the server is running the site is available at [localhost:1313/](http://localhost:1313/).

{{% warning %}}
Sometimes changes made to the site source will not be re-loaded correctly so it may be necessary to stop and re-start the server.
{{% /warning %}}


## Building the site locally

To perform a full build of the static site run:

{{< command-line >}}
./container_build/runInHugoDocker.sh build
{{</ command-line >}}

This will generate all the static content and place it in `public/`.


## Generating the PDF

Every page has a _Print entire section_ link that will display a printable view of that section and its children.
In addition to this the GitHub Actions we generate a PDF of the `docs` section and all its children, i.e. all of the documentation (but not News/Releases or Community) in one PDF.
This makes the documentation available for offline use.

To test the PDF generation do:

{{< command-line >}}
./container_build/runInPupeteerDocker.sh PDF
{{</ command-line >}}


## Updating the Docsy theme

The {{< external-link "Docsy" "https://github.com/google/docsy" >}} theme is pulled in as a Go module.
To update the version of Docsy used see {{< external-link "Update the Docsy Hugo Module" "https://www.docsy.dev/docs/updating/updating-hugo-module/" >}}.

When these instrustions say to run the `hugo` command you need to run them using the builder container.
e.g.

{{< command-line >}}
./container_build/runInHugoDocker.sh "hugo mod get -u github.com/google/docsy@v0.2.0"
{{</ command-line >}}

