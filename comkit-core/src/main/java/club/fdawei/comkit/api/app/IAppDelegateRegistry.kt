package club.fdawei.comkit.api.app

import club.fdawei.comkit.api.base.Keeper

/**
 * Create by david on 2019/07/20.
 */
interface IAppDelegateRegistry {
    fun register(keeper: Keeper<out IAppDelegate>)
}