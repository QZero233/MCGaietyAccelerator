# 服务器插件系统使用指南

## 用户篇

### 1.插件简介

**插件（Plugin）** 是 **MinecraftGaietyAccelerator（以下简称MCGA）** 的一个高级功能，它允许其它开发者给其添加新功能

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

### 1.xml插件开发

**（注意：xml插件每次执行时都会被重新分配一个上下文，也就是说，如果指令需要选择服务器，就需要先把选择服务器指令写了再执行这个指令）**

xml插件是一种简单便于编写的插件

#### 插件模板

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<plugin>
    <commands>
        <command name="do_it" parameterCount="1">
            <execute>execute /say $1$</execute>
        </command>
    </commands>

    <listeners>
        <listener type="playerEvent">
            <execute>execute /say $1$ again</execute>
        </listener>
    </listeners>
</plugin>
```

#### 添加新指令

在commands标签下如模板新建一个command标签

其中

name是指令名称，parameterCount是参数数量

在下方添加的execute标签 中就依次写需要执行的指令（这里的指令是指MCGA内部指令，如果需要执行minecraft指令需要用execute指令并指明服务器）

需要引用第n个参数就用\$n​\$代替



例如：一个跨服通信的例子

添加指令为 `announce_to <服务器名称> <消息内容>`

```xml
<command name="announce_to" parameterCount="2" needServerSelected="false">
    <execute>select $1$</execute>
	<execute>execute /say $2$</execute>
</command>
```

这样的话，在服务器A中，只要玩家输入`#announce_to B Hello`

那么在B服务器中就会收到一个消息Hello

#### 添加新监听器

在listeners标签下如模板新建一个listener标签

execute的部分同上文

listener标签中，type是监听器类型

监听器的类型有

- output 输出监听器
- serverEvent 服务器事件监听器
- playerEvent 玩家事件监听器

监听器对应的参数的顺序如下

##### 输出监听器（output）

| 参数顺序 | 参数名称   | 参数描述                      |
| -------- | ---------- | ----------------------------- |
| 1        | serverName | 输出发生的服务器名称          |
| 2        | outputLine | 输出的内容                    |
| 3        | outputType | 输出的类型（normal或者error） |

##### 服务器事件监听器（serverEvent）

| 参数顺序 | 参数名称    | 参数描述                                         |
| -------- | ----------- | ------------------------------------------------ |
| 1        | serverName  | 输出发生的服务器名称                             |
| 2        | serverEvent | 服务器事件类型（starting或者started或者stopped） |

##### 玩家事件监听器（playerEvent）

| 参数顺序 | 参数名称    | 参数描述                  |
| -------- | ----------- | ------------------------- |
| 1        | serverName  | 输出发生的服务器名称      |
| 2        | playerName  | 发生该玩家事件的玩家名称  |
| 3        | playerEvent | 玩家事件（join或者leave） |

例如：一个玩家进入或离开就备份存档的例子

（假设备份存档的指令为backup）

```xml
<listener type="playerEvent">
    <execute>select $1$</execute>
    <execute>backup</execute>
    <execute>execute /say "Map has been backed up due to $2$'s $3$"</execute>
</listener>
```

