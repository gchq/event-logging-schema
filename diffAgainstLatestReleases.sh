#!/bin/bash

set -e

API_URL="https://api.github.com/repos/gchq/event-logging-schema/releases/latest"

#Shell Colour constants for use in 'echo -e'
RED='\033[1;31m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
NC='\033[0m' # No Color

pushd () {
    command pushd "$@" > /dev/null
}

popd () {
    command popd "$@" > /dev/null
}

#Check script arguments
if [ "$#" -ne 1 ] || ! [ -d "$1" ]; then
    echo -e "${RED}INVALID ARGS!${NC}"
    echo -e "Usage: ${GREEN}$0 workingDir${NC}"
    echo -e "Where workingDir is normally ${BLUE}event-logging-transformer-main/pipelines${NC}"
    exit 1
fi

workingDir=$1
pushd ${workingDir}

#Call the github API to git the json for the latest release, then extract the sources jar binary url from it
#GH_USER_AND_TOKEN decalred in .travis.yml env:/global/:secure
sedScript='s/\s*"browser_download_url":.*"\(http.*event-logging-v.*\.xsd\)"/\1/p'
if [ "${GH_USER_AND_TOKEN}x" = "x" ]; then 
    #running locally so do it unauthenticated
    latestUrls=$(curl -s ${API_URL} | sed -ne ${sedScript})
else
    latestUrls=$(curl -s --user "${GH_USER_AND_TOKEN}" ${API_URL} | sed -ne ${sedScript})
fi

echo
echo -e "${YELLOW}Comparing the latest released schemas from github against the ones just generated${NC}"

echo
echo -e "Working directory: [${BLUE}${PWD}${NC}]"
#echo "PWD=${PWD}"
echo
echo -e "${GREEN}Latest release URLs${NC}:\n${latestUrls}"

if [ "${latestUrls}x" = "x" ]; then 
    echo
    echo -e "${RED}ERROR${NC} Latest schema urls could not be found from ${API_URL} json content:"
    #dump out all the download urls
    curl -s ${API_URL} | grep "\"browser_download_url\""
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
    curl -L -s -O ${url}
    popd
    #extract the filename part from the url
    downloadedFilename="$(echo "${url}" | sed -ne 's#http.*/\([^/]*\.xsd\)#\1#p')"
    downloadedFile="./downloaded/${downloadedFilename}"
    #capture the suffix part from the filename, if there is one
    suffix="$(echo "${downloadedFilename}" | sed -ne 's/event-logging-v[0-9]*\(.*\)\.xsd/\1/p')"

    echo -e "Downloaded file: [${GREEN}${downloadedFile}${NC}]"
    echo -e "Suffix: [${GREEN}${suffix}${NC}]"

    #localFile=$(ls ../event-logging-v*${suffix}.xsd)
    localFile="$(find ./generated/ -maxdepth 1 -regex ".*/event-logging-v[0-9]*${suffix}.xsd")"
    if [ "x${localFile}" = "x" ]; then 
        echo -e "${RED}Warning${NC}: no local file found to match downloaded file ${BLUE}${downloadedFile}${NC}"
    else
        echo -e "Local file: [${GREEN}${localFile}${NC}]"

        echo -e "Diffing ${BLUE}${localFile}${NC} against ${BLUE}${downloadedFile}${NC}"
        diffFile="${downloadedFile}.diff"
        #OR with true to stop the exit code from diff stopping the script
        diff ${localFile} ${downloadedFile} > ${diffFile} || true
        if [ $(cat ${diffFile} | wc -l) -gt 0 ]; then
            diff ${localFile} ${downloadedFile} || true
            echo -e "\n${RED}Warning${NC}: Local schema ${BLUE}${localFile}${NC} differs from ${BLUE}${downloadedFile}${NC}"
            echo -e "(see changes in $PWD/${diffFile})${NC}"
        else 
            echo -e "\n${GREEN}Local schema ${BLUE}${localFile}${NC} is identical to ${BLUE}${downloadedFile}${NC}"
        fi
    fi
done <<< "$latestUrls"

exit 0
