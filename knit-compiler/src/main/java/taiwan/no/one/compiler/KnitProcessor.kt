package taiwan.no.one.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * @author  jieyi
 * @since   2017/11/30
 */
@AutoService(Processor::class)
class KnitProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Builder::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Builder::class.java).forEach {
            //            println("------------------------------------")
//            val typeE = processingEnv.elementUtils.getTypeElement(it.toString())
            // 這邊可以拿到 all methods
            processingEnv.elementUtils.getAllMembers(it as TypeElement)

            val interfaceElement = processingEnv.elementUtils.getTypeElement(it.interfaces[0].toString())
            // 真的拿到 interface 的 methods!!
            interfaceElement.enclosedElements.forEach {
                println(it.javaClass)
                println(it.simpleName.toString())
                // 拿到 parameters and data-type
                ((it as ExecutableElement).parameters).forEach {
                    println(it)
                    println(it.asType())
                }
                val params = ((it).parameters).map { it.asType().asTypeName() }

                // print the information!!
                val f = FileSpec.builder("ttt", "11")
                    .addType(TypeSpec.classBuilder("ttt").
                        addProperty(it.simpleName.toString(), LambdaTypeName.
                            get(parameters = params, returnType = Unit::class.asTypeName()).
                            asNullable()).
                        build())
                    .build()
                f.writeTo(System.out)
            }

            println("------------------------------------")
            val className = it.simpleName.toString()
            val pack = processingEnv.elementUtils.getPackageOf(it).toString()
            generateClass(className, pack)
        }
        return true
    }

    private fun generateClass(className: String, pack: String) {
        val fileName = "DSLBuilder$className"
        val file = FileSpec.builder(pack, fileName)
            .addType(TypeSpec.classBuilder(fileName)
                .addFunction(FunSpec.builder("getName")
                    .addStatement("return \"World\"")
                    .build())
                .build())
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
//        file.writeTo(System.out)
    }
}