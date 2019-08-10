package club.fdawei.comkit.processor.generator

import com.squareup.kotlinpoet.ClassName

/**
 * Create by david on 2019/07/20.
 */
object TypeBox {
    val KEEPER = ClassName.bestGuess("club.fdawei.comkit.api.base.Keeper")
    val I_APP_DELEGATE_PROVIDER = ClassName.bestGuess("club.fdawei.comkit.api.app.IAppDelegateProvider")
    val I_APP_DELEGATE_REGISTRY = ClassName.bestGuess("club.fdawei.comkit.api.app.IAppDelegateRegistry")
}
