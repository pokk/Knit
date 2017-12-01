package taiwan.no.one.sample

/**
 * @author  jieyi
 * @since   2017/12/01
 */
interface MyListener {
    fun checkListener(arg1: Int, arg2: Int)
    fun finishedListener(res: MutableList<Int>)
}