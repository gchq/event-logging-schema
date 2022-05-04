#!/usr/bin/env bash

set -eo pipefail

setup_echo_colours() {
  # Exit the script on any error
  set -e

  # shellcheck disable=SC2034
  if [ "${MONOCHROME}" = true ]; then
    RED=''
    GREEN=''
    YELLOW=''
    BLUE=''
    BLUE2=''
    DGREY=''
    NC='' # No Colour
  else 
    RED='\033[1;31m'
    GREEN='\033[1;32m'
    YELLOW='\033[1;33m'
    BLUE='\033[1;34m'
    BLUE2='\033[1;34m'
    DGREY='\e[90m'
    NC='\033[0m' # No Colour
  fi
}

main() {
  echo "::group::Build info"
  if [ -n "$BUILD_TAG" ]; then
      #Tagged commit so use that as our stroom version, e.g. v3.0.0
      SCHEMA_VERSION="${BUILD_TAG}"
      DOCS_VERSION="${BUILD_TAG}"
  else
      DOCS_VERSION="SNAPSHOT"
  fi

  BUILD_NAME=event-logging-schema-docs-$DOCS_VERSION
  PDF_FILENAME=${BUILD_NAME}.pdf
  ZIP_FILENAME=${BUILD_NAME}.zip
  RELEASE_ARTEFACTS_DIR_NAME="release_artefacts"
  RELEASE_ARTEFACTS_DIR="${BUILD_DIR}/${RELEASE_ARTEFACTS_DIR_NAME}"
  #RELEASE_ARTEFACTS_REL_DIR="./${RELEASE_ARTEFACTS_DIR_NAME}"
  GENERATED_SITE_DIR="${BUILD_DIR}/docs/public"
  GITHUB_PAGES_DIR="${BUILD_DIR}/gh-pages"

  #Dump all the travis env vars to the console for debugging
  echo -e "BUILD_NUMBER:          [${GREEN}${BUILD_NUMBER}${NC}]"
  echo -e "BUILD_COMMIT:          [${GREEN}${BUILD_COMMIT}${NC}]"
  echo -e "BUILD_BRANCH:          [${GREEN}${BUILD_BRANCH}${NC}]"
  echo -e "BUILD_TAG:             [${GREEN}${BUILD_TAG}${NC}]"
  echo -e "BUILD_PULL_REQUEST:    [${GREEN}${BUILD_PULL_REQUEST}${NC}]"
  echo -e "BUILD_EVENT_TYPE:      [${GREEN}${BUILD_EVENT_TYPE}${NC}]"
  echo -e "RELEASE_ARTEFACTS_DIR: [${GREEN}${RELEASE_ARTEFACTS_DIR}${NC}]"
  echo -e "SCHEMA_VERSION:        [${GREEN}${SCHEMA_VERSION}${NC}]"
  echo -e "PDF_FILENAME:          [${GREEN}${PDF_FILENAME}${NC}]"
  echo -e "ZIP_FILENAME:          [${GREEN}${ZIP_FILENAME}${NC}]"

  mkdir -p "${RELEASE_ARTEFACTS_DIR}"
  echo "::endgroup::"

  # validate the source schema - probably overkill as java will validate 
  # the generated schemas

  echo -e "::group::Validating source schema"
  xmllint \
    --noout \
    --schema http://www.w3.org/2001/XMLSchema.xsd \
    ./event-logging.xsd
  echo "::endgroup::"

  echo -e "::group::Running gradle build"
  #run the gradle build to compile the transformations code and generate the 
  #schemas from the configured pipelines
  #The build will also validate the versions in the source schema
  ./gradlew \
    --no-daemon \
    -Pversion="${SCHEMA_VERSION}" \
    clean \
    build \
    runShadow \
    -x diffAgainstLatest 

  ls -l "${BUILD_DIR}/event-logging-transformer-main/build/libs/"

  ls -l "${BUILD_DIR}"/event-logging-transformer-main/build/libs/event-logging-transformer*-all.jar

  cp \
    "${BUILD_DIR}"/event-logging-transformer-main/build/libs/event-logging-transformer*-all.jar \
    "${RELEASE_ARTEFACTS_DIR}"

  GENERATED_DIR="event-logging-transformer-main/pipelines/generated"
  echo "Deleting generated schemas that are not release artifacts"
  # This is to make sure they don't get picked up for release
  rm -v "${GENERATED_DIR}"/event-logging-v999-documentation.xsd
  rm -v "${GENERATED_DIR}"/test-v*-test.xsd
  rm -v "${GENERATED_DIR}"/identity-v*-identity.xsd

  # Copy the schemas to the release dir for upload to gh releases
  cp "${GENERATED_DIR}"/*.xsd "${RELEASE_ARTEFACTS_DIR}/"
  echo "::endgroup::"

  # build the gitbook
  echo -e "::group::${GREEN}Building the docs${NC}"
  ./container_build/runInHugoDocker.sh "build"
  echo "::endgroup::"

  #echo "Highlighting un-converted markdown files as they could be missing from the SUMMARY.md" 
  #find ./_book/ -name "*.md"
  #mdFileCount=$(find ./_book/ -name "*.md" | wc -l)
  #if [ "${mdFileCount}" -gt 0 ]; then
      #echo -e "${RED}ERROR${NC} - $mdFileCount unconverted markdown files" \
        #"exist, add them to the SUMMARY.md so they are converted"
      #exit 1
  #fi

  #build a pdf of the docs and genrate a zip of the static html, for release to github

  echo -e "::group::${GREEN}Gathering artefacts${NC}"

  echo "Build name - ${BUILD_NAME}," \
    "pdf file - ${PDF_FILENAME}," \
      "zip file - ${ZIP_FILENAME}"

  # generate a pdf of the gitbook
  # TODO change to generate the pdf of the hugo site
  #./container_build/runInNodeDocker.sh \
    #"gitbook pdf ./ \"${RELEASE_ARTEFACTS_REL_DIR}/${PDF_FILENAME}\""

  echo "Making a zip of the html content"
  pushd "${GENERATED_SITE_DIR}"
  zip -r -9 "${RELEASE_ARTEFACTS_DIR}/${ZIP_FILENAME}" ./*
  popd

  echo -e "${GREEN}Dumping contents of ${RELEASE_ARTEFACTS_DIR}${NC}"
  ls -1 "${RELEASE_ARTEFACTS_DIR}/"

  # We release on every commit to master
  if [[ -n "${BUILD_TAG}" && "${BUILD_IS_PULL_REQUEST}" != "true" ]]; then

    mkdir -p "${GITHUB_PAGES_DIR}"

    echo -e "${GREEN}Copying from ${GENERATED_SITE_DIR}/ to ${GITHUB_PAGES_DIR}/${NC}"
    cp -r "${GENERATED_SITE_DIR}"/* "${GITHUB_PAGES_DIR}/"
  fi
  echo "::endgroup::"
}

main "$@"
