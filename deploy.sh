#!/bin/bash

JAR_NAME=objectsdetection-1.0-SNAPSHOT.jar
HOME_DIR=/home/mahima
PROGRAM_DIR=$HOME_DIR/IdeaProjects/ObjectsDetect
IMAGE_DIR=$HOME_DIR/vlcimages
JAVA_BIN=/usr/lib/jvm/java-8-oracle/jre/bin/java
GITHUB_USERNAME=abryu
GITHUB_REPO=ObjectsDetection

# Remove Existing Images, Jar, and Logs

rm $PROGRAM_DIR/*-SNAPSHOT.jar
rm -rf $PROGRAM_DIR/logs
rm $IMAGE_DIR/*

# Download the latest Release
cd $PROGRAM_DIR
curl -s https://api.github.com/repos/$GITHUB_USERNAME/$GITHUB_REPO/releases/latest \
| grep "browser_download_url.*jar" \
| cut -d : -f 2,3 \
| tr -d \" \
| wget -qi -

# Pre-Configure
cp -rf $HOME_DIR/config $PROGRAM_DIR
mkdir $PROGRAM_DIR/logs

# Kill Running Conflict Processes
ps -ef | grep java | grep -v grep | awk '{print $2}' | xargs kill
ps -ef | grep vlc | grep -v grep | awk '{print $2}' | xargs kill
pwd
# Run
$JAVA_BIN -jar $JAR_NAME
