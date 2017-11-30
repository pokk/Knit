package taiwan.no.one.sample

import taiwan.no.one.compiler.Builder

/**
 * @author  jieyi
 * @since   2017/11/30
 */
interface Listener {
    fun checkListener(arg1: String, arg2: Int)
    fun finishedListener(res: String)
}

@Builder
abstract class TestListener : Listener

fun listener(listenerWrapper: ListenerWrapper.() -> Unit): Listener = object : Listener {
    val wrapper: ListenerWrapper = ListenerWrapper().apply(listenerWrapper)

    override fun checkListener(arg1: String, arg2: Int) =
        wrapper.checkListener?.invoke(arg1, arg2) ?: Unit

    override fun finishedListener(res: String) =
        wrapper.finishedListener?.invoke(res) ?: Unit
}

class ListenerWrapper {
    var checkListener: ((arg1: String, arg2: Int) -> Unit)? = null
    var finishedListener: ((res: String) -> Unit)? = null
}

fun main(args: Array<String>) {
//    println("Hello ${Generated_Hello().getName()}")
}