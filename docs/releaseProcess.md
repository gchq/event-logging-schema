# Release Process

See [Change Log](CHANGELOG.md) for details of the version numbering approach.

The definition of breaking change is one which is not backwards compatible with the latest release.  This is both in terms of an XML document conforming to the event-logging schema and the Java JAXB api in [event-logging](https://github.com/gchq/event-loggin). A non-exhaustive list of examples of a breaking change are:

* New mandatory `xs:element` or `xs:attribute`
* Removal of an `xs:element` or `xs:attribute`
* Changing an `xs:element` to be mandatory
* Changing the position of an element within an `xs:sequence`
* Changing the position of an attribute within an `xs:element`
* Renaming an `xs:element` or `xs:attribute`
* Renaming a complex type

## Release Steps

1. Decide what the new version number will be
1. Ensure all changes to `event-logging.xsd` required for the new release have been pushed to master 
1. Verify the travis build is passing, i.e. the xsd has been validated.
1. If the change is a breaking change: 
    1. Increment the major version number in `xmlns:evt`, e.g. `event-logging:4`
    1. Increment the major version number in `targetNamespace`, e.g. `event-logging:4`
1. Change the `version` attribute to the intended version number, e.g. `4.1.2`
1. Change the `id` attribute to the intended version number, e.g. `event-logging-v4.1.2`
1. Commit and push the new version number changes
1. Create a tag in git for the new release, e.g. `git tag v4.1.2`
1. Push the tag to the remote `git push v4.1.2`. Travis will create a release in github, adding the xsd file as a release artifact


