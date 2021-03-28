# 服务端命令使用指南

## 各指令介绍

注：有些指令未提供用法，这就意味着这条指令不需要参数，直接输入即可

### 服务器管理类

#### 1.select

用法：`select <服务器名称>`

选择当前需要操作的服务器

在进行大多数操作前都需要先选择服务器

#### 2.start

启动服务器

#### 3.stop

关闭服务器

#### 4.force_stop

强制关闭服务器

通常在服务器已关闭，但是状态未更新时使用

#### 5.server_status

查看当前服务器的运行状态

#### 6.execute

用法：`execute <mc指令（带斜杠）>`

让当前服务器执行一条mc指令

#### 7.switch_and_run

用法：switch_and_run <服务器名称>

相当于

```
select <服务器名称>
start
```

#### 8.stop_and_switch_and_run

用法：`stop_and_switch_and_run <服务器名称>`

会关闭当前选择的服务器并重新选择并启动另一个服务器

这条指令可以在游戏内实现服务器切换

相当于

```
stop
select <服务器名称>
start
```

#### 9.current_server

返回当前选择的服务器

#### 10.show_all_servers

显示当前所有可选的服务器



### 环境配置类

#### 1.auto_config

用法：`auto_config <服务器名称>`

根据服务器的serverConfig.config自动配置服务器

#### 2.reload

重新加载配置文件

配置文件会在程序启动时加载到内存里

如果改动了配置文件，需要重新加载

可调用此指令

#### 3.show_server_config

输出当前服务器的配置文件中的所有配置项

#### 4.update_server_config

用法：`update_server_config <配置项名称> <配置项内容>`

该指令可以修改serverConfig.config中的配置项

但是修改后内存中的并不会更改，所以需要`reload`来重新加载配置文件

比如，我们需要把level-seed修改为2333，并把这个更新写入server.properties里面

```
update_server_config level-seed 2333
reload
auto_config <当前服务器名称>
```

#### 5.add_server

用法：`add_server <新服务器名称>`

创建一个新的服务器，并新建一个空的配置文件

创建完后若要选择，需要先`reload`



### 权限操作类

#### 1.show_all_ops

输出所有管理员的Minecraft ID

#### 2.remove_op

移除一个管理员ID

#### 3.add_op

用法：`add_op <管理员ID>`

添加一个管理员ID



## 相关特性介绍

### 1.游戏内指令

有部分玩家有权限，可以通过在游戏内聊天栏里输入指令，来执行上述指令

我们称这部分玩家为**管理员**，它们在游戏内的ID为**管理员ID**

游戏内指令的操作很简单

只需要打开聊天栏，输入#，再输入指令，发送即可

比如想在游戏内把自己（假设自己是Steve）设置为服务器的OP

就可以在聊天栏里输入

`#execute /op Steve`

注意：

1.游戏内指令默认会选择当前服务器，即相当于预先执行了`select`指令

2.一个服务器所有管理员公用一个上下文，其它管理员的行为可能导致上下文变化

比如在s1服务器中，管理员B使用了`select s2`

那么管理员A使用`current_server`时会输出s2而非s1

### 2.自动配置

自动配置的依据是服务器配置文件，即serverConfig.config

自动配置主要会做如下事情

- 自动准备服务端jar文件

  如果服务器目录中存在指定名称的jar文件，就不会复制

  如果不存在，就会在程序同级目录中寻找同名文件，如果有就复制

  如果没有就把默认服务端文件(server.jar)复制到服务器文件夹下，并改名为指定的名称

- 自动复制eula协议同意书

  **注意：eula同意书需要用户手动放置在程序同级目录下**

- 自动准备server.properties

  使用前需要把一个默认的server.properties放在程序同级目录下

  在自动配置时，serverConfig.config里的部分配置项会写入server.properties（如果原server.properties存在该配置项就覆盖掉）

- 自动复制指定的文件及文件夹到服务器目录下

### 3.目录式服务器

对于在程序同级目录下的文件夹，若这个文件夹中有serverConfig.config

则将这个文件夹认定为一个服务器的文件夹，该服务器名就是文件夹的名字

### 4.监听模式

为了美观与方便，控制台默认不输出每个mc服务器的输出内容（输出内容会以文件形式保存）

如果需要在控制台中实时监听输出内容，需要用指令

`listen on`

不需要时用指令

`listen off`

关闭监听模式

## 配置文件及其规范