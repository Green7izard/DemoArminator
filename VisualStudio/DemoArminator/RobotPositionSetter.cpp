#pragma once
#include "stdafx.h"

#include "RobotControl.hpp"
#include "Vector.hpp"

#define _USE_MATH_DEFINES
#include <math.h>



//Lengths of the arms parts (From corner to corner)
#define J2_J3 = 250
#define J3_j5 = 160
#define J5_j6 = 72

//Robot Limits in degrees
#define J1_MIN = -150
#define J1_MAX = 150
#define J2_MIN = -60
#define J2_MAX = 120
#define J3_MIN = -110
#define J3_MAX = 120
#define J5_MIN = -90
#define J5_MAX = 90
#define J6_MIN = -200
#define J6_MAX = 200

using namespace std;
using namespace Robot;
using namespace RobotArminator;

class RobotPositionSetter
{
public:
    //ROBOT LIMITS:
    // J1 Lower rotator : -150  +150
    // j2 Lower elbow   : -60   +120
    // j3 Middle elbow  : -110  +120
    // j4: DOES NOT EXIST
    // j5 end elbow     : -90   +90
    // j6 head rotator  : -200  +200

    

    //TODO THIS FUNCTION
    void handleCommand(Vector command, IRobotControl &robotControl)
    {

        //The content of this function can be changed to stay in here, endless loop, or run predefined programs!
    }















    void send(IRobotControl &robotControl, int j1 = 0, int j2 = 0, int j3 = 0, int j5 = 0, int j6 = 0)
    {
        robotControl.writeData("PRN 1,(" + to_string(j1) + "," + to_string(j2) + "," + to_string(j3) + ",0," + to_string(j5) + "," + to_string(j6) + ")\r");
    }



};
