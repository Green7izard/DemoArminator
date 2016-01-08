import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Simulatie extends PApplet {

 


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
float speed = 0.05f;
float scale = 0.75f;

int xball = 0,yball = 250,zball = 0;

String lastText = "";
String buffer = "";

int checkConnection = 0;

public void setup() {
  //fullScreen(P3D);
  
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
    robotSerial = new Serial(this, Serial.list()[0], 19200, 'E', 8, 2.0f);
    //robotSerial = new Serial(this, Serial.list()[0], 9600);
    lastText = "COM connected";
  }
}

public void draw() {
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

public void drawOrigin(){
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

public void printText(String text){
  printText(text,32,64);
}
public void printText(String text, int x, int y){
  fill(255);
  textSize(32);
  text(text,x,y,0);
}

public void mouseDragged() {
  rotY -= (mouseX - pmouseX) * 0.01f;
  rotX -= (mouseY - pmouseY) * 0.01f;
}

public void mouseWheel(MouseEvent event) {
  float count = event.getCount();
  scale -= count/10;
  if(scale <0.1f){
   scale = 0.1f; 
  } else if(scale>3)
  {
    scale=3;
  }
}

public void keyPressed() {
}

public void handleConnection() {
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

public void drawTable(int x, int y, int z, int w, int h, int b) {
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

public void handleMessage(String buffer) {
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

public void serialEvent(Serial p) { 
  print(p.readString()); 
} 
class TextField{
  int x,y, xsize,ysize;
  String text = "";
  int value = 0;
  
  boolean edit = false;
  
  TextField(int x, int y, String text){
    this.x=x;
    this.y=y;
    xsize = 60;
    ysize = 20;
    this.text=text;
  }
  public int update(){
    if(mouseX>x&&mouseX<x+xsize&&mouseY>height-y&&mouseY<height-y+ysize&&mouseButton==LEFT&&mousePressed){
      mousePressed = false;
      if(!edit)
      value = 0;
      edit = !edit;
    }else if(mousePressed == true || (keyPressed && key == 10)){
      edit = false;
    }
    
    if(keyPressed && edit && key >= '0'  && key <='9'){
      value *= 10;
      int keyVal= key-'0';
      if(value<0)
      {
        value-=keyVal;
      }
      else
    {
      value+=keyVal;
    }
      key = 0;
    }
    if(keyPressed && edit && key== '-'){
      value *= -1;
      key = 0;
    }
    if(keyPressed && edit && key == 8)
    {
      value = round( value /10 );
      key = 0;
    }
    if(edit){
      fill(255);
    }else{
      fill(100);
    }
    
    
    rect(x,height-y,xsize,ysize);
    
    if(edit)
    fill(0);
    else
    fill(255);
    textSize(10);
    text(text + ": " + value,x+2,height-y+15);
    
    return value;
  }
}
class Button{
  int x,y, xsize,ysize;
  String text = "";
  Button(int x, int y, String text){
    this.x=x;
    this.y=y;
    xsize = 60;
    ysize = 20;
    this.text=text;
  }
  public boolean update(){
    boolean returnValue = false;
    if(mouseX>x&&mouseX<x+xsize&&mouseY>height-y&&mouseY<height-y+ysize&&mouseButton==LEFT&&mousePressed){
      returnValue = true;
      mousePressed = false;
    }else{
      returnValue = false;
    }
    if(returnValue){
      fill(255);
    }else{
      fill(100);
    }
    
    rect(x,height-y,xsize,ysize);
    fill(255);
    textSize(10);
    text(text,x+2,height-y+15);
    
    return returnValue;
  }
}
class Part { 
  float partLength = -10;
  PShape shape;
  float rotation;
  boolean invertRotation;
  float currentRotation;
  float minRotation, maxRotation;
  int rotationAxis;
  float speed;
  int colour;
  String partName;

  Part (String partName, int rotationaxis, float minRotation, float maxRotation, float Length, int drawColor) {  
    shape = loadShape(partName);
    this.partName = partName;
    partLength = Length;
    rotationAxis = rotationaxis;
    this.minRotation = minRotation;
    this.maxRotation = maxRotation;
    speed = 0.05f;
    colour = drawColor;
    shape.disableStyle();
  } 
  public void update() {
    constrain(currentRotation,minRotation+speed+1,maxRotation-speed-1);
    if (degrees(currentRotation) > maxRotation) {
      lastText = "ROBOT COLLISION!: exceeded maximal rotation " + partName + "(" + round(degrees(currentRotation)) + ")";
      println(lastText);
      rotation = (radians(maxRotation));
      currentRotation = rotation;
    } else if (degrees(currentRotation) < minRotation) {
      lastText = "ROBOT COLLISION!: exceeded minimal rotation " + partName + "(" + round(degrees(currentRotation)) + ")";
      println(lastText);
      rotation = (radians(minRotation));
      currentRotation = rotation;
    } else {
      if (currentRotation-speed >= rotation ) {
        currentRotation -= speed;
      } else if (currentRotation+speed <= rotation) {
        currentRotation += speed;
      }
    }
    fill(colour); 
    noStroke();
    
    if (rotationAxis==0)
      rotateX(-currentRotation);
    else if (rotationAxis==1)
      rotateY(currentRotation);
    else
      rotateZ(currentRotation);
    shape(shape);
    translate(0, partLength, 0);
  }
} 
class Robot { 
  ArrayList<Part> parts = new ArrayList<Part>();
  
  Robot () {  
    parts.add(new Part("base.obj",0,0,0,4,color(100,100,100)));
    parts.add(new Part("shoulder.obj",1,-150,150,22,color(255,255,255)));
    parts.add(new Part("upperArm.obj",0,-60,120,250/5,color(255,255,0)));
    parts.add(new Part("lowerArm.obj",0,-110,120,160/5,color(255,255,0)));
    parts.add(new Part("end.obj",0,-90,90,72/5-2,color(100,100,100)));
    parts.add(new Part("claw.obj",1,-200,200,12,color(255,255,255)));
  } 
  public void update() { 
    pushMatrix();
    //translate(width/2, height/2);
    //rotateX(rotX);
    //rotateY(-rotY);
    translate(0,25*5,0);
    scale(-5);
    for(Part part: parts){
      part.update();
    }
    //box(10);
    popMatrix();
  }
  public void setRotation(int partNumber, float rotation){

      parts.get(partNumber).rotation = rotation;

  }
  public void setRotationDirect(int partNumber, float rotation){

      parts.get(partNumber).rotation = rotation;
      parts.get(partNumber).currentRotation = rotation;
    
  }
  public float getCurrentRotation(int partNumber){
    return parts.get(partNumber).rotation;
  }
  public float getRotation(int partNumber){
   return parts.get(partNumber).rotation;
  }
} 
  public void settings() {  size(1200, 800, OPENGL); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Simulatie" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
