#
# Copyright (c) 2-2/8/23, 11:42 PM
# Created by https://github.com/alwayswanna
#

# build & test all maven modules
mvn clean package --quiet

##################################################################################
# shellcheck disable=SC2164
cd fellowworkerfront
# build flutter models.
flutter pub run build_runner build
# build web layer to output directory
flutter build web
# shellcheck disable=SC2103
cd ..
# build Docker file in current directory
docker build "fellowworkerfront/." -t alwayswanna/fellow-worker:frontend
# push image to repository
docker push alwayswanna/fellow-worker:frontend

##################################################################################
# build image for client-manager
docker build "client-manager/." -t alwayswanna/fellow-worker:client-manager
# push image to repository for client-manager
docker push alwayswanna/fellow-worker:client-manager

##################################################################################
# build image for cv-generator
docker build "cv-generator/." -t alwayswanna/fellow-worker:cv-generator
# push image to repository for cv-generator
docker push alwayswanna/fellow-worker:cv-generator

##################################################################################
# build image for fellow-worker-service
docker build "fellow-worker-service/." -t alwayswanna/fellow-worker:fellow-worker-service
# push image to repository for fellow-worker-service
docker push alwayswanna/fellow-worker:fellow-worker-service

##################################################################################
# build image for oauth2-server
docker build "oauth2-server/." -t alwayswanna/fellow-worker:oauth2-server
# push image to repository for oauth2-server
docker push alwayswanna/fellow-worker:oauth2-server

echo 'Build completed ...'
