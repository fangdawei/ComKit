package club.fdawei.comkit.processor

import club.fdawei.comkit.annotation.AppDelegate
import club.fdawei.comkit.processor.base.Context
import club.fdawei.comkit.processor.base.UtilBox
import club.fdawei.comkit.processor.common.OPTION_MODULE_NAME
import club.fdawei.comkit.processor.generator.ProviderGenerator
import club.fdawei.comkit.processor.log.Logger
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * Created by david on 2019/07/19.
 */
class ComKitProcessor : AbstractProcessor() {

    private var isEnvironmentValid = false
    private val context = Context()
    private lateinit var filer: Filer
    private val utilBox = UtilBox()
    private lateinit var providerGenerator: ProviderGenerator

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            AppDelegate::class.java.canonicalName
        )
    }

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (processingEnv == null) {
            return
        }
        isEnvironmentValid = true
        filer = processingEnv.filer
        utilBox.elements = processingEnv.elementUtils
        utilBox.types = processingEnv.typeUtils
        utilBox.logger = Logger(processingEnv.messager)
        context.moduleName = processingEnv.options[OPTION_MODULE_NAME]
        providerGenerator = ProviderGenerator(context, utilBox)
        utilBox.logger.i("init $context")
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (!isEnvironmentValid) {
            return false
        }
        providerGenerator.clear()
        if (roundEnv != null) {
            collectAppDelegateWith(roundEnv)
            providerGenerator.genKtFile(filer)
        }
        return true
    }

    private fun collectAppDelegateWith(roundEnv: RoundEnvironment) {
        val elements = roundEnv.getElementsAnnotatedWith(AppDelegate::class.java)
        elements.forEach {
            if (it.kind == ElementKind.CLASS) {
                providerGenerator.addAppDelegateWith(it as TypeElement)
            }
        }
    }
}