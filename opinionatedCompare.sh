#!/usr/bin/env bash

# Script to diff two .xml/.xsd files. Each file is formatted
# with the output going to a temporay file. The formatting is 
# very opinionated to help us diff as xml rather than text.
# By default diffs with meld but you can use a different diff
# tool if you specify the diff tool binary name as the third
# arg.

set -e

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

debug_value() {
  local name="$1"; shift
  local value="$1"; shift
  
  if [ "${IS_DEBUG}" = true ]; then
    echo -e "${DGREY}DEBUG ${name}: ${value}${NC}"
  fi
}

debug() {
  local str="$1"; shift
  
  if [ "${IS_DEBUG}" = true ]; then
    echo -e "${DGREY}DEBUG ${str}${NC}"
  fi
}

main() {
  IS_DEBUG=false

  setup_echo_colours

  if [[ $# -lt 2 ]]; then
    echo -e "Error: Invalid args. Usage: ${0} file1 file2 [difftool]"
    exit 1
  fi

  local file1="${1}"
  local file2="${2}"
  local diffTool="${3:-meld}"

  if ! command -v "zpretty" 1>/dev/null; then
    echo -e "Error: zpretty not found!"
    exit 1
  fi

  if ! command -v "${diffTool}" 1>/dev/null; then
    echo -e "Error: diff tool binary ${diffTool} not found!"
    exit 1
  fi

  if [[ ! -f "${file1}" ]]; then
    echo -e "File ${file1} doesn't exist"
    exit 1
  fi

  if [[ ! -f "${file2}" ]]; then
    echo -e "File ${file2} doesn't exist"
    exit 1
  fi

  local temp_file1
  temp_file1="$(tempfile -p "before")"
  local temp_file2
  temp_file2="$(tempfile -p "after")"

  # Format the files in a very opinionated way so we can compare the
  # XML
  echo -e "${GREEN}Writing file ${BLUE}${temp_file1}${NC}"
  zpretty --xml "${file1}" > "${temp_file1}"

  echo -e "${GREEN}Writing file ${BLUE}${temp_file2}${NC}"
  zpretty --xml "${file2}" > "${temp_file2}"

  "${diffTool}" "${temp_file1}" "${temp_file2}"

  echo -e "${GREEN}Deleting file ${BLUE}${temp_file1}${NC}"
  rm -f "${temp_file1}"
  echo -e "${GREEN}Deleting file ${BLUE}${temp_file2}${NC}"
  rm -f "${temp_file2}"
}

main "$@"
