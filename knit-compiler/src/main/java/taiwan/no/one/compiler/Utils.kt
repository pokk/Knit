package taiwan.no.one.compiler

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

/**
 * @author  jieyi
 * @since   2017/12/01
 */
fun TypeName.correct(): TypeName {
    return when (this.toString()) {
        "java.lang.String" -> String::class.asTypeName()
        else ->
//            if ("java.util.HashMap" in this.toString()) {
//                println("=================================")
//                println(MutableList::class.asTypeName().topLevelClassName())
//                println("=================================")
//                MutableList::class.asTypeName()
//            }
//            else
            this
    }
}
