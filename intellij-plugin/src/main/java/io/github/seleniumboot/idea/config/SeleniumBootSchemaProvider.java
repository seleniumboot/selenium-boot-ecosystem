package io.github.seleniumboot.idea.config;

import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.SchemaType;
import org.jetbrains.annotations.NlsContexts;
import org.jetbrains.annotations.Nullable;

/**
 * Binds the bundled {@code selenium-boot.schema.json} to any file named
 * {@code selenium-boot.yml} (or {@code .yaml}), giving completion, enum
 * validation and inline docs with no custom UI.
 */
final class SeleniumBootSchemaProvider implements JsonSchemaFileProvider {

    /** Classpath location of the schema, relative to this class's module resources. */
    private static final String SCHEMA_RESOURCE = "/schemas/selenium-boot.schema.json";

    private final VirtualFile schemaFile;

    SeleniumBootSchemaProvider(VirtualFile schemaFile) {
        this.schemaFile = schemaFile;
    }

    @Override
    public boolean isAvailableForFile(VirtualFile file) {
        String name = file.getName();
        return "selenium-boot.yml".equals(name) || "selenium-boot.yaml".equals(name);
    }

    @Override
    public @NlsContexts.Label String getName() {
        return "Selenium Boot";
    }

    @Override
    public @Nullable VirtualFile getSchemaFile() {
        return schemaFile;
    }

    @Override
    public SchemaType getSchemaType() {
        // Shipped inside the plugin; users cannot edit it.
        return SchemaType.embeddedSchema;
    }

    static String schemaResourcePath() {
        return SCHEMA_RESOURCE;
    }
}
