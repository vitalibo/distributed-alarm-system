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

STAGE="../../infrastructure/aws/stage/$1.yaml"
PROFILE=`[[ $# -eq 2 ]] && echo $2 || echo 'default'`

function param() {
  yq -r ".Parameters.$1" $STAGE
}

ENVIRONMENT=`param 'Environment'`
NAME=`param 'Name'`
BUCKET="s3://`param 'Bucket'`/$NAME/$ENVIRONMENT"
VERSION=`date -u +%Y%m%dT%H%M%SZ`

function params() {
  yq -r ".$1 | to_entries | map(join(\"=\")) | join(\" \")" $STAGE
}

mvn clean package -pl alarm-processor/aws -am -Dmaven.test.skip=true -P $ENVIRONMENT -f ../../pom.xml

echo 'Create/Update stack initialized'
for MODULE in 'alarm-processor'; do
  aws s3 cp --profile $PROFILE "target/${MODULE}-aws-1.0-SNAPSHOT.jar" "$BUCKET/$VERSION/"
done

function output() {
  aws cloudformation describe-stacks \
    --stack-name "$NAME-infrastructure" \
    --profile $PROFILE \
    --query 'Stacks[0].Outputs[?OutputKey==`'$1'`].OutputValue' \
    --output text
}

aws cloudformation deploy \
  --stack-name "$NAME-alarm-stream" \
  --parameter-overrides `params 'Parameters'` Version=$VERSION LivyHost=`output 'EmrAddress'` \
  --capabilities 'CAPABILITY_NAMED_IAM' \
  --profile ${PROFILE} \
  --template-file stack.yaml