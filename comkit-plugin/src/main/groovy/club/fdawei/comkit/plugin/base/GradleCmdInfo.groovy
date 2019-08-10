package club.fdawei.comkit.plugin.base

/**
 * Created by david on 2019/07/17.
 */
class GradleCmdInfo {
    private boolean isAssemble = false
    private List<String> assemblePaths = new LinkedList<>()

    boolean getIsAssemble() {
        return isAssemble
    }

    void setIsAssemble(boolean isAssemble) {
        this.isAssemble = isAssemble
    }

    boolean isMultiAssemble() {
        return assemblePaths.size() > 1
    }

    void addAssemblePath(String path) {
        assemblePaths.add(path)
    }

    boolean hasAssemblePath() {
        return !assemblePaths.isEmpty()
    }

    List<String> getAssemblePaths() {
        return assemblePaths
    }

    String getFirstAssemblePath() {
        if (!assemblePaths.isEmpty()) {
            return assemblePaths.get(0)
        }
        return null
    }

    @Override
    String toString() {
        return "(isAssemble=" + isAssemble +
                ", assemblePaths=" + assemblePaths +
                ')'
    }
}
