# Frequently Asked Questions

## **How can I check the schema is valid before submitting a pull request?**

Run the following (which relies on libxml2-utils):

``` 
xmllint --noout --schema http://www.w3.org/2001/XMLSchema.xsd event-logging-vX.X.X.xsd
```
