#!/usr/bin/env bash

set -e
cd $(dirname $0)

if [[ $# -ne 1 ]] ; then
  echo "Usage: $0 [stage]"
  echo ''
  echo 'Options:'
  echo '  stage         The JSON file which contains environment configuration'
  exit 1
fi

STAGE="stage/$1.json"

function param() {
  jq -r ".parameters.$1.value" $STAGE
}

ENVIRONMENT=`param 'environment'`
NAME=`param 'name'`
LOCATION=`param 'location'`
RESOURCE_GROUP="$NAME-$ENVIRONMENT"
VERSION=`date -u +%Y%m%dT%H%M%SZ`

if [[ `az group exists --name $RESOURCE_GROUP` == 'false' ]] ; then
  echo 'Create resource group'
  az group create --name $RESOURCE_GROUP --location $LOCATION --output none
else
  ENABLE_ROLLBACK='--rollback-on-error'
fi

echo 'Create/Update stack initialized'
az group deployment create $ENABLE_ROLLBACK \
  --name "$RESOURCE_GROUP-$VERSION" \
  --mode Complete \
  --resource-group $RESOURCE_GROUP \
  --template-file stack.json \
  --parameters "@$STAGE" \
  --query 'properties.outputs'

echo 'Package/Copy artifacts initialized'
mvn clean package \
  -pl alarm-api/azure -am -DskipTests=true -P $ENVIRONMENT -f ./../../pom.xml \
  -DfunctionAppName=$RESOURCE_GROUP-functions \
  -DfunctionAppRegion=$LOCATION \
  -DfunctionResourceGroup=$RESOURCE_GROUP

mvn azure-functions:deploy -f ./../../alarm-api/azure/pom.xml
