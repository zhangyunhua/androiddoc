sendevent需要4个参数即：device，type，code，value。这些值可以由input子系统定义，也可以从getevent里面获取。type其实就是和getevent中的支持事件类型所对应的，type, code, value的定义可参看kernel/include/linux/input.h

sendevent /dev/input/event0 1 158 1  
sendevent /dev/input/event0 1 158 0  


device需要是支持该按键的设备这里是sec_touchscreen；type为1表示是按键事件；value为1表示按下，为0表示弹起，一次按键事件由按下和弹起两个操作组成。

