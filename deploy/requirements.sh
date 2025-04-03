#bin/bash

sudo apt install -y i2c-tools git java-common libxi6 libxrender1 libxtst6

cd ~/Downloads

wget https://cdn.azul.com/zulu/bin/zulu21.38.21-ca-jdk21.0.5-linux_arm64.deb


sudo dpkg -i zulu21.38.21-ca-jdk21.0.5-linux_arm64.deb

wget https://download2.gluonhq.com/openjfx/22/openjfx-22_linux-aarch64_bin-sdk.zip

unzip openjfx-22_linux-aarch64_bin-sdk.zip

sudo mkdir -p /usr/lib/jvm
sudo mv openjfx-22 /usr/lib/jvm/openjfx-22
sudo chown -R $USER:$USER /usr/lib/jvm/openjfx-22




#  scp build/libs/WaterLevel.jar  ahmed@192.168.1.102:/home/ahmed  from pc 
# export JAVA_HOME=/usr/lib/jvm/zulu-21-amd64
# export PATH=$JAVA_HOME/bin:$PATH

chmod +x /home/ahmed/start-kiosk.sh
java -jar --module-path /usr/lib/jvm/openjfx-22/javafx-sdk-22/lib/ --add-modules javafx.controls ./WaterLevel.jar
sudo cp /home/ahmed/kiosk.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable kiosk.service
sudo systemctl start kiosk.service



