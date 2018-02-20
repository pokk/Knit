package taiwan.no.one.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * @author  jieyi
 * @since   2017/11/30
 */
@AutoService(Processor::class)
class KnitProcessor : AbstractProcessor() {
    private val utils by lazy { processingEnv.elementUtils }
    private val genParts by lazy { GenerateParts(utils) }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(KnitBuilder::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(KnitBuilder::class.java).forEach {
            val cls = genParts.genWrapperClass(it)
            val func = genParts.genDSLFunction(it)

            FileSpec.builder(utils.getPackageOf(it).toString(), "Knit${it.simpleName}").run {
                func.zip(cls).forEach {
                    addFunction(it.first)
                    println(it.second.toString())
                    addType(it.second)
                }
                build()
            }.run {
                    //                writeTo(System.out)
                writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]))
            }
        }

        return true
    }
}