package dev.sort.oss.quarkus.config.hocon.deployment;

import dev.sort.oss.quarkus.config.hocon.runtime.ApplicationHoconConfigSourceLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalBootstrapConfigSourceProviderBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem;
import io.quarkus.deployment.builditem.StaticInitConfigSourceProviderBuildItem;
import io.quarkus.runtime.configuration.ProfileManager;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ConfigHoconProcessor {

    public static final String CONFIG_HOCON = "hocon-config";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(CONFIG_HOCON);
    }

    @BuildStep
    public void bootstrap(
            BuildProducer<AdditionalBootstrapConfigSourceProviderBuildItem> additionalBootstrapConfigSourceProvider,
            BuildProducer<StaticInitConfigSourceProviderBuildItem> staticInitConfigSourceProvider) {
        additionalBootstrapConfigSourceProvider.produce(new AdditionalBootstrapConfigSourceProviderBuildItem(
                ApplicationHoconConfigSourceLoader.InFileSystem.class.getName()));
        additionalBootstrapConfigSourceProvider.produce(new AdditionalBootstrapConfigSourceProviderBuildItem(
                ApplicationHoconConfigSourceLoader.InClassPath.class.getName()));
        staticInitConfigSourceProvider.produce(new StaticInitConfigSourceProviderBuildItem(
                ApplicationHoconConfigSourceLoader.InFileSystem.class.getName()));
        staticInitConfigSourceProvider.produce(new StaticInitConfigSourceProviderBuildItem(
                ApplicationHoconConfigSourceLoader.InClassPath.class.getName()));
    }

    @BuildStep
    void watchHoconConfig(BuildProducer<HotDeploymentWatchedFileBuildItem> watchedFiles) {
        List<String> configWatchedFiles = new ArrayList<>();
        String userDir = System.getProperty("user.dir");

        String[] exts = ApplicationHoconConfigSourceLoader.HOCON_FILE_EXT;

        // Main files
        for (String ext : exts) {
            configWatchedFiles.add("application." + ext);
            configWatchedFiles.add(Paths.get(userDir, "config", "application." + ext).toAbsolutePath().toString());
        }

        // Profiles
        String profile = ProfileManager.getActiveProfile();
        for (String ext : exts) {
            configWatchedFiles.add(String.format("application-%s." + ext, profile));
            configWatchedFiles
                    .add(Paths.get(userDir, "config", String.format("application-%s." + ext, profile)).toAbsolutePath().toString());

        }
        for (String configWatchedFile : configWatchedFiles) {
            watchedFiles.produce(new HotDeploymentWatchedFileBuildItem(configWatchedFile));
        }
    }
}
