package com.haiziwang.platform

import com.haiziwang.platform.jplugin.kotlin.esfClient
import com.haiziwang.platform.jplugin.kotlin.log.log
import net.jplugin.core.kernel.api.IStartup
import net.jplugin.core.kernel.api.PluginError
import net.jplugin.ext.webasic.api.AbstractExController
import javax.jms.Message
import javax.jms.MessageListener

/**
 * @author peiyu
 */
class MyCtrl : AbstractExController() {
    private val service = esfClient<MyRpcProxy>()

    fun index() {
        service.sayWhat("hello!")
        log.debug { "hello!" }
    }
}


class MyCtrl2 : AbstractExController()
interface IMyService
class MyService : IMyService
interface IMyMapping {
    fun hello(what: String): String
}

interface MyRpcProxy {
    fun sayWhat(what: String)
}
class MyRestfulProxy

class MyCppReq

class MyStartUp : IStartup {
    override fun startFailed(p0: Throwable?, p1: MutableList<PluginError>?) {

    }

    override fun startSuccess() {

    }
}

class MyMessageListener : MessageListener {
    override fun onMessage(p0: Message?) {

    }

}