package club.fdawei.comkit.api.app

import club.fdawei.comkit.api.base.Keeper

/**
 * Created by david on 2019/07/19.
 */
abstract class AbsAppDelegateContainer : IAppDelegateContainer, IAppDelegateRegistry {

    private val delegateKeepers = mutableListOf<Keeper<out IAppDelegate>>()
    private val delegateProviders = mutableSetOf<IAppDelegateProvider>()
    override val delegates by lazy { delegateKeepers.map { it.target } }

    init {
        this.initProviders()
        this.delegateProviders.forEach {
            it.provide(this)
        }
    }

    protected abstract fun initProviders()

    protected fun addProvider(provider: IAppDelegateProvider) {
        delegateProviders.add(provider)
    }

    override fun register(keeper: Keeper<out IAppDelegate>) {
        delegateKeepers.add(keeper)
    }
}