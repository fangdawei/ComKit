package club.fdawei.comkit.plugin


import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

/**
 * Create by david on 2019/07/21.
 */
class ComKitTransform extends Transform {

    private Project currProject

    ComKitTransform(Project currProject) {
        this.currProject = currProject
    }

    @Override
    String getName() {
        return 'comKit'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        new AppDelegateCollector(currProject, transformInvocation).transform()
    }
}
