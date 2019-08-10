package club.fdawei.comkit.plugin


import com.android.build.gradle.internal.core.VariantConfiguration
import com.android.build.gradle.internal.dsl.CoreBuildType
import com.android.build.gradle.internal.dsl.CoreProductFlavor
import com.android.builder.core.ManifestAttributeSupplier
import com.android.builder.core.VariantType
import com.android.builder.dexing.DexingType
import com.android.builder.errors.EvalIssueReporter
import com.android.builder.model.*
import com.android.ide.common.resources.AssetSet
import com.android.ide.common.resources.ResourceSet
import com.android.sdklib.AndroidVersion

import java.util.function.BooleanSupplier
import java.util.function.Function
import java.util.function.IntSupplier
import java.util.function.Supplier

/**
 * Create by david on 2019/07/28.
 */
class VariantConfigurationProxy extends VariantConfiguration<CoreBuildType, CoreProductFlavor, CoreProductFlavor> {

    private VariantConfiguration<CoreBuildType, CoreProductFlavor, CoreProductFlavor> target
    private List<File> includeManifests

    VariantConfigurationProxy(
            VariantConfiguration<CoreBuildType, CoreProductFlavor, CoreProductFlavor> target,
            List<File> includeManifests,
            CoreProductFlavor defaultConfig,
            SourceProvider defaultSourceProvider,
            ManifestAttributeSupplier mainManifestAttributeSupplier,
            CoreBuildType buildType,
            SourceProvider buildTypeSourceProvider,
            VariantType type,
            VariantConfiguration<CoreBuildType, CoreProductFlavor, CoreProductFlavor> testedConfig,
            SigningConfig signingConfigOverride,
            EvalIssueReporter issueReporter,
            BooleanSupplier isInExecutionPhase) {
        super(
                defaultConfig,
                defaultSourceProvider,
                mainManifestAttributeSupplier,
                buildType,
                buildTypeSourceProvider,
                type,
                testedConfig,
                signingConfigOverride,
                issueReporter,
                isInExecutionPhase)
        this.target = target
        this.includeManifests = includeManifests
    }

    @Override
    String getFullName() {
        if (target == null) {
            return ''
        }
        return target.getFullName()
    }

    @Override
    String computeHybridVariantName() {
        return target.computeHybridVariantName()
    }

    @Override
    String computeFullNameWithSplits(String splitName) {
        return target.computeFullNameWithSplits(splitName)
    }

    @Override
    String getFlavorName() {
        return target.getFlavorName()
    }

    @Override
    String getBaseName() {
        return target.getBaseName()
    }

    @Override
    String computeBaseNameWithSplits(String splitName) {
        return target.computeBaseNameWithSplits(splitName)
    }

    @Override
    String getDirName() {
        return target.getDirName()
    }

    @Override
    Collection<String> getDirectorySegments() {
        return target.getDirectorySegments()
    }

    @Override
    String computeDirNameWithSplits(String... splitNames) {
        return target.computeDirNameWithSplits(splitNames)
    }

    @Override
    List<String> getFlavorNamesWithDimensionNames() {
        return target.getFlavorNamesWithDimensionNames()
    }

    @Override
    VariantConfiguration addProductFlavor(CoreProductFlavor productFlavor, SourceProvider sourceProvider, String dimensionName) {
        return target.addProductFlavor(productFlavor, sourceProvider, dimensionName)
    }

    @Override
    VariantConfiguration setVariantSourceProvider(SourceProvider sourceProvider) {
        return target.setVariantSourceProvider(sourceProvider)
    }

    @Override
    VariantConfiguration setMultiFlavorSourceProvider(SourceProvider sourceProvider) {
        return target.setMultiFlavorSourceProvider(sourceProvider)
    }

    @Override
    SourceProvider getVariantSourceProvider() {
        return target.getVariantSourceProvider()
    }

    @Override
    SourceProvider getMultiFlavorSourceProvider() {
        return target.getMultiFlavorSourceProvider()
    }

    @Override
    CoreProductFlavor getDefaultConfig() {
        return target.getDefaultConfig()
    }

    @Override
    SourceProvider getDefaultSourceSet() {
        return target.getDefaultSourceSet()
    }

    @Override
    ProductFlavor getMergedFlavor() {
        return target.getMergedFlavor()
    }

    @Override
    CoreBuildType getBuildType() {
        return target.getBuildType()
    }

    @Override
    SourceProvider getBuildTypeSourceSet() {
        return target.getBuildTypeSourceSet()
    }

    @Override
    boolean hasFlavors() {
        return target.hasFlavors()
    }

    @Override
    List<CoreProductFlavor> getProductFlavors() {
        return target.getProductFlavors()
    }

    @Override
    List<SourceProvider> getFlavorSourceProviders() {
        return target.getFlavorSourceProviders()
    }

    @Override
    VariantType getType() {
        return target.getType()
    }

    @Override
    VariantConfiguration getTestedConfig() {
        return target.getTestedConfig()
    }

    @Override
    String getOriginalApplicationId() {
        return target.getOriginalApplicationId()
    }

    @Override
    String getApplicationId() {
        return target.getApplicationId()
    }

    @Override
    String getTestApplicationId() {
        return target.getTestApplicationId()
    }

    @Override
    String getTestedApplicationId() {
        return target.getTestedApplicationId()
    }

    @Override
    String getIdOverride() {
        return target.getIdOverride()
    }

    @Override
    String getVersionName() {
        return target.getVersionName()
    }

    @Override
    int getVersionCode() {
        return target.getVersionCode()
    }

    @Override
    Supplier<String> getVersionNameSerializableSupplier() {
        return target.getVersionNameSerializableSupplier()
    }

    @Override
    IntSupplier getVersionCodeSerializableSupplier() {
        return target.getVersionCodeSerializableSupplier()
    }

    @Override
    String getInstrumentationRunner() {
        return target.getInstrumentationRunner()
    }

    @Override
    Map<String, String> getInstrumentationRunnerArguments() {
        return target.getInstrumentationRunnerArguments()
    }

    @Override
    Boolean getHandleProfiling() {
        return target.getHandleProfiling()
    }

    @Override
    Boolean getFunctionalTest() {
        return target.getFunctionalTest()
    }

    @Override
    String getTestLabel() {
        return target.getTestLabel()
    }

    @Override
    String getPackageFromManifest() {
        return target.getPackageFromManifest()
    }

    @Override
    AndroidVersion getMinSdkVersion() {
        return target.getMinSdkVersion()
    }

    @Override
    int getMinSdkVersionValue() {
        return target.getMinSdkVersionValue()
    }

    @Override
    ApiVersion getTargetSdkVersion() {
        return target.getTargetSdkVersion()
    }

    @Override
    File getMainManifest() {
        return target.getMainManifest()
    }

    @Override
    List<SourceProvider> getSortedSourceProviders() {
        return target.getSortedSourceProviders()
    }

    @Override
    List<File> getManifestOverlays() {
        def overlays = target.getManifestOverlays()
        if (includeManifests != null) {
            overlays.addAll(includeManifests)
        }
        return overlays
    }

    @Override
    List<File> getNavigationFiles() {
        return target.getNavigationFiles()
    }

    @Override
    Set<File> getSourceFiles(Function<SourceProvider, Collection<File>> f) {
        return target.getSourceFiles(f)
    }

    @Override
    List<ResourceSet> getResourceSets(boolean validateEnabled) {
        return target.getResourceSets(validateEnabled)
    }

    @Override
    List<AssetSet> getSourceFilesAsAssetSets(Function<SourceProvider, Collection<File>> function) {
        return target.getSourceFilesAsAssetSets(function)
    }

    @Override
    int getRenderscriptTarget() {
        return target.getRenderscriptTarget()
    }

    @Override
    Collection<File> getRenderscriptSourceList() {
        return target.getRenderscriptSourceList()
    }

    @Override
    Collection<File> getAidlSourceList() {
        return target.getAidlSourceList()
    }

    @Override
    Collection<File> getJniSourceList() {
        return target.getJniSourceList()
    }

    @Override
    void addBuildConfigField(String type, String name, String value) {
        target.addBuildConfigField(type, name, value)
    }

    @Override
    void addResValue(String type, String name, String value) {
        target.addResValue(type, name, value)
    }

    @Override
    List<Object> getBuildConfigItems() {
        return target.getBuildConfigItems()
    }

    @Override
    Map<String, ClassField> getMergedBuildConfigFields() {
        return target.getMergedBuildConfigFields()
    }

    @Override
    Map<String, ClassField> getMergedResValues() {
        return target.getMergedResValues()
    }

    @Override
    List<Object> getResValues() {
        return target.getResValues()
    }

    @Override
    SigningConfig getSigningConfig() {
        return target.getSigningConfig()
    }

    @Override
    boolean isSigningReady() {
        return target.isSigningReady()
    }

    @Override
    boolean isTestCoverageEnabled() {
        return target.isTestCoverageEnabled()
    }

    @Override
    Map<String, Object> getManifestPlaceholders() {
        return target.getManifestPlaceholders()
    }

    @Override
    boolean isMultiDexEnabled() {
        return target.isMultiDexEnabled()
    }

    @Override
    File getMultiDexKeepFile() {
        return target.getMultiDexKeepFile()
    }

    @Override
    File getMultiDexKeepProguard() {
        return target.getMultiDexKeepProguard()
    }

    @Override
    boolean isLegacyMultiDexMode() {
        return target.isLegacyMultiDexMode()
    }

    @Override
    DexingType getDexingType() {
        return target.getDexingType()
    }

    @Override
    boolean getRenderscriptSupportModeEnabled() {
        return target.getRenderscriptSupportModeEnabled()
    }

    @Override
    boolean getRenderscriptSupportModeBlasEnabled() {
        return target.getRenderscriptSupportModeBlasEnabled()
    }

    @Override
    boolean getRenderscriptNdkModeEnabled() {
        return target.getRenderscriptNdkModeEnabled()
    }

    @Override
    boolean isBundled() {
        return target.isBundled()
    }

    @Override
    protected EvalIssueReporter getIssueReporter() {
        return target.getIssueReporter()
    }

    static VariantConfigurationProxy create(
            VariantConfiguration<CoreBuildType, CoreProductFlavor, CoreProductFlavor> target,
            List<File> includeManifests) {

        new VariantConfigurationProxy(
                target,
                includeManifests,
                target.defaultConfig,
                target.defaultSourceSet,
                null,
                target.buildType,
                target.buildTypeSourceSet,
                target.type,
                target.testedConfig,
                target.signingConfig,
                target.issueReporter,
                { return true }
        )
    }
}
