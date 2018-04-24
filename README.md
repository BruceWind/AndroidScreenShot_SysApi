# AndroidScreenShot_SysApi
非常优雅的方案实现屏幕截图，利用android 5.0 之后的录屏API获取一帧画面，来实现截屏。

## 特性 

 1. 方便后台service调用截屏功能。
    
    
 2. 打破老旧的截屏方案，只能截当前activity不能截状态栏的问题。
 
## 使用

1. 直接编译代码run，等待toast成功.

    注意：目前代码截图成功后保存到使用外置私有存储，不需要申请外部存储权限，所以不要惊讶，不会崩溃的。
    
2. 自定义使用
   
    直接使用Shotter，或者直接使用ScreenShotActivity。
    
3. 更多拓展：
   
   > 因为他是一个透明并隐藏的activity，玩法有很多:
    
   3.1截图桌面;
   
   3.2对其他app进行截图:你自己试着调整shotter的delay时间为3秒，然后start截图，再切换到其他app里，等toast截图成功。
   
   3.3从service里启动:你要改manifaest，配置action，从service里启动试试，这个我没有玩过。