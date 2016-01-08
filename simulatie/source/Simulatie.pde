import processing.net.*; 
import processing.serial.*;

Server myServer;
Serial robotSerial;
Robot robot;

PGraphics icon;
TextField xfield;
TextField yfield;
TextField zfield;
Button sendButton;

float rotX, rotY;
float posX=1, posY=50, posZ=50;
float speed = 0.05;
float scale = 0.75;

int xball = 0,yball = 250,zball = 0;

String lastText = "";
String buffer = "";

int checkConnection = 0;

void setup() {
  //fullScreen(P3D);
  size(1200, 800, OPENGL);
  surface.setTitle("Simulation - By: Ietstechnisch1");
  surface.setResizable(true);
  
  robot = new Robot();
  sphereDetail(10);
  xfield = new TextField(10,160 , "x");
  yfield = new TextField(10,120 , "y");
  zfield = new TextField(10,80 , "z");
  sendButton = new Button(10,40, "Send");
  robot.setRotation(2,radians(-30));
  //textMode(MODEL);
  
  println("COM ports: ");
  printArray(Serial.list());
  if(Serial.list().length > 0){
    robotSerial = new Serial(this, Serial.list()[0], 19200, 'E', 8, 2.0);
    //robotSerial = new Serial(this, Serial.list()[0], 9600);
    lastText = "COM connected";
  }
}

void draw() {
  background(32);
  
  //----------DRAW 3D WORLD----------
  //Rotate/translate/scale/light world
  pushMatrix();
  translate(width/2,height/2);
  rotateX(rotX);
  rotateY(-rotY);
  scale(scale);
  smooth();
  lights();
  
  //Daw ball.
  pushMatrix();
  translate(-xball,-yball,zball);
  fill(color(255,0,0));
  sphere(20);
  fill(0);
  popMatrix();
  
  //Draw environment.
  directionalLight(51, 102, 126, -1, 0, 0);
  drawTable(0, 220, 0, 200, 10, 200);
  robot.update();
  noStroke();
  drawOrigin();
  popMatrix();
  
  //----------DRAW HUD (2D)----------
  noLights();
  hint(DISABLE_DEPTH_TEST);
  xball = xfield.update();
  yball = yfield.update();
  zball = zfield.update();
  if(sendButton.update() && myServer.active()){
    myServer.write(xball + ";" + yball + ";" + zball + ";\r");
    println("Sent");
  }
  printText(lastText);
  hint(ENABLE_DEPTH_TEST);
  
  //Others.
  handleConnection();
}

void drawOrigin(){
//Draw origin (R/G/B xyz lines)
   strokeWeight(2);
   textSize(24);
   stroke(255,0,0);
   text("+X",-200,0);
   line(0,0,0,-200,0,0);
   stroke(0,255,0);
   text("+Z",0,0,200);
   line(0,0,0,0,-200,0);
   stroke(0,0,255);
   line(0,0,0,0,0,200);
   text("+Y",0,-200,0);
   textSize(32);
   noStroke();
}

void printText(String text){
  printText(text,32,64);
}
void printText(String text, int x, int y){
  fill(255);
  textSize(32);
  text(text,x,y,0);
}

void mouseDragged() {
  rotY -= (mouseX - pmouseX) * 0.01;
  rotX -= (mouseY - pmouseY) * 0.01;
}

void mouseWheel(MouseEvent event) {
  float count = event.getCount();
  scale -= count/10;
  if(scale <0.1){
   scale = 0.1; 
  } else if(scale>3)
  {
    scale=3;
  }
}

void keyPressed() {
}

void handleConnection() {
  checkConnection--;
  if (myServer == null || (!myServer.active() && checkConnection <= 0)) {
    myServer = new Server(this, 63258);
    checkConnection = round(frameRate*10);
    println("Attempting to start server.");
  } else if (myServer != null) {
    //println("Server ready");
    Client thisClient = myServer.available();
    if (thisClient != null) {
      String bufferFromClient = thisClient.readString();
      if (bufferFromClient != null) {
        println("Received: \"" + bufferFromClient + "\" from: " + thisClient.ip());
          buffer += bufferFromClient;
          
          if(buffer.indexOf("\r") != -1){
             handleMessage(buffer);
             buffer = "";
          }
      }else{
        println("Nothing received from " + thisClient.ip());
      }
    }else{
      //println("Client == null");
    }
  }
}

void drawTable(int x, int y, int z, int w, int h, int b) {
  pushMatrix();
  //translate(width/2, height/2);
  //rotateX(rotX);
  //rotateY(-rotY);
  translate(x, y, z);

  scale(-4);
  fill(180);
  box(w, h, b);//floor
  popMatrix();
}

void handleMessage(String buffer) {
  if(robotSerial != null){
    robotSerial.write(buffer);
  }
    lastText = "";
  println("handleMessage: " + buffer);
  int startIdx = buffer.indexOf("PRN");
  int endIdx = buffer.indexOf(",");
  int type = 0;
  int[] rotations = new int[6];
  if(endIdx == -1 || startIdx == -1){
    println("Couldn't parse string");
   return; 
  }
  type = Integer.parseInt(buffer.substring(startIdx+4, endIdx));
  println("Type: "+ type);
  startIdx = buffer.indexOf("(", endIdx);
  endIdx = buffer.indexOf(",", startIdx);
  println("Found ( at: " + startIdx + " and , at: " + endIdx);

  for (int i = 0; i < 5; i++) {
    if(endIdx == -1 || startIdx == -1){
      println("Couldn't parse string");
     return; 
    }
    rotations[i] = Integer.parseInt(buffer.substring(startIdx+1, endIdx));
    println("Rotation[" + i + "]:" + rotations[i]);
    startIdx = buffer.indexOf(",", startIdx+1);
    endIdx = buffer.indexOf(",", startIdx+1);
    println("Found , at: " + startIdx + " and next , at: " + endIdx);
  }
  endIdx = buffer.indexOf(")", startIdx+1);
  println("Found , at: " + startIdx + " and last ) at: " + endIdx);
  if(endIdx == -1 || startIdx == -1){
    println("Couldn't parse string");
   return; 
  }
  rotations[5] = Integer.parseInt(buffer.substring(startIdx+1, endIdx));
  println("Rotation[" + 5 + "]:" + rotations[5]);
  
  int j = 1;//TODO fixme
  for (int i = 0; i<rotations.length; i++) {
    if(type==1 || type == 2 && rotations[i] != 0){
      if(i != 3){
        println("Set: " + j + " to " + rotations[i]);
        if(type == 1)
        robot.setRotation(j,radians(rotations[i]));
        else
        robot.setRotation(j,radians(rotations[j]));
        j++;
      }
    }
    
    /*if ( type == 1 || ( type == 2 && rotations[i] != 0) ){
      if(i == 3){
        i+=1;
      }
      if(i<3)
      robot.setRotation(i+1, radians(rotations[i]));
      else if(i>3)
      robot.setRotation(i,radians(rotations[i+1]));
    }*/
      //Ba = 0
      //J1 = 1
      //J2 = 2
      //J3 = 3
      //J4 = x
      //J5 = 5
      //J6 = 6
  }
}

void serialEvent(Serial p) { 
  print(p.readString()); 
} 