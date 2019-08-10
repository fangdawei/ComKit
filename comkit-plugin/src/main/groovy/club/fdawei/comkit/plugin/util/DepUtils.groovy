package club.fdawei.comkit.plugin.util

/**
 * Created by david on 2019/07/17.
 */
class DepUtils {
    // groupId:artifactId:version
    static boolean isDepMaven(String dep) {
        return dep ==~ /^\S+:\S+:\S+$/
    }

    static boolean isDepProject(String dep) {
        return dep ==~ /^(:\S+)+$/
    }
}
