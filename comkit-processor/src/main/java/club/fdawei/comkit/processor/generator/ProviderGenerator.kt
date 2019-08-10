package club.fdawei.comkit.processor.generator

import club.fdawei.comkit.processor.base.Context
import club.fdawei.comkit.processor.base.UtilBox
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.TypeElement

/**
 * Create by david on 2019/07/20.
 */
class ProviderGenerator(
    private val context: Context,
    private val utilBox: UtilBox
) {
    private val appDelegateElements = mutableListOf<TypeElement>()

    fun addAppDelegateWith(element: TypeElement) {
        appDelegateElements.add(element)
    }

    fun clear() {
        appDelegateElements.clear()
    }

    private fun buildFunProvide(): FunSpec {
        val funBuilder = FunSpec.builder(IAppDelegateProvider.FUN_PROVIDE)
            .addModifiers(KModifier.OVERRIDE)
        funBuilder.addParameter(
            IAppDelegateProvider.FUN_PROVIDE_PARAM,
            TypeBox.I_APP_DELEGATE_REGISTRY
        )
        appDelegateElements.forEach {
            funBuilder.addStatement(
                "${IAppDelegateProvider.FUN_PROVIDE_PARAM}." +
                        "${IAppDelegateRegistry.FUN_REGISTER}(%T.${Keeper.FUN_OF}({ %T() } ))",
                TypeBox.KEEPER, it.asClassName()
            )
        }
        return funBuilder.build()
    }

    private fun buildTypeProvider(name: String): TypeSpec {
        return TypeSpec.classBuilder(name)
            .addSuperinterface(TypeBox.I_APP_DELEGATE_PROVIDER)
            .addFunction(buildFunProvide())
            .build()
    }

    fun genKtFile(filer: Filer) {
        if (context.moduleName.isNullOrBlank() || appDelegateElements.isEmpty()) {
            return
        }
        val providerName = buildProviderName(context.moduleName!!)
        FileSpec.builder(PROVIDER_PACKAGE, providerName)
            .addComment("Generated automatically by ComKit. Do not modify!")
            .addType(buildTypeProvider(providerName))
            .build()
            .writeTo(filer)
    }
}