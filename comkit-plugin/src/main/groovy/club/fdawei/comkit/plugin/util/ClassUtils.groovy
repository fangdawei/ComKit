package club.fdawei.comkit.plugin.util

/**
 * Created by david on 2019/07/22.
 */
class ClassUtils {

    static String getClassName(File dir, File classFile) {
        def dirPath = dir.absolutePath
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath.concat(File.separator)
        }
        def relativePath = classFile.absolutePath.replace(dirPath, '')
        return getClassName(relativePath)
    }

    static String getClassName(String relativePath) {
        if (!relativePath.endsWith('.class')) {
            return false
        }
        return relativePath.replace('.class', '').replace(File.separator, '.')
    }
}
