#!/usr/bin/env bash

set -e

# Script to validate an example xml file against a schema
# If the file ends with .xml.md then we assume the XML is inside
# a single fenced block in the markdown file, like so
#
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Some markdown content
#
# ``` xml
# <?xml version="1.0" encoding="UTF-8"?>
# <root>
# </root>
# ```
# Some markdown content
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# If it is inside a markdown file then we strip everything around and
# including the code fences to leave pure XML to validate
#
# The script handles either .xml.md files as described above or plain
# .xml files. In both cases, the xml must be fully formed and not a fragment
# else it will not be valid against the schema.

setup_echo_colours() {
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

validate_dir() {
  local schema_file="$1"; shift
  local examples_dir="$1"; shift
  local this_script="${SCRIPT_DIR}/${SCRIPT_NAME}"
  debug_value "this_script" "${this_script}"

  local regex_pattern=".*\.xml(\.md)?" 
  local file_count
  file_count="$(
    find \
        "${examples_dir}" \
        -type f \
        -regextype posix-egrep \
        -regex "${regex_pattern}" \
        -print \
        -quit \
      | wc -l
    )"

  if [[ "${file_count}" -gt 0 ]]; then
    echo -e "${GREEN}Validating all *.xml & *.xml.md files in" \
      "${BLUE}${examples_dir}${NC}"
    # Find all .xml.md and .xml files and validate them by calling this script
    # for a single file
    find \
      "${examples_dir}" \
      -type f \
      -regextype posix-egrep \
      -regex "${regex_pattern}" \
      -exec "${this_script}" "${schema_file}" {} \;
  else
    echo -e "\n${RED}ERROR${NC} - No files matching pattern" \
      "${BLUE}${regex_pattern}${NC} found in ${BLUE}${examples_dir}${NC}."
  fi
}

validate_file() {
  local schema_file="$1"; shift
  local example_file="$1"; shift

  echo -e "Validating file ${BLUE}${example_file}${NC}"
  echo -e "  Using schema: ${BLUE}${schema_file}${NC}"

  if [[ "${example_file}" =~ .*\.xml\.md ]]; then
    # XML inside markdown so assume the file contains a single fenced block
    # e.g.
    # ``` xml
    # <some_xml/>
    # ```
    # Then strip everything up to and including the start fence and everything
    # including and beyond the end fence, to leave pure XML that we can 
    # validate.

    # xmlint is available in libxml2-utils

    xml_fenced_block_count=

    # shellcheck disable=SC2016
    xml_fenced_block_count=$(grep -c '^```\s*xml$' "${example_file}")

    if [ "${xml_fenced_block_count}" -ne 1 ]; then
      echo -e "\n${RED}ERROR${NC} - Expecting only one ${BLUE}'\`\`\` xml' fenced" \
        "block in file ${BLUE}${example_file}${NC}, found" \
        "${BLUE}${xml_fenced_block_count}${NC}"
      exit 1
    fi

    # 1st sed script deletes from first line upto the match (inc.)
    # 2nd sed script delete from match (inc.) to last line
    # pipe the left over xml to xmllint to validate
    sed '1,/^```\s*xml$/d; /^```$/,$d' "${example_file}" |
      xmllint --noout --nowarning --schema "${schema_file}" -

    validation_exit_status="$?"
  else
    # Plain old XML file so validate normally
    xmllint --noout --nowarning --schema "${schema_file}" "${example_file}"
    validation_exit_status="$?"
  fi

  if [ "${validation_exit_status}" -eq 0 ]; then
    echo -e "  Validation ${GREEN}PASSED${NC}"
  else
    echo -e "  Validation ${RED}FAILED${NC}"
  fi

  exit "${validation_exit_status}"
}

main() {
  local schema_file="$1"; shift
  local example_file_or_dir="$1"; shift
  
  if [[ -f "${example_file_or_dir}" ]]; then
    validate_file "${schema_file}" "${example_file_or_dir}"
  elif [[ -d "${example_file_or_dir}" ]]; then
    validate_dir "${schema_file}" "${example_file_or_dir}"
  else
    echo -e "\n${RED}ERROR${NC} - ${BLUE}${example_file_or_dir}${NC}" \
      "is not a file or directory"
  fi
  echo -e "${GREEN}Done${NC}"
}

########################
#  Script starts here  #
########################

setup_echo_colours

if ! command -v xmllint 1>/dev/null; then
  echo -e "\n${RED}ERROR${NC} - ${BLUE}xmllint${NC} binary is not installed." \
    "It is available in the package '${BLUE}libxml2-utils${NC}'"
  exit 1
fi

if [ $# -ne 2 ]; then
  echo -e "${RED}ERROR${NC} - Invalid arguments."
  echo -e "Usage: ${BLUE}$0 schema_file example_file_or_dir${NC}"
  exit 1
fi

SCRIPT_NAME="$( basename "$0" )"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

main "$@"
