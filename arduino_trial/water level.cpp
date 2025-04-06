#include <Servo.h>
#include <Arduino.h>
#include "SerialCommands.h"

const int waterSensorPin = A0; // Connect the sensor signal to A0


Servo myservo;  // create servo object to control a servo
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
    int brightness = map(analogRead(waterSensorPin), 250, 400, 0, 4);
    sender->GetSerial()->print("WATER");
 Serial.println(brightness);
  /*sender->GetSerial()->print("WATER");
  int waterLevel = analogRead(waterSensorPin); // Range: 0 - 1023
  Serial.print("Water Level: ");
  Serial.println(waterLevel);
  int brightness = map(waterLevel, 250, 400, 0, 4);
  Serial.print("   Water brightness: ");
  Serial.println(brightness);
  delay(1000); // Wait half a second*/

}

void cmd_MOTOR(SerialCommands* sender)
{
  sender->GetSerial()->println("MOTOR");
  myservo.write(50);
}

void cmd_VERSION(SerialCommands* sender)
{
  sender->GetSerial()->println("Water Control Device Version 1.0.0");
}

//
//
////First parameter pwm value is required
////Optional parameters: led colors
////e.g. LED 128 red
////     LED 128 red blue
////     LED 0 red blue green
//void cmd_rgb_led(SerialCommands* sender)
//{
//	//Note: Every call to Next moves the pointer to next parameter
//
//	char* pwm_str = sender->Next();
//	if (pwm_str == NULL)
//	{
//		sender->GetSerial()->println("ERROR NO_PWM");
//		return;
//	}
//	int pwm = atoi(pwm_str);
//
//	char* led_str;
//	while ((led_str = sender->Next()) != NULL)
//	{
//		if (set_led(led_str, pwm))
//		{
//			sender->GetSerial()->print("LED_STATUS ");
//			sender->GetSerial()->print(led_str);
//			sender->GetSerial()->print(" ");
//			sender->GetSerial()->println(pwm);
//		}
//		else
//		{
//			sender->GetSerial()->print("ERROR ");
//			sender->GetSerial()->println(led_str);
//		}
//	}
//}
//

SerialCommand cmd_SYNC_("SYNC", cmd_SYNC);
SerialCommand cmd_WATERLEVEL_("WATERLEVEL", cmd_WATERLEVEL);
SerialCommand cmd_MOTOR_("MOTOR", cmd_MOTOR);
SerialCommand cmd_VERSION_("VERSION", cmd_VERSION);


void setup()
{
  Serial.begin(115200);
  myservo.attach(9);  // attaches the servo on pin 9 to the servo object
  serial_commands_.SetDefaultHandler(cmd_unrecognized);
  serial_commands_.AddCommand(&cmd_SYNC_);
  serial_commands_.AddCommand(&cmd_WATERLEVEL_);
  serial_commands_.AddCommand(&cmd_MOTOR_);
  serial_commands_.AddCommand(&cmd_VERSION_);

}

void loop()
{
  serial_commands_.ReadSerial();
}