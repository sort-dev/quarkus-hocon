package dev.sort.oss.quarkus.config.hocon.runtime;

import io.smallrye.config.AbstractLocationConfigSourceLoader;
import io.smallrye.config.source.hocon.HoconConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationHoconConfigSourceLoader extends AbstractLocationConfigSourceLoader {
    public static final String[] HOCON_FILE_EXT = new String[] { "conf", "hocon" };

    @Override
    protected String[] getFileExtensions() {
        return HOCON_FILE_EXT;
    }

    @Override
    protected ConfigSource loadConfigSource(final URL url, final int ordinal) throws IOException {
        return new HoconConfigSource(url, ordinal);
    }

    public static class InClassPath extends ApplicationHoconConfigSourceLoader implements ConfigSourceProvider {
        @Override
        public List<ConfigSource> getConfigSources(final ClassLoader classLoader) {
            return Stream.of(getFileExtensions()).flatMap(ext ->
                            loadConfigSources("application." + ext, 255, classLoader).stream()
                    ).collect(Collectors.toList());
        }

        @Override
        protected List<ConfigSource> tryFileSystem(final URI uri, final int ordinal) {
            return new ArrayList<>();
        }
    }

    public static class InFileSystem extends ApplicationHoconConfigSourceLoader implements ConfigSourceProvider {
        @Override
        public List<ConfigSource> getConfigSources(final ClassLoader classLoader) {
            return Stream.of(getFileExtensions()).flatMap(ext ->
                    loadConfigSources(Paths.get(System.getProperty("user.dir"), "config", "application." + ext)
                            .toUri().toString(), 265, classLoader).stream()
            ).collect(Collectors.toList());
        }

        @Override
        protected List<ConfigSource> tryClassPath(final URI uri, final int ordinal, final ClassLoader classLoader) {
            return new ArrayList<>();
        }
    }
}
