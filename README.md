# AndroidScreenShot_SysApi
这是一个例子，以非常优雅的方案实现屏幕截图。 实现原理为 利用android 5.0 之后的录屏API获取一帧画面，来实现截屏。

## Special Features

 1. 打破老旧的截屏方案，不能截状态栏的问题。

 2. 截图其他app.

## Usage

**使用Shotter**

    请查阅代码 onClickReqPermission()

**使用ScreenShotActivity**

    请查阅代码 onClickShot(),该方法可以截图其他app

    
### 更多拓展：
   
   > 因为ScreenShotActivity是一个透明并隐藏的activity，玩法有很多:
    
   a. 截图桌面;
   
   b. 对其他app进行截图:你自己试着调整shotter的delay时间为3秒，然后start截图，再切换到其他app里，等toast截图成功。
