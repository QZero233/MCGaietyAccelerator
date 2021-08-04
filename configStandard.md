# 配置文件及其规范

## 必需文件列表

- 默认服务端（文件名必须为server.jar）
- EULA协议认同书：eula.txt（必须已改为true，否则验证不通过）
- 默认MC服务器配置文件：server.properties（里面是之后所有服务端的默认配置）
- MCGA配置文件：mcga.config
- minecraft服务器运行环境配置文件：env.config

一个可供参考的服务器文件结构如下

|----MinecraftGaietyAccelerator.jar

|----server.properties

|----mcga.config

|----eula.txt

|----env.config

|----game_server

​			|----serverConfig.config

|----authorize

​			|----XXXX.config

|----plugins

​			|----mapBackup.xml

​			|----autoLoadList.txt

## SC规范

配置文件大多采用**标准配置文件格式（以下简称SC）**

即 key=value（**严格区分大小写**）

每一个配置项占一行

比如

```SC
commandFilePath=/bin/bash
javaPath=jdk/bin/java
javaParameter=-Xmx2000M
```

## env.config

用途：这是配置minecraft服务器运行环境的配置文件

位置：该文件位于根目录

格式：格式为SC

需要有以下内容

| 名称           | Key             | 解释                                                     |
| -------------- | --------------- | -------------------------------------------------------- |
| 控制台终端位置 | commandFilePath | 如linux下的/bin/bash或windows下的cmd.exe                 |
| Java的位置     | javaPath        | 即java虚拟机的位置，如jdk/bin/java                       |
| Java参数       | javaParameter   | java虚拟机运行参数，如 -Xmx2000M**（不加-jar xxx.jar）** |

实例：

```SC
commandFilePath=/bin/bash
javaPath=jdk/bin/java
javaParameter=-Xmx2000M
```

## serverConfig.config

用途：配置服务器相关信息

位置：这个文件在以服务器名为文件夹名的目录下（如服务器名为survivalServer，则该文件位于 survivalServer/serverConfig.config）

格式：格式为SC

可以有以下内容（全为可选配置项）

| 名称                   | Key               | 解释                                                         |
| ---------------------- | ----------------- | ------------------------------------------------------------ |
| 服务端jar文件名        | serverJarFileName | 如果此值有，就不采用默认的（自动配置时默认先会在主目录找与此文件同名的服务端jar，如果没有，就复制默认的server.jar并改名） |
| Java的位置             | javaPath          | 如果此值有，就不采用默认的                                   |
| Java参数               | javaParameter     | 如果此值有，就不采用默认的                                   |
| 自动配置需要复制的文件 | autoConfigCopy    | 用逗号隔开，如server.jar,a.jar（可以是文件夹）               |

**同时，也可有其它内容**

**如果有，则会写入服务器的server.properties代替原有的**

## mcga.config

用途：配置MCGA运行的相关信息

位置：根目录

格式：SC

可以有以下内容

| 名称            | Key           | 解释                              |
| --------------- | ------------- | --------------------------------- |
| Container的名称 | containerName | 如果为空则为默认的commonContainer |

这里面还可以添加其他内容

其他的内容将作为自定义container的参数

关于Container:MCGA允许插件添加自定义的container，container是装载minectaft服务器运行进程的容器，插件开发者可宏观地通过监听各个服务器的信息来调控服务器的运行（比如同时只允许一个服务器运行）

## 管理员身份配置文件

用途：配置管理员的ID，密码，权限等

位置：此文件的文件名为 <管理员的mcID>.config，位于 authorize/ 目录下**（这里的管理员指可在游戏内执行MCGA指令的玩家，而非游戏中的OP）**

格式：格式为SC

需要以下内容

| 名称               | Key          | 解释                                                         |
| ------------------ | ------------ | ------------------------------------------------------------ |
| 管理员密码的哈希值 | passwordHash | 使用sha256，16进制全大写编码，例如8D:96:9E:EF:6E:CA:D3:C2:9A:3A:62:92:80:E6:86:CF:0C:3F:5D:5A:86:AF:F3:CA:12:02:0C:92:3A:DC:6C:92 |
| 管理员等级         | adminLevel   | 只能为1或2，具体规则见下方注释①                              |

关于管理员等级

管理员等级1：1等级的可进行修改服务器等操作

管理员等级2：只有2等级的才可以添加管理员和执行服务器linux指令