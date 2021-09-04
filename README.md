# MinecraftGaietyAccelerator
## 简介

**MinecraftGaietyAccelerator（简称MCGA）**是一个minecraft服务器管理器，可以很方便地在

- 游戏内
- 控制台
- 其他任何联网终端

管理正在运行的minecraft服务器

## 用户群体

MCGA主要是针对面板服用户设计的

MCGA允许用户使用自己上传的java来运行minecraft服务器，这样可以解决面板服jdk版本不兼容的问题（通常都还在用openjdk 1.8版本）

同时还可以方便地实现服务器的切换，可以在一个面板服上存储多个服务端，使用指令来开启或关闭某些个服务端

比如可以同时在服务器上放一个生存服和一个小游戏服，平时运行生存服，等有需要时就关闭生存服启动小游戏服，这个操作可以通过一个指令轻松实现（甚至可以在游戏内通过聊天栏输入指令实现切换）

## 特性

详细说明请见 [userGuide.md](userGuide.md)，这里只简单列举一下

- 游戏内指令

比如用`#stop_and_switch_and_run c `来停止当前运行的服务器同时启动名称为 c 的服务器，等服务器启动后再进入即可

全程不用进入面板服控制端操作

- 自动配置

可以用`auto_config`指令来自动配置服务端

具体功能见[userGuide.md](userGuide.md)

- 插件系统

允许用户使用java编写jar插件，直接调用MCGA程序，来实现功能的扩展

也可用xml编写简单的xml插件，具体请见[pluginTips.md](pluginTips.md)

- 单端口模式（现在已独立成一个插件）

单端口模式运行，适用于只开放了一个端口的服务器（比如网上租赁的mc服务器）
只能同时运行一个mc服务器
运行mc服务器时远程管理服务端会自动关闭，当mc服务器关闭时会自动启动

- 联机神器Tunnel（现在已独立成一个插件）

可以把面板服变成一个跳板，实现2个客户端的直接联机

具体功能见[userGuide.md](userGuide.md)

- 本地控制台模式

这主要是面向面板服用户设计的，进入该模式后，可执行所属系统控制台指令（比如对windows就可以当cmd来用）

可用来调试服务器

具体功能见[userGuide.md](userGuide.md)

- 自动执行

在根目录下新建一个名为auto_exec.txt

在里面输入需要启动时就自动执行的指令，一行一条

比如，如果需要开启就自动运行服务器s，就在里面输入

```
select s
start_server
```

**（注：这里的指令并不支持listen on,listen off,exit,stop_program等本地控制台专用指令）**

## 如何使用

1. 新建一个文件夹作为MCGA的工作目录，并将下载或编译得到的MCGA的jar包放入该文件夹中**（以下就称该目录为根目录了，如mcga.jar放在/home/mc/server/mcga.jar中，则根目录就是指 /home/mc/server）**
2. 在根目录中准备配置文件，服务端文件以及其他必需文件（具体配置文件规范请见[configStandard.md](configStandard.md)）
3. 手动新建一个服务器（如果要使用指令新建就跳过这一步），具体操作为，为服务器取个名字**（全英文字母）**，在根目录新建一个同名的文件夹，在里面新建一个serverConfig.config（具体配置文件规范请见[configStandard.md](configStandard.md)）
4. 用java启动mcga
5. 如果需要查看服务端输出的，用`listen on`打开监听模式
6. 用`auto_config`指令来自动配置服务器
7. 用`select`指令选择服务器
8. 用`start_server`启动服务器