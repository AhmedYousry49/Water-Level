const int waterSensorPin = A0; 

void setup() {
  Serial.begin(9600);
}

void loop() {
  int waterLevel = analogRead(waterSensorPin);
  Serial.print("Water Level: ");
Serial.println(waterLevel);
    int phase = map(waterLevel, 250, 400, 0, 4);

   Serial.print("   Water brightness: ");
  Serial.println(phase);
  delay(1000); 
}
