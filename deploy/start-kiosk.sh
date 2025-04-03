#!/bin/bash

LOGFILE="/home/ahmed/kiosk.log" 

echo "Current date and time: $(date)" >> $LOGFILE 2>&1
echo 'Hiding the mouse cursor...' >> $LOGFILE 2>&1

# unclutter -idle 0.1 -root &

export ENABLE_GLUON_COMMERCIAL_EXTENSIONS=true

echo 'Starting javaFX...' >> $LOGFILE 2>&1

/opt/jdk/bin/java \
  -Degl.displayid=/dev/dri/card0 \
  -Dmonocle.egl.lib=/usr/lib/jvm/openjfx-22/javafx-sdk-22/lib/libgluon_drm-1.1.7.so \
  -Djava.library.path=/usr/lib/jvm/openjfx-22/javafx-sdk-22/lib/ \
  -Dmonocle.platform.traceConfig=false \
  -Dprism.verbose=false \
  -Djavafx.verbose=false \
  -Dmonocle.platform=EGL \
  --module-path /usr/lib/jvm/openjfx-22/javafx-sdk-22/lib/ \
  --add-modules javafx.controls,javafx.fxml \
  -jar /home/ahmed/WaterLevel.jar  >> $LOGFILE 2>&1
