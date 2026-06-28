package io.github.seleniumboot.idea.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Registers the Selenium Boot JSON schema with the IDE's JSON Schema engine.
 *
 * <p>Wired in {@code plugin.xml} via the
 * {@code com.jetbrains.jsonSchema.ProviderFactory} extension point. The engine
 * then drives YAML completion/validation for every {@code selenium-boot.yml}.
 */
public final class SeleniumBootSchemaProviderFactory implements JsonSchemaProviderFactory {

    @Override
    public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        VirtualFile schemaFile =
                JsonSchemaProviderFactory.getResourceFile(getClass(), SeleniumBootSchemaProvider.schemaResourcePath());
        if (schemaFile == null) {
            return Collections.emptyList();
        }
        return List.of(new SeleniumBootSchemaProvider(schemaFile));
    }
}
