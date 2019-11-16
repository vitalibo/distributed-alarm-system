#!/usr/bin/env bash

set -e
cd $(dirname $0)

if [[ $# -ne 1 ]] && [[ $# -ne 2 ]]; then
  echo "Usage: $0 [stage] [profile]"
  echo ''
  echo 'Options:'
  echo '  stage         The JSON file which contains environment configuration'
  echo '  profile       Use a specific AWS profile from your credential file'
  exit 1
fi

STAGE="stage/$1.yaml"
PROFILE=`[[ $# -eq 2 ]] && echo $2 || echo 'default'`

function param() {
  yq -r ".Parameters.$1" $STAGE
}

ENVIRONMENT=`param 'Environment'`
NAME=`param 'Name'`
VERSION=`date -u +%Y%m%dT%H%M%SZ`

function params() {
  yq -r ".$1 | to_entries | map(join(\"=\")) | join(\" \")" $STAGE
}

echo 'Package/Copy artifacts initialized'
# TODO: copy artifacts to S3

aws cloudformation deploy \
  --stack-name "$NAME-infrastructure" \
  --parameter-overrides `params 'Parameters'` Version=$VERSION \
  --capabilities 'CAPABILITY_NAMED_IAM' \
  --tags `params 'Tags'` \
  --profile $PROFILE \
  --template-file stack.yaml