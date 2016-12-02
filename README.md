# AndroidScreenShot_SysApi
非常优雅的方案实现屏幕截图，利用android 5.0 之后的录屏API获取一帧画面，来实现截屏。

## 特性 

 1. 方便后台service调用截屏功能。
    
    
 2. 打破老旧的截屏方案，只能截当前activity不能截状态栏的问题。
 
## 使用

1. 直接后台service启动ScreenShotActivity

> 这个activity是透明的，而且不会引起当前activity onPuase，
ScreenShotActivity并自动实现弹出用户提示并截屏保存到外置存储空间下

    注意：这个app使用外部存储并没有在mainfast中生明权限也没有在activity中动态申请。
    
2. 自定义使用

    直接使用Shotter，使用方法参考ScreenShotActivity。