#!/bin/bash

#exit script on any error
set -e

# shellcheck disable=SC2034
{
  #Shell Colour constants for use in 'echo -e'
  #e.g.  echo -e "My message ${GREEN}with just this text in green${NC}"
  RED='\033[1;31m'
  GREEN='\033[1;32m'
  YELLOW='\033[1;33m'
  BLUE='\033[1;34m'
  NC='\033[0m' # No Colour 
}

if [ -n "$TRAVIS_TAG" ]; then
    #Tagged commit so use that as our stroom version, e.g. v3.0.0
    SCHEMA_VERSION="${TRAVIS_TAG}"
    DOCS_VERSION="${TRAVIS_TAG}"
else
    DOCS_VERSION="SNAPSHOT"
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

#run the gradle build to compile the transformations code and generate the 
#schemas from the configured pipelines
#The build will also validate the versions in the source schema
./gradlew -Pversion="${SCHEMA_VERSION}" clean build runShadow -x diffAgainstLatest 

GENERATED_DIR="event-logging-transformer-main/pipelines/generated"
echo "Deleting generated schemas that are not release artifacts"
# This is to make sure they don't get picked up for release
rm -v "${GENERATED_DIR}"/event-logging-v999-documentation.xsd
rm -v "${GENERATED_DIR}"/test-v*-test.xsd
rm -v "${GENERATED_DIR}"/identity-v*-identity.xsd

#Now build the gitbook
sudo mv ebook-convert /usr/local/bin/
gitbook install

#Convert our markdown into static html
gitbook build

echo "Highlighting un-converted markdown files as they could be missing from the SUMMARY.md" 
find ./_book/ -name "*.md"
mdFileCount=$(find ./_book/ -name "*.md" | wc -l)
if [ "${mdFileCount}" -gt 0 ]; then
    echo -e "${RED}ERROR${NC} - $mdFileCount unconverted markdown files exist, add them to the SUMMARY.md so they are converted"
    exit 1
fi

#build a pdf of the docs and genrate a zip of the static html, for release to github
export BUILD_NAME=event-logging-schema-docs-$DOCS_VERSION
export PDF_FILENAME=$BUILD_NAME.pdf
export ZIP_FILENAME=$BUILD_NAME.zip
echo "Build name - $BUILD_NAME, pdf file - $PDF_FILENAME, zip file - $ZIP_FILENAME"
gitbook pdf ./ ./$PDF_FILENAME
echo "Making a zip of the html content"
pushd _book
zip -r -9 ../$ZIP_FILENAME ./*
popd

exit 0
