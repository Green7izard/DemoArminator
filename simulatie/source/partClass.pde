class Part { 
  float partLength = -10;
  PShape shape;
  float rotation;
  boolean invertRotation;
  float currentRotation;
  float minRotation, maxRotation;
  int rotationAxis;
  float speed;
  color colour;
  String partName;

  Part (String partName, int rotationaxis, float minRotation, float maxRotation, float Length, color drawColor) {  
    shape = loadShape(partName);
    this.partName = partName;
    partLength = Length;
    rotationAxis = rotationaxis;
    this.minRotation = minRotation;
    this.maxRotation = maxRotation;
    speed = 0.05;
    colour = drawColor;
    shape.disableStyle();
  } 
  void update() {
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