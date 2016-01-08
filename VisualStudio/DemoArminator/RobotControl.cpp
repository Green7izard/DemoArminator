#include "stdafx.h"
#include "RobotControl.hpp"

/*** Public functions ***/
RobotControl::RobotControl(TCPRobot * robot) : robot(robot)
{

}

RobotControl::~RobotControl()
{
}


void RobotControl::resetPositions()
{
    writeData("PRN 1,(0,0,0,0,0,0)\r");
}



void RobotControl::writeData(std::string aData)
{
    robot->sendMessage(aData);
}

std::string RobotControl::readData()
{
    return robot->readMessage();
}

double RobotControl::getRadian(double aDegree)
{
    return aDegree * (3.14159265 / 180);
}

