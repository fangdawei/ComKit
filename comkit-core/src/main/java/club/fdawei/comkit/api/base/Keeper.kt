package club.fdawei.comkit.api.base

/**
 * Create by david on 2019/07/20.
 */
class Keeper<T>(
    private val creator: () -> T
) {
    val target by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { creator.invoke() }

    companion object {
        fun <T> of(creator: () -> T): Keeper<T> {
            return Keeper(creator)
        }
    }
}