#/bin/sh

file="./local.properties"

if [ -f "$file" ]
then
  while IFS='=' read -r key value
  do
    key=$(echo $key | tr '.' '_')
    eval ${key}=\${value}
  done < "$file"

  ./gradlew clean build  bintrayUpload -PbintrayUser=$jcenterName -PbintrayKey=$jcenterKey -PdryRun=false
else
  echo "$file not found."
fi
