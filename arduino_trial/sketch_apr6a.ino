#include <Servo.h>
#include <Arduino.h>
#include "SerialCommands.h"

const int waterSensorPin = A0; // Connect the sensor signal to A0


Servo myservo;  // create servo object to control a servo
int pwm =0;
char serial_command_buffer_[32];
SerialCommands serial_commands_(&Serial, serial_command_buffer_, sizeof(serial_command_buffer_), "\r\n", " ");

void cmd_unrecognized(SerialCommands* sender, const char* cmd)
{
  sender->GetSerial()->print("Unrecognized command [");
  sender->GetSerial()->print(cmd);
  sender->GetSerial()->println("]");
}

void cmd_SYNC(SerialCommands* sender)
{

  sender->GetSerial()->println("SYNC");
}
void cmd_WATERLEVEL(SerialCommands* sender)
{
   // int brightness = map(analogRead(waterSensorPin), 250, 400, 0, 4);
    sender->GetSerial()->print("WATER[");
    Serial.print(analogRead(waterSensorPin));
     Serial.println("]");

}

void cmd_MOTOR(SerialCommands* sender)
{
 	char* pwm_str = sender->Next();
	if (pwm_str == NULL)
	{
		sender->GetSerial()->println("ERROR NO_PWM");
		return;
	}
 
 	pwm = atoi(pwm_str);
  myservo.write(pwm);
    delay(15);    
    
  sender->GetSerial()->println("OK!");   
}
void cmd_MOTORINFO(SerialCommands* sender)
{
    sender->GetSerial()->print("MOTOR[");
    Serial.print(pwm);
     Serial.println("]");  
}

void cmd_VERSION(SerialCommands* sender)
{
  sender->GetSerial()->println("Water Control Device Version 1.0.0");
}


SerialCommand cmd_SYNC_("SYNC", cmd_SYNC);
SerialCommand cmd_WATERLEVEL_("WATERLEVEL", cmd_WATERLEVEL);
SerialCommand cmd_MOTOR_("MOTOR", cmd_MOTOR);
SerialCommand cmd_MOTORINFO_("MOTORINFO", cmd_MOTORINFO);
SerialCommand cmd_VERSION_("VERSION", cmd_VERSION);


void setup()
{
  Serial.begin(115200);
  myservo.attach(9);  // attaches the servo on pin 9 to the servo object
  serial_commands_.SetDefaultHandler(cmd_unrecognized);
  serial_commands_.AddCommand(&cmd_SYNC_);
  serial_commands_.AddCommand(&cmd_WATERLEVEL_);
  serial_commands_.AddCommand(&cmd_MOTOR_);
   serial_commands_.AddCommand(&cmd_MOTORINFO_);
  serial_commands_.AddCommand(&cmd_VERSION_);

}

void loop()
{
  serial_commands_.ReadSerial();
}