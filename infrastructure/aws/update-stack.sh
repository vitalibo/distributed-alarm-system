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
BUCKET=`param 'Bucket'`
VERSION=`date -u +%Y%m%dT%H%M%SZ`

function params() {
  yq -r ".$1 | to_entries | map(join(\"=\")) | join(\" \")" $STAGE
}

echo 'Package/Copy artifacts initialized'
mvn clean package -DskipTests=true -P $ENVIRONMENT -f ./../../pom.xml

STACK_NAME="$NAME-infrastructure"
PACKAGED_TEMPLATE=`mktemp`
aws cloudformation package \
  --template-file stack.yaml \
  --s3-bucket $BUCKET \
  --output-template-file $PACKAGED_TEMPLATE \
  --profile $PROFILE

aws cloudformation deploy \
  --stack-name $STACK_NAME \
  --parameter-overrides `params 'Parameters'` Version=$VERSION \
  --capabilities 'CAPABILITY_NAMED_IAM' \
  --tags `params 'Tags'` \
  --profile $PROFILE \
  --template-file $PACKAGED_TEMPLATE

echo 'Stack Outputs'
aws cloudformation describe-stacks \
  --stack-name $STACK_NAME \
  --query 'Stacks[0].Outputs' \
  --profile $PROFILE \
  --output text