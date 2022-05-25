#!/bin/bash

set -eo pipefail

API_URL="https://api.github.com/repos/gchq/event-logging-schema"

#Shell Colour constants for use in 'echo -e'
RED='\033[1;31m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' # No Color

pushd() {
  command pushd "$@" > /dev/null
}

popd() {
  command popd > /dev/null
}

debug_value() {
  local name="$1"; shift
  local value="$1"; shift
  
  if [ "${IS_DEBUG}" = true ]; then
    echo -e "${DGREY}DEBUG ${name}: [${value}]${NC}" >&2
  fi
}

debug() {
  local str="$1"; shift
  
  if [ "${IS_DEBUG}" = true ]; then
    echo -e "${DGREY}DEBUG ${str}${NC}" >&2
  fi
}

check_for_installed_binary() {
  local -r binary_name=$1
  command -v "${binary_name}" 1>/dev/null \
    || error_exit "Binary ${binary_name} is not installed"
}

check_for_installed_binary "jq"

main() {

  #Check script arguments
  if [ "$#" -ne 1 ] || ! [ -d "$1" ]; then
    echo -e "${RED}INVALID ARGS!${NC}"
    echo -e "Usage: ${GREEN}$0 workingDir${NC}"
    echo -e "Where workingDir is normally ${BLUE}event-logging-transformer-main/pipelines${NC}"
    exit 1
  fi

  workingDir=$1
  pushd "${workingDir}"

  local curl_auth_args=()
  if [[ -n "${GIHUB_TOKEN}" ]]; then
    curl_auth_args+=( --header "authorization: Bearer ${GITHUB_TOKEN}" )
  fi

  latest_schema_release_tag="$( \
    curl \
      --silent \
      "${curl_auth_args[@]}" \
      "${API_URL}/releases" \
    | jq -r '.[].tag_name | select(. | test("^v"))' \
    | head -n1 )"

  debug_value "latest_schema_release_tag" "${latest_schema_release_tag}"

  #Call the github API to git the json for the latest release, then extract the sources jar binary url from it#GH_USER_AND_TOKEN decalred in .travis.yml env:/global/:secure
  sedScript='s/\s*"browser_download_url":.*"\(http.*event-logging-v.*\.xsd\)"/\1/p'
  if [ "${GH_USER_AND_TOKEN}x" = "x" ]; then 
    #running locally so do it unauthenticated
    latestUrls=$(curl -s ${API_URL} | sed -ne "${sedScript}")
  else
    latestUrls=$(curl -s --user "${GH_USER_AND_TOKEN}" ${API_URL} | sed -ne "${sedScript}")
  fi

  local latest_schema_release_url="${API_URL}/releases/tags/${latest_schema_release_tag}" 
  local latest_schema_urls
  latest_schema_urls="$( \
    curl \
      --silent \
      "${curl_auth_args[@]}" \
      "${latest_schema_release_url}" \
        | jq -r '.assets[] | select(.browser_download_url | test(".*\\.xsd")) | .browser_download_url' )"

  echo
  echo -e "${YELLOW}Comparing the latest released schemas from github against the ones just generated${NC}"

  echo
  echo -e "Working directory: [${BLUE}${PWD}${NC}]"
  #echo "PWD=${PWD}"
  echo
  echo -e "${GREEN}Latest release URLs${NC}:\n${latestUrls}"

  if [ -z "${latest_schema_urls}" ]; then 
    echo
    echo -e "${RED}ERROR${NC} Latest schema urls could not be found from ${API_URL} json content:"
    #dump out all the download urls
    curl \
      --silent \
      "${curl_auth_args[@]}" \
      "${latest_schema_release_url}" \
        | jq -r '.assets[] | .browser_download_url'
    exit 1
  fi

  mkdir -p downloaded
  rm -rf ./downloaded/*

  #loop over each of the download urls on github
  while read -r url; do
    echo
    echo -e "Downloading schema ${BLUE}${url}${NC}"
    pushd downloaded
    #Download the released schema from github
    #Need -L to follow re-directs as github will redirect the request
    curl -L -s -O "${url}"
    popd
    #extract the filename part from the url
    downloadedFilename="$(echo "${url}" | sed -ne 's#http.*/\([^/]*\.xsd\)#\1#p')"
    downloadedFile="./downloaded/${downloadedFilename}"
    #capture the suffix part from the filename, if there is one
    suffix="$(echo "${downloadedFilename}" | sed -ne 's/event-logging-v[0-9]*\(.*\)\.xsd/\1/p')"

    echo -e "Downloaded file: [${GREEN}${downloadedFile}${NC}]"
    echo -e "Suffix: [${GREEN}${suffix}${NC}]"

    #localFile=$(ls ../event-logging-v*${suffix}.xsd)
    localFile="$(find ./generated/ -maxdepth 1 -regextype posix-extended -regex ".*/event-logging-v([0-9]+|SNAPSHOT)${suffix}.xsd")"
    if [ -z "${localFile}" ]; then 
      echo -e "${RED}Warning${NC}: no local file found to match downloaded file ${BLUE}${downloadedFile}${NC}"
    else
      echo -e "Local file: [${GREEN}${localFile}${NC}]"

      echo -e "Diffing ${BLUE}${localFile}${NC} against ${BLUE}${downloadedFile}${NC}"
      diffFile="${downloadedFile}.diff"
      #OR with true to stop the exit code from diff stopping the script
      diff "${localFile}" "${downloadedFile}" > "${diffFile}" || true
      if [ "$( wc -l < "${diffFile}" )" -gt 0 ]; then
        diff "${localFile}" "${downloadedFile}" || true
        echo -e "\n${RED}Warning${NC}: Local schema ${BLUE}${localFile}${NC} differs from the latest release ${BLUE}${downloadedFile}${NC}"
        echo -e "This will likely be as intended but serves as a confirmation of what has changed since the latest release"
        echo -e "(see changes in $PWD/${diffFile})${NC}"
      else 
        echo -e "\n${GREEN}Local schema ${BLUE}${localFile}${NC} is identical to ${BLUE}${downloadedFile}${NC}"
      fi
    fi
  done <<< "${latest_schema_urls}"

  exit 0
}

main "$@"

# vim: set tabstop=2 shiftwidth=2 expandtab:
