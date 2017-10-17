#!/bin/bash

#exit script on any error
set -e

GITHUB_REPO="gchq/event-logging-schema"

#Shell Colour constants for use in 'echo -e'
#e.g.  echo -e "My message ${GREEN}with just this text in green${NC}"
RED='\033[1;31m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' # No Colour 

if [ -n "$TRAVIS_TAG" ]; then
    #Tagged commit so use that as our stroom version, e.g. v3.0.0
    SCHEMA_VERSION="${TRAVIS_TAG}"
fi

#Dump all the travis env vars to the console for debugging
echo -e "TRAVIS_BUILD_NUMBER: [${GREEN}${TRAVIS_BUILD_NUMBER}${NC}]"
echo -e "TRAVIS_COMMIT:       [${GREEN}${TRAVIS_COMMIT}${NC}]"
echo -e "TRAVIS_BRANCH:       [${GREEN}${TRAVIS_BRANCH}${NC}]"
echo -e "TRAVIS_TAG:          [${GREEN}${TRAVIS_TAG}${NC}]"
echo -e "TRAVIS_PULL_REQUEST: [${GREEN}${TRAVIS_PULL_REQUEST}${NC}]"
echo -e "TRAVIS_EVENT_TYPE:   [${GREEN}${TRAVIS_EVENT_TYPE}${NC}]"
echo -e "SCHEMA_VERSION:      [${GREEN}${SCHEMA_VERSION}${NC}]"

# validate the source schema - probably overkill as java will validate the generated schemas
xmllint --noout --schema http://www.w3.org/2001/XMLSchema.xsd ./event-logging.xsd

#Ensure the versions in the schema are all correct
./validateSchemaVersions.py

#run the gradle build to compile the transformations code and generate the 
#schemas from the configured pipelines
./gradlew -Pversion=$SCHEMA_VERSION clean build runShadow

exit 0
