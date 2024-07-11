---
title: "Cloud Service"
linkTitle: "Cloud Service"
weight: 60
date: 2024-06-10
tags: 
  - object-type
object_types:
  - cloud-service
description: >
  Describes a service on a cloud provider, e.g. an AWS Lambda or a Google BigQuery database.
  This normally descirbes a Software As A Serivce offering.
  Cloud infrastructure such as AWS EC2s should be modelled with the VirtualDevice object type instead.
---

This object type can be used for modelling events where a user is performing actions on a cloud service, e.g. creating an AWS Lambda, updating the configuration of a Google BigQuery database.

`CloudService` is distinct from  [`VirtualDevice`]({{< relref "./virtual-device" >}}) in that it tends to represent software as a service rather than a virtualised device.
What is a service and what is a virtual device is somewhat subjective though, so some care and consistency of approach is needed.
