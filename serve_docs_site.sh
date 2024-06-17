#!/usr/bin/env bash

# #############################################################################
# Relies on docker and its buildx plugin to do the svg generation and to run
# Script to run the documentation site
# Hugo.
# #############################################################################

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

check_prerequisites() {
  if ! docker version >/dev/null 2>&1; then
    echo -e "${RED}ERROR: Docker is not installed. Please install Docker or Docker Desktop.${NC}"
    exit 1
  fi

  if ! docker buildx version >/dev/null 2>&1; then
    echo -e "${RED}ERROR: Docker buildx plugin is not installed. Please install it. See https://github.com/docker/buildx#installing${NC}"
    exit 1
  fi
}

main() {
  IS_DEBUG=false
  local repo_root
  repo_root="$(git rev-parse --show-toplevel)"
  pushd "${repo_root}" > /dev/null

  setup_echo_colours
  check_prerequisites

  echo -e "${GREEN}Running the Hugo server on localhost:1313${NC}"

  ./container_build/runInHugoDocker.sh server
}

main "$@"
