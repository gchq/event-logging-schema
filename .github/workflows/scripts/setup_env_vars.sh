#!/usr/bin/env bash

set -eo pipefail

# Set variables in github's special env file which are then automatically 
# read into env vars in each subsequent step

GIT_API_URL="https://api.github.com/repos/gchq/event-logging-schema"
is_schema_release=false
is_docs_release=false

echo -e "${GREEN}GITHUB_REF: ${BLUE}${GITHUB_REF}${NC}"

if [[ ${GITHUB_REF} =~ ^refs/tags/ ]]; then
  # This is build is for a git tag
  # strip off the 'refs/tags/' bit
  tag_name="${GITHUB_REF#refs/tags/}"
  if [[ "${tag_name}" =~ ^v ]]; then
    # e.g. 'v4.0.2'
    is_schema_release=true
    echo "tag_name=${tag_name}"
  fi
elif [[ ${GITHUB_REF} =~ ^refs/heads/ ]]; then
  # This build is just a commit on a branch
  # strip off the 'ref/heads/' bit
  build_branch="${GITHUB_REF#refs/heads/}"
fi

echo -e "${GREEN}Grepping git commit msg for publish keyword${NC}"
git --no-pager log -1 --pretty=format:"%s" \
  | head -n1 \
  | grep -i "\[publish\]" || true
echo -e "${GREEN}----${NC}"

# If releases are only done nighly this allows us to force one for testing
# So just include '[publish]' in the commit msg.
# Don't force if scheduled as a scheduled job just builds the latest commit
# which may have force on it.
if [[ "${build_branch}" = "master" && "${GITHUB_EVENT_NAME}" != "schedule" ]] \
  && git --no-pager log -1 --pretty=format:"%s" \
    | head -n1 \
    | grep -q -i "\[publish\]"; then

  # Seen '[publish]' in the latest commit msg on master
  is_forced_docs_release=true;
  is_docs_release=true
else
  is_forced_docs_release=false
fi
echo -e "${GREEN}is_forced_docs_release: ${BLUE}${is_forced_docs_release}${NC}"

# Get the release number of the latest docs release.
# Tags of the form docs-vNNNNN
# DO NOT echo the token
latest_docs_release_num=
# No point doing this work if we are releasing the schema
if [[ "${is_schema_release}" = "false" ]]; then
  latest_docs_release_num="$( \
    curl \
      --silent \
      --header "authorization: Bearer ${GITHUB_TOKEN}" \
      "${GIT_API_URL}/releases" \
    | jq -r '.[].tag_name | select(. | test("^docs-v"))' \
    | sed 's/^docs-v//' \
    | sort \
    | tail -n1)"

  latest_docs_release_tag_name=
  latest_docs_release_tag_sha=
  if [[ -n "${latest_docs_release_num}" ]]; then
    latest_docs_release_tag_name="docs-v${latest_docs_release_num}"
    # Get the commit sha for the tag.
    # DO NOT echo the token
    latest_docs_release_tag_sha="$( \
      curl \
        --silent \
        --header "authorization: Bearer ${GITHUB_TOKEN}" \
        "${GIT_API_URL}/git/ref/tags/${latest_docs_release_tag_name}" \
      | jq -r '.object.sha')"
  fi
  # Echoing for debug, not to set it in env vars
  echo -e "${GREEN}latest_docs_release_num: ${BLUE}${latest_docs_release_num}${NC}"
  echo -e "${GREEN}latest_docs_release_tag_name: ${BLUE}${latest_docs_release_tag_name}${NC}"
  echo -e "${GREEN}latest_docs_release_tag_sha: ${BLUE}${latest_docs_release_tag_sha}${NC}"
  echo -e "${GREEN}GITHUB_SHA: ${BLUE}${GITHUB_SHA}${NC}"

  if [[ "${GITHUB_SHA}" = "${latest_docs_release_tag_sha}" ]]; then
    is_same_commit_as_last_release=true
  else
    is_same_commit_as_last_release=false
  fi

  echo -e "${GREEN}is_same_commit_as_last_release:" \
    "${BLUE}${is_same_commit_as_last_release}${NC}"

  # Determine if we need to perform a release or not
  # As we have scheduled runs we will get multiple runs for the same commit
  if [[ "${is_same_commit_as_last_release}" = "false" \
    && ( ${GITHUB_EVENT_NAME} = "schedule" || "${is_forced_docs_release}" = "true" ) \
    ]]; then
    is_docs_release=true
  fi
  echo -e "${GREEN}is_docs_release: ${BLUE}${is_docs_release}${NC}"
fi

# Brace block means all echo stdout get appended to GITHUB_ENV
{
  # Map the GITHUB env vars to our own
  echo "BUILD_DIR=${GITHUB_WORKSPACE}"
  echo "BUILD_COMMIT=${GITHUB_SHA}"

  build_number="${GITHUB_RUN_NUMBER}"
  echo "BUILD_NUMBER=${build_number}"

  echo "ACTIONS_SCRIPTS_DIR=${GITHUB_WORKSPACE}/.github/workflows/scripts"

  if [[ ${GITHUB_REF} =~ ^refs/heads/ ]]; then
    echo "BUILD_BRANCH=${build_branch}"
  fi

  if [[ -n ${latest_docs_release_tag_name} ]]; then
    echo "LATEST_DOCS_RELEASE_TAG_NAME=${latest_docs_release_tag_name}"
  fi

  # Only do a release based on our schedule, e.g. nightly
  # Skip release if we have same commit as last time
  if [[ "${is_docs_release}" = "true" ]]; then
    echo "BUILD_IS_DOCS_RELEASE=true"
    echo "BUILD_IS_SCHEMA_RELEASE=false"
    echo "BUILD_TAG=docs-v${build_number}"
  elif [[ "${is_schema_release}" = "true" ]]; then
    echo "BUILD_IS_DOCS_RELEASE=false"
    echo "BUILD_IS_SCHEMA_RELEASE=true"
    echo "BUILD_TAG=${GITHUB_REF#refs/tags/}"
  else
    echo "BUILD_IS_DOCS_RELEASE=false"
    echo "BUILD_IS_SCHEMA_RELEASE=false"
  fi

  # TODO Not sure BUILD_IS_FORCED_RELEASE is used anywhere
  if [[ "${is_forced_docs_release}" = "true" ]]; then
    echo "BUILD_IS_FORCED_DOCS_RELEASE=true"
  else
    echo "BUILD_IS_FORCED_DOCS_RELEASE=false"
  fi

  if [[ ${GITHUB_REF} =~ ^refs/pulls/ ]]; then
    echo "BUILD_IS_PULL_REQUEST=true"
  else
    echo "BUILD_IS_PULL_REQUEST=false"
  fi

} >> "${GITHUB_ENV}"
