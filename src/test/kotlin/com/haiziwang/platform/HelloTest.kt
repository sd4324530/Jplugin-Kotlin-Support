package com.haiziwang.platform

import com.haiziwang.platform.jplugin.kotlin.*
import com.haiziwang.platform.jplugin.kotlin.http.get
import net.jplugin.core.das.mybatis.api.MysqlPageInterceptor
import net.jplugin.core.kernel.api.AbstractPlugin
import net.jplugin.core.kernel.api.PluginAnnotation

@PluginAnnotation
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
        configChangeHandler {
            "*"[MyConfigChangeHandler::class]
        }
        rule {
            "/test"[IMyService::class, MyService::class]
        }
    }

    override fun getPrivority() = 0

    override fun init() {
    }
}


fun main(args: Array<String>) {
    Plugin()

    get("https://www.baidu.com") { (json, e) ->
        //成功
        if (null == e) {
            println(json)
        }
        //失败
        else {

        }
    }
}