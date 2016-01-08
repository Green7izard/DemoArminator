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
  int update(){
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