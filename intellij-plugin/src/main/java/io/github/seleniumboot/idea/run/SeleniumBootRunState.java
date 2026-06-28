package io.github.seleniumboot.idea.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Builds and runs the Maven command line for a {@link SeleniumBootRunConfiguration}.
 * Prefers the project's Maven wrapper ({@code mvnw}) when present, else {@code mvn}.
 */
final class SeleniumBootRunState extends CommandLineState {

    private final SeleniumBootRunConfiguration config;

    SeleniumBootRunState(@NotNull ExecutionEnvironment environment, @NotNull SeleniumBootRunConfiguration config) {
        super(environment);
        this.config = config;
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        String basePath = config.getProject().getBasePath();
        if (basePath == null) {
            throw new ExecutionException("Cannot determine project base directory.");
        }

        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setWorkDirectory(basePath);
        cmd.setExePath(resolveMaven(basePath));

        // Goals (default "test"), e.g. "clean test".
        String goals = config.getGoals().isBlank() ? "test" : config.getGoals();
        cmd.addParameters(ParametersListUtil.parse(goals));

        // Selenium Boot knobs → system properties read by ConfigurationLoader.
        if (!config.getProfile().isBlank()) {
            cmd.addParameter("-Dselenium.boot.profile=" + config.getProfile());
        }
        if (!config.getConfigFile().isBlank()) {
            cmd.addParameter("-Dselenium.boot.config=" + config.getConfigFile());
        }
        if (!config.getTestFilter().isBlank()) {
            cmd.addParameter("-Dtest=" + config.getTestFilter());
        }
        if (!config.getMavenArgs().isBlank()) {
            cmd.addParameters(ParametersListUtil.parse(config.getMavenArgs()));
        }

        OSProcessHandler handler = new OSProcessHandler(cmd);
        ProcessTerminatedListener.attach(handler);
        return handler;
    }

    /** Project Maven wrapper if it exists, otherwise {@code mvn} from PATH. */
    private static String resolveMaven(String basePath) {
        String wrapperName = SystemInfo.isWindows ? "mvnw.cmd" : "mvnw";
        File wrapper = new File(basePath, wrapperName);
        if (wrapper.isFile()) {
            return wrapper.getAbsolutePath();
        }
        return SystemInfo.isWindows ? "mvn.cmd" : "mvn";
    }
}
