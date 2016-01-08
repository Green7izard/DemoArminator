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
  void update() { 
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
  void setRotation(int partNumber, float rotation){

      parts.get(partNumber).rotation = rotation;

  }
  void setRotationDirect(int partNumber, float rotation){

      parts.get(partNumber).rotation = rotation;
      parts.get(partNumber).currentRotation = rotation;
    
  }
  float getCurrentRotation(int partNumber){
    return parts.get(partNumber).rotation;
  }
  float getRotation(int partNumber){
   return parts.get(partNumber).rotation;
  }
} 