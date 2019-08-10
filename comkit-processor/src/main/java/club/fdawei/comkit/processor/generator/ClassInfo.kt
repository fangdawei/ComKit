package club.fdawei.comkit.processor.generator

/**
 * Create by david on 2019/07/20.
 */
const val PROVIDER_PACKAGE = "comkit.generated.providers"

fun buildProviderName(moduleName: String): String {
    return "ComKit_${moduleName}_Provider"
}

object IAppDelegateProvider {
    const val FUN_PROVIDE = "provide"
    const val FUN_PROVIDE_PARAM = "registry"
}

object IAppDelegateRegistry {
    const val FUN_REGISTER = "register"
}

object Keeper {
    const val FUN_OF = "of"
}