#!/bin/sh


SCHEMA_FILE_MAJOR_VERSION=3
SCHEMA_FILE_VERSION=$SCHEMA_FILE_MAJOR_VERSION.0.0

echo "Checking stroom samples copy"
diff -w versions/v$SCHEMA_FILE_MAJOR_VERSION/event-logging-v$SCHEMA_FILE_VERSION.xsd ../stroom/stroom-core-server/src/test/resources/samples/config/XML\ Schemas/event-logging/event-logging\ v$SCHEMA_FILE_VERSION.XMLSchema.data.xsd
echo ""

echo "Checking stroom-content copy"
diff -w versions/v$SCHEMA_FILE_MAJOR_VERSION/event-logging-v$SCHEMA_FILE_VERSION.xsd ../stroom-content/stroom-content-source/event-logging-xml-schema/XML\ Schemas/event-logging/event-logging\ v$SCHEMA_FILE_VERSION.XMLSchema.data.xsd
echo ""

echo "Checking event-logging copy (NOTE: at the moment they should differ around the Event and Events element)"
#schema.xsd should have both Events and Event as top level element to allow the sending of a single Event or multiple Event elements within Events
diff -w versions/v$SCHEMA_FILE_MAJOR_VERSION/event-logging-v$SCHEMA_FILE_VERSION.xsd ../event-logging/generator/schema.xsd
echo ""

