# JPlugin In Kotlin

[![@peiyu on weibo](https://img.shields.io/badge/weibo-%40peiyu-red.svg)](http://weibo.com/1728407960)
[![JDK 1.8](https://img.shields.io/badge/JDK-1.8-green.svg "JDK 1.8")]()
[![Kotlin 1.3,10](https://img.shields.io/badge/Kotlin-1.3.10-green.svg "Kotlin 1.3.10")]()
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)



## 框架覆盖范围

- [x] Controller
- [x] ServiceExport
- [x] Mybatis
	- [x] Mapping
	- [x] Interceptor


- [x] ESFClient
	- [x] RPC
	- [x] Restful


- [x] Kmem
- [x] Mq
- [x] TMQ
- [x] CppClient
- [x] Startup
- [x] Rule



## 项目配置（DSL）

```kotlin
class Plugin : AbstractPlugin() {

    init {
        controller {
            "/test"[MyCtrl::class]
            "/test2"[MyCtrl2::class]
        }
        serviceExport {
            "/api"[MyService::class]
        }
        mybatis {
            mapping {
                +IMyMapping::class
            }
            interceptor {
                +MysqlPageInterceptor::class
            }
        }
        esfClient {
            rpc {
                "esf://KBDQE/rpc"[MyRpcProxy::class]
            }
            restful {
                "esf://KBDQE/rest"[MyRestfulProxy::class]
            }
        }
        kmem {
            +"cache1"
        }
        mq {
            "mq1"[MyMessageListener::class]
        }
        tmq {
            producer {
                +"testProducer"
            }
            consumer {
                "testProducer"[TmqconsumerListener::class]
            }
        }
        cppClient {
            +MyCppReq::class
        }
        startup {
            +MyStartUp::class
        }
        rule {
            "/test"[IMyService::class, MyService::class]
        }
    }

    override fun getPrivority() = 0

    override fun init() {
    }
}
```



## 极简开发扩展

### Log

~~在每个需要打印日志的类中定义Log变量~~

每个类都自带log对象，可直接使用

```kotlin
class MyCtrl : AbstractExController() {
    fun index() {
    	//直接使用
        log.debug("hello!")
    }
}
```

~~打印复杂日志前，有时会需要先判断项目日志级别~~

自动判断，无脑使用

```kotlin
class MyCtrl : AbstractExController() {
    fun index() {
        val id = 123
        val param1 = "param1"
        //花括号，自动判定日志级别，如果不符合，不会拼接
        log.debug { "业务id:$id 参数1:$param1" }
    }
}
```



### ESF客户端

极简写法

```kotlin
class MyCtrl : AbstractExController() {
    private val service = esfClient<MyRpcProxy>()

    fun index() {
        service.sayWhat("hello!")
    }
}
```



### DAO(Mybatis)

极简使用

```kotlin
//创建查询条件
val example = createCriteria()
//添加查询条件
example.createConditon().run {
    //实现 and clusterId = ?
    andEqualTo(TKredisSlowlog.Field.CLUSTERID, request.clusterId)
    //实现 slowTime >= startTime and slowTime <= endTime
    andBetween(TKredisSlowlog.Field.SLOWTIME, request.startTime, request.endTime)
}
//添加order by条件
example.orderByClause = TKredisSlowlog.Field.SLOWTIME + DESC
//执行sql
val slowLogList = runWithMapper(TKredisSlowlogMapper::class) { it.selectByExample(example) }
```



### Controller

~~每个参数调用一次getParam~~

自动将参数转化成data class

```kotlin
class MyCtrl : AbstractExController() {
    fun index() {
        val bean = getBean(GetSlowLogDataRequest::class)
    }
}

data class GetSlowLogDataRequest(val clusterId: String, val startTime: String, val endTime: String)
```



### Http请求

极简使用

```kotlin
get("https://www.baidu.com") { (json, e) ->
        //成功
        if (null == e) {
            println(json)
        }
        //失败
        else {

        }
    }
```



## Kotlin介绍

Kotlin 是一个基于 JVM 的新的编程语言，由 [JetBrains](https://baike.baidu.com/item/JetBrains) 开发。

Kotlin可以编译成Java字节码，也可以编译成JavaScript，方便在没有JVM的设备上运行。

JetBrains，作为目前广受欢迎的Java IDE [IntelliJ](https://baike.baidu.com/item/IntelliJ) 的提供商，在 Apache 许可下已经开源其Kotlin 编程语言。

Kotlin已正式成为Android官方支持开发语言。

## Kotlin特点

比Java更安全，能够静态检测常见的陷阱。如：引用空指针

比Java更简洁，通过支持variable type inference，higher-order functions (closures)，extension functions，mixins and first-class delegation等实现。

比最成熟的竞争对手[Scala](https://baike.baidu.com/item/Scala)语言更加简单。



## JPlugin介绍

https://github.com/sunlet/jplugin