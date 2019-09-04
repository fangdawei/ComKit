package club.fdawei.comkit.plugin.base

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform

/**
 * Create by david on 2019/08/27.
 */
class CommonTransform extends Transform {
    @Override
    String getName() {
        return null
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return null
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return null
    }

    @Override
    boolean isIncremental() {
        return false
    }
}
