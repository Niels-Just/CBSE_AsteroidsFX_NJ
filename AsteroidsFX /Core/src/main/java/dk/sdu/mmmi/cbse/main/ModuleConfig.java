package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ModuleConfig {

    private final ModuleLayer pluginLayer = buildPluginLayer();

    public ModuleConfig() {
    }

    // Load the plugins folder on its own so two modules can use the same package name.
    private static ModuleLayer buildPluginLayer() {
        Path pluginsDir = Paths.get("plugins");
        ModuleFinder finder = ModuleFinder.of(pluginsDir);

        List<String> roots = finder.findAll().stream().map(ref -> ref.descriptor().name()).collect(toList());

        if (roots.isEmpty()) {
            return ModuleLayer.boot();
        }

        java.lang.module.Configuration cfg = ModuleLayer.boot().configuration().resolve(finder, ModuleFinder.of(), roots);

        return ModuleLayer.boot().defineModulesWithOneLoader(cfg, ClassLoader.getSystemClassLoader());
    }

    @Bean
    public Game game() {
        return new Game(gamePluginServices(), entityProcessingServiceList(), postEntityProcessingServices());
    }

    // This also picks up features from the plugins folder.
    @Bean
    public List<IEntityProcessingService> entityProcessingServiceList() {
        return ServiceLoader.load(pluginLayer, IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean
    public List<IGamePluginService> gamePluginServices() {
        return ServiceLoader.load(pluginLayer, IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    @Bean
    public List<IPostEntityProcessingService> postEntityProcessingServices() {
        return ServiceLoader.load(pluginLayer, IPostEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
