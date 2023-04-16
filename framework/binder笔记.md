同学们好，欢迎来到享学课堂，我是今天的主讲老师 Leo，

我们正式 <font color=red>上课的时间 20：00</font> ，已经进来的同学请耐心等候下其他同学哦。







native  java   可以通信



大管家  --- 



.exe



node

sm注册完成

1. 打开驱动，内存映射（设置大小 128K）
2. 设置 SM 为大管家 --- sm  作用  为了管理系统服务
   1. 创建 binder_node 结构体对象
   2. proc --》 binder_node
   3. 创建  work  和 todo --》类似 messageQueue
3. BC_ENTER_LOOPER 命令
   1. 写入状态Loop
   2. 去读数据：binder_thread_read：ret = wait_event_freezable_exclusive(proc->wait, binder_has_proc_work(proc, thread)); 进入等待

sm获取 --- native这块 --- 



获取sm的情况：native 的服务注册和获取的时候都会走这个

1. 注册服务到sm
2. 通过sm去获取服务 ---java

也是服务端

1. ProcessState::self()->getContextObject(NULL)、
   1. ProcessState::self()
      1. 打开驱动：binder
      2. 设置线程最大数目：15个
      3. mmap  -- 设置共享内存大小 --- （1M-8K） 普通服务的大小
   2. getContextObject
      1. 创建一个BpBinder --- 客户端的对象
2. interface_cast
   1. new BpServiceManager(new BpBinder) ==》 new Proxy(binder==BinderProxy)
   2. remote.transact -->远程调用  
   3. remote == BpBinder
3. java 层 --- ServiceManager.addService
   1. new ServiceManagerProxy(new BinderProxy)
   2. mRemote == BinderProxy
   3. BinderProxy.mObject == BpBinder
   4. mRemote.transact == BpBinder.transact



AIDL

普通类之间的调用  --- 提供接口



AMS  --- ActivityManagerNative





BpBinder  -- native 的方法







政府机构 --- 买车 上牌 --- 流程很复杂  （AIDL -- 黄牛，中介）



讲解 AIDL 生成的代码  --- 字节跳动的面试题 



java 类 想要拥有跨进程能力  --- 继承Binder



Activity  --- 





同步的情况   --- transcat 调用后 会挂起 -- 一般都是同步



```
ServiceManager.addService  服务的注册
ServiceManager.getService  服务的获取
```



多看，多做笔记，多总结，先放过细节，





AIDL

