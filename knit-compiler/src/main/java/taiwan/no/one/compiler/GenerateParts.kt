package taiwan.no.one.compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * @author  jieyi
 * @since   2017/12/01
 */
class GenerateParts(private val element: Elements) {
    /**
     * @param classElement Element :
     */
    fun genWrapperClass(classElement: Element): List<TypeSpec> = (classElement as TypeElement).interfaces.map {
        val className = "${it.toString().split(".").last()}Wrapper"
        val interfaceElement = element.getTypeElement(it.toString())
        val methods = interfaceElement.enclosedElements.map {
            val params = ((it as ExecutableElement).parameters).map { it.asType().asTypeName().correct() }

            PropertySpec.builder(it.simpleName.toString(),
                LambdaTypeName.get(parameters = params, returnType = Unit::class.asTypeName()).asNullable()).run {
                initializer("null")
                mutable(true)
            }.build()
        }

        TypeSpec.classBuilder(className).run {
            addProperties(methods)
        }.build()
    }

    /**
     * @param classElement Element :
     */
    fun genDSLFunction(classElement: Element): List<FunSpec> = (classElement as TypeElement).interfaces.map {
        val name = ClassName.bestGuess("${it.toString().split(".").last()}Wrapper") as TypeName
        val interfaceElement = element.getTypeElement(it.toString())
        val wrapperVariable = PropertySpec.builder("wrapper", name).run {
            initializer("%N().apply(%N)", name.toString(), "wrapperListener")
        }.build()
        val overrideFunc = interfaceElement.enclosedElements.map {
            //            FunSpec.overriding(it as ExecutableElement).
//                addCode("return wrapper.%N?.invoke(%N) ?: Unit",
//                    it.simpleName.toString(),
//                    it.parameters.joinToString()).build()

            val funcParams = (it as ExecutableElement).parameters.map {
                ParameterSpec.builder(it.toString(), it.asType().asTypeName().correct()).build()
            }

            FunSpec.builder(it.simpleName.toString()).run {
                addParameters(funcParams)
                addCode("return wrapper.%N?.invoke(%N) ?: Unit", it.simpleName, it.parameters.joinToString())
            }.build()
        }

//                    |    ${overrideFunc.joinToString("\n\t")}
        FunSpec.builder(it.toString().split(".").last().decapitalize()).run {
            returns(it.asTypeName())
            addParameter(ParameterSpec.builder("wrapperListener",
                LambdaTypeName.get(receiver = name, returnType = Unit::class.asTypeName().correct())).build())
            addCode("""return object : %N {
                    |    %N
                    |    ${overrideFunc.joinToString("\n\toverride ", "override ")}
                    |}""".trimMargin(),
                it.asTypeName().toString().split(".").last(),
                wrapperVariable.toString())
        }.build()
    }
}
