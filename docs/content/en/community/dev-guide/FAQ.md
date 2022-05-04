---
title: "Frequently Asked Questions"
linkTitle: "FAQ"
#weight:
date: 2022-05-04
tags: 
  - faq
description: >
  
---

## **How can I check the schema is valid before submitting a pull request?**

Run the following (which relies on libxml2-utils):

{{< command-line >}}
xmllint --noout --schema http://www.w3.org/2001/XMLSchema.xsd event-logging-vX.X.X.xsd
{{</ command-line >}}
