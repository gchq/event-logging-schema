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
1. Change or add to the list of enumerations in `VersionSimplType`. If the change is a breaking change the remove all existign enumerations and add in one for the new version. If it is not a breaking changed add the new version to the list of existing versions.
1. Run the script `validateSchemaVersions.py` to ensure all the schema versions have been set correctly (this will be run by the travis build anyway)
1. Ensure CHANGELOG.md has all changes documented in it
1. Commit and push the new version number changes
1. Create a tag in git for the new release, e.g. `git tag v4.1.2`
1. TODO Push the tag to the remote `git push v4.1.2`. Travis will create a release in github, adding the xsd file as a release artifact
1. Add the new schema to stroom-content
    1. Copy the new schema file into `stroom-content/source/event-logging-xml-schema/stroomContent/XML Schemas/event-logging/` naming it something like `event-logging v4.1.2.XMLSchema.data.xsd`.
    1. Copy the latest `.XMLSchema.xml` files into one named for the new version, e.g. `event-logging v4.1.2.XMLSchema.xml`
    1. Edit this new `.XMLSchema.xml` file:
        1. Update the `<name>` tag to reflect the new version number
        1. If the major version number has changed update the `<namespaceURI>` tag with the new major version number
        1. Update the `<systemId>` tag to reflect the new version number
        1. Replace the content of the `<uuid>` tag with a newly generated UUID.  You can use the linux binary `uuidgen` to generate a new UUID.
    1. Update the `CHANGELOG.md` file in `stroom-content/source/event-logging-xml-schema/`, probably copying the content from the CHANGELOG in the event-logging-schema git repo.
    1. Run the build to build the new pack
    1. Commit and push the changes
    1. In GitHUb create a new release for the updated pack.
1. Update the version numbers in `event-logging.xsd`
    1. Change the `version` attribute to the next intended version number with a SNAPSHOT suffix, e.g. `4.2.0-SNAPSHOT`
    1. Change the `id` attribute to the next intended version number with a SNAPSHOT suffix, e.g. `event-logging-v4.2.0-SNAPSHOT`
    1. Commit and push the change

