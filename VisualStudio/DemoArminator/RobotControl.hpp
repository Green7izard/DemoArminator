#pragma once

#include "IRobotControl.hpp"
#include "TCPRobot.hpp"

#include <functional>
#include <iostream>
#include "Vector.hpp"
#include "stdafx.h"


using namespace Robot;

class RobotControl : public IRobotControl
{
public:
    RobotControl(TCPRobot * robot);
    ~RobotControl();
    virtual void resetPositions() override;
    virtual void writeData(std::string aData) override;
    virtual std::string readData() override;
private:

    TCPRobot * robot;
    double getRadian(double aDegree);
};



