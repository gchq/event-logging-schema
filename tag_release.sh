#!/usr/bin/env bash

# This script creates and pushes a git annotated tag with a commit message taken from
# the appropriate section of the CHANGELOG.md.
set -e

setup_echo_colours() {

    #Shell Colour constants for use in 'echo -e'
    # shellcheck disable=SC2034
    {
        RED='\033[1;31m'
        GREEN='\033[1;32m'
        YELLOW='\033[1;33m'
        BLUE='\033[1;34m'
        LGREY='\e[37m'
        DGREY='\e[90m'
        NC='\033[0m' # No Color
    }
}

error_exit() {
    msg="$1"
    echo -e "${RED}ERROR${GREEN}: ${msg}${NC}"
    echo
    exit 1
}

main() {
    # Git tags should match this regex to be a release tag
    local -r release_version_regex='^v[0-9]+\.[0-9]+(\.[0-9]+|-(alpha|beta)\.[0-9]+)$'
    local -r changelog_file="CHANGELOG.md"
    local -r schema_file="event-logging.xsd"

    setup_echo_colours
    echo


    if [ $# -ne 1 ]; then
        echo -e "${RED}ERROR${GREEN}: Missing version argument${NC}"
        echo -e "${GREEN}Usage: ${BLUE}./tag_release.sh version${NC}"
        echo -e "${GREEN}e.g:   ${BLUE}./tag_release.sh v3.4.1${NC}"
        echo
        echo -e "${GREEN}This script will extract the changes from the ${BLUE}CHANGELOG.md${GREEN} file for the passed${NC}"
        echo -e "${GREEN}version tag and create an annotated git commit with it. The tag commit will be pushed${NC}"
        echo -e "${GREEN}to the origin.${NC}"
        exit 1
    fi

    local version=$1
    local curr_date
    curr_date="$(date +%Y-%m-%d)"
    
    if [[ ! "${version}" =~ ${release_version_regex} ]]; then
        error_exit "Version [${BLUE}${version}${GREEN}] does not match the release version regex ${BLUE}${release_version_regex}${NC}"
    fi

    if [ ! -f "${changelog_file}" ]; then
        error_exit "The file ${BLUE}${changelog_file}${GREEN} does not exist in the current directory.${NC}"
    fi

    if ! git rev-parse --show-toplevel > /dev/null 2>&1; then
        error_exit "You are not in a git repository. This script should be run from the root of a repository.${NC}"
    fi

    if git tag | grep -q "^${version}$"; then
        error_exit "This repository has already been tagged with [${BLUE}${version}${GREEN}].${NC}"
    fi

    if grep -q "\"(event-logging-v)?SNAPSHOT\"" "${schema_file}"; then
        error_exit "The Schema ${BLUE}${schema_file}${GREEN} contains ${BLUE}SNAPSHOT${GREEN} versions"
    fi

    if ! grep -q "^\s*##\s*\[${version}\]" "${changelog_file}"; then
        error_exit "Version [${BLUE}${version}${GREEN}] is not in the CHANGELOG.${NC}"
    fi

    if ! grep -q "^\s*##\s*\[${version}\] - ${curr_date}" "${changelog_file}"; then
        error_exit "Cannot find a heading with today's date [${BLUE}## [${version}] - ${curr_date}${GREEN}] in the CHANGELOG.${NC}"
    fi

    if ! grep -q "^\[${version}\]:" "${changelog_file}"; then
        echo -e "${RED}ERROR${GREEN}: Version [${BLUE}${version}${GREEN}] does not have a link entry at the bottom of the CHANGELOG.${NC}"
        echo -e "${GREEN}e.g.:${NC}"
        echo -e "${BLUE}[v3.3.1]: https://github.com/gchq/stroom/compare/v3.3.0...v3.3.1${NC}"
        echo
        exit 1
    fi

    if [ "$(git status --porcelain 2>/dev/null | wc -l)" -ne 0 ]; then
        error_exit "There are uncommitted changes or untracked files. Commit them before tagging.${NC}"
    fi

    local change_text
    # delete all lines up to and including the desired version header
    # then output all lines until quitting when you hit the next 
    # version header
    change_text="$(sed "1,/^\s*##\s*\[${version}\]/d;/## \[/Q" "${changelog_file}")"

    # Add the release version as the top line of the commit msg, followed by
    # two new lines
    change_text="${version}\n\n${change_text}"

    # Remove any repeated blank lines with cat -s
    change_text="$(echo -e "${change_text}" | cat -s)"

    echo -e "${GREEN}You are about to create the git tag ${BLUE}${version}${GREEN}${NC}"
    echo -e "${GREEN}with the following commit message.${NC}"
    echo -e "${GREEN}If this is pretty empty then you need to add some entries to the ${BLUE}${changelog_file}${NC}"
    echo -e "${DGREY}------------------------------------------------------------------------${NC}"
    echo -e "${YELLOW}${change_text}${NC}"
    echo -e "${DGREY}------------------------------------------------------------------------${NC}"

    read -rsp $'Press "y" to continue, any other key to cancel.\n' -n1 keyPressed

    if [ "$keyPressed" = 'y' ] || [ "$keyPressed" = 'Y' ]; then
        echo
        echo -e "${GREEN}Tagging the current commit${NC}"
        echo -e "${change_text}" | git tag -a --file - "${version}"

        echo -e "${GREEN}Pushing the new tag${NC}"
        git push origin "${version}"

        echo -e "${GREEN}Done.${NC}"
        echo
    else
        echo
        echo -e "${GREEN}Exiting without tagging a commit${NC}"
        echo
        exit 0
    fi
}

main "$@"
