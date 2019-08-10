package club.fdawei.comkit.plugin.ext

/**
 * Created by david on 2019/07/19.
 */
class ComKitExtension {

    private String packageName = null
    private String name = null
    private Set<String> manifests = new LinkedHashSet()
    private ModuleNameObserver moduleNameObserver

    String getPackageName() {
        return packageName
    }

    void setPackageName(String packageName) {
        this.packageName = packageName
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    Set<String> getManifests() {
        return manifests
    }

    void observeModuleName(ModuleNameObserver observer) {
        this.moduleNameObserver = observer
        if (observer != null) {
            observer.onUpdate(name)
        }
    }

    void includeManifest(CharSequence path) {
        this.manifests.add(path)
    }

    void moduleName(CharSequence name) {
        this.name = name
        if (moduleNameObserver != null) {
            moduleNameObserver.onUpdate(name)
        }
    }
}
