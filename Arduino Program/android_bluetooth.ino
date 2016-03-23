#include <SoftwareSerial.h>

int bTX = 2;
int bRX = 3;
char incomingByte = '0';

SoftwareSerial bluetooth(bTX,bRX);

void setup() {
  // start serial connection to pc
  Serial.begin(9600);
  Serial.print("SETUP");
  //start bluetooth
  bluetooth.begin(115200);
  delay(100);
  bluetooth.begin(9600);

  pinMode(4,OUTPUT);  //set pin 4 for output
  pinMode(5,OUTPUT);  //set pin 5 for output
  
}

void loop() {
  //Reads user input if bluetooth application is turned on
  if(bluetooth.available() > 0) {
    incomingByte = (char)bluetooth.read();

    if(incomingByte == '0'){
      Serial.println("Off");
      digitalWrite(4,LOW);    //Sets LED OFF
      digitalWrite(5,HIGH);
    }
    else if(incomingByte == '1'){
      Serial.println("Low");
      digitalWrite(4,HIGH);   //Sets LED LOW
      digitalWrite(5,HIGH);
    }
    else if(incomingByte == '2'){
      Serial.println("High");
      digitalWrite(4,HIGH);   //Sets LED HIGH
      digitalWrite(5,LOW);
    }
  }

}
