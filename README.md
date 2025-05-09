# SCREENCAST TO CIVIC METER
## 将中控画面投放到液晶仪表盘

前置条件：  
1、你的车机已经ROOT  
2、你已经知道怎样给自己的车机安装APK  
3、建议安装Carlife  
4、依据你使用的投屏软件来调整录制屏幕的范围（我使用VIVO 的车机互联）  

## 开发思路
开启车机开发者模式，开启 显示surface 更新，可快速定位仪表中哪些画面是由Android 车机进行控制的，经过验证，只有手机画面和媒体播放画面是属于android车机进行控制的  
见image文件夹中的mp4文件  
## 目前实现的功能
1、接通车辆电源后，车机释放广播 com.mitsubishielectric.ada.appservice.powermanager.ACTION_HMI_SERVICE_START，MyBroadcastReceiver.java在接收到广播后启动三个关键服务：  
### 1、仪表投屏服务  
### 2、蓝牙链接监听服务
### 3、方向盘按键监听  
2、将仪表切换至媒体播放界面或者电话界面时，仪表会启用中控屏的画面显示，值得注意的是，暂时没打算研究系统服务 ExternalDisplacedlayApService.apk是如何指导仪表显示的，直接偷了个懒使用System.TYPE.ERROR来覆盖系统在仪表的显示模式System.TYPE.ALERT（正因为系统使用的是ALERT，所以常规API显示的内容无法在仪表显示，因为都被ALERT级别挡住了，那么在破坏原系统的同时投屏显示自己的内容只有使用比ALERT更高的级别ERROR—），此时，自定义图像可以正常显示到仪表，且覆盖系统的内容。  

3、蓝牙监听服务是用来监听指定蓝牙手机连接后，自动开启Carlife，以及指导车机连接指定手机的WIFI AP，蓝牙名称和WIFI连接映射关系由BluetoothApMapSettingActivity.java页面管理，主要是因为目标手机通过蓝牙连接上车机，但车机会寻找最优的WIFI AP连接，这个WIFI AP可能并非这台目标手机，导致真正想要连接的手机不能连接Carlife。  

4、方向盘按键监控则是绑定系统服务 Strgsw服务，这是系统提供的AIDL接口，通过方向盘按键控制仪表显示投屏的开关以及投屏内容。
