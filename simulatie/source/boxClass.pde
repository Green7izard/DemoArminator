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
  boolean update(){
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