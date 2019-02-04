#!/bin/bash

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
# .xml files.

# shellcheck disable=SC2034
{
  #Colour constants for use in echo -e statements
  RED='\033[1;31m'
  GREEN='\033[1;32m'
  YELLOW='\033[1;33m'
  BLUE='\033[1;34m'
  NC='\033[0m' # No Colour
}

main() {

  echo -e "Validating file ${BLUE}${example_file}${NC}"

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

    # 1st sed script deletes from first line upto the match (inc.)
    # 2nd sed script delete from match (inc.) to last line
    sed '1,/^```\s*xml$/d; /^```$/,$d' "${example_file}" |
      xmllint --noout --nowarning --schema "${schema_file}" -

    validation_exit_status="$?"
  else
    # Plain old XML file so validate normally
    xmllint --noout --nowarning --schema "${schema_file}" "${example_file}"
    validation_exit_status="$?"
  fi

  if [ "${validation_exit_status}" -eq 0 ]; then
    echo -e "Validation ${GREEN}PASSED${NC}"
  else
    echo -e "Validation ${RED}FAILED${NC}"
  fi

  exit "${validation_exit_status}"
}

if ! command -v xmllint 1>/dev/null; then
  echo -e "\n${RED}ERROR${NC} - ${BLUE}xmllint${NC} binary is not installed." \
    "It is available in the package '${BLUE}libxml2-utils${NC}'"
  exit 1
fi

if [ $# -ne 2 ]; then
  echo -e "${RED}ERROR${NC} - Invalid arguments."
  echo -e "Usage: ${BLUE}$0 schema_file example_file${NC}"
  exit 1
fi

schema_file="$1"
example_file="$2"

main "$@"

