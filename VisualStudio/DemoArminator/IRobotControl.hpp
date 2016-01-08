#pragma once

#include "stdafx.h"
#include <iostream>

namespace Robot
{
    class IRobotControl
    {
    public:
        virtual ~IRobotControl() = 0;
        virtual void resetPositions() = 0;
        virtual void writeData(std::string aData) = 0;
        virtual std::string readData() = 0;
    protected:
        IRobotControl();
    };
    inline IRobotControl::IRobotControl() {};
    inline IRobotControl::~IRobotControl() {};
}