# 服务器插件系统使用指南

## 用户篇

### 1.插件简介

**插件（Plugin）**是**MinecraftGaietyAccelerator（以下简称MCGA）**的一个高级功能，它允许其它开发者给其添加新功能

### 2.插件功能

- 添加新的指令

插件可以添加新的指令（就是类似于update_server_config的指令），实现更多有趣的附加功能

- 添加新的监听器

插件可以允许开发者使用MCGA的监听器，监听游戏事件（比如玩家进入），从而进行相应反应

例如：可以监听玩家进入事件，当指定玩家进入事件后，自动备份存档

### 3.插件名称

插件名称即插件的文件名**（不含后缀名）**，

如backupMap.jar，插件名称就是backupMap

autoBackup.xml，插件名就是autoBackup

### 4.插件类型

插件类型暂时有

- jar包型
- xml文件型

### 5.插件的使用

把插件放在主程序jar包同级下的plugins文件夹中

在控制台中使用load_plugin指令即可

不需要时只需使用unload_plugin指令卸载插件

**（注意：插件默认不会自动加载，需要每次手动加载）**

### 6.自动加载插件

在plugins文件夹中新建一个autoLoadList.txt文件

再里面写入需要每次启动自动加载的插件名即可（用**英文**逗号隔开）

如 backupMap,autoBackup

## 开发者篇