#!/bin/bash

CATEGORY=$1
ICON_NAME=$2
SRC_PATH=~/git/material-design-icons

cp $SRC_PATH/$CATEGORY/drawable-anydpi-v21/$ICON_NAME.xml res/drawable-anydpi-v21/
cp $SRC_PATH/$CATEGORY/drawable-hdpi/$ICON_NAME.png res/drawable-hdpi/
cp $SRC_PATH/$CATEGORY/drawable-mdpi/$ICON_NAME.png res/drawable-mdpi/
cp $SRC_PATH/$CATEGORY/drawable-xhdpi/$ICON_NAME.png res/drawable-xhdpi/
cp $SRC_PATH/$CATEGORY/drawable-xxhdpi/$ICON_NAME.png res/drawable-xxhdpi/
