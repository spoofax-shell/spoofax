package org.metaborg.spoofax.core.project.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.metaborg.core.language.LanguageContributionIdentifier;
import org.metaborg.core.language.LanguageIdentifier;
import org.metaborg.core.project.configuration.ConfigurationBasedLanguageSpecConfigBuilder;
import org.metaborg.core.project.configuration.ConfigurationReaderWriter;
import org.metaborg.spoofax.core.project.settings.Format;
import java.util.HashSet;
import java.util.Set;
import org.metaborg.core.project.configuration.ILanguageSpecConfig;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.virtlink.commons.configuration2.jackson.JacksonConfiguration;

/**
 * Configuration-based builder for {@link ILanguageSpecConfig} objects.
 */
public class ConfigurationBasedSpoofaxLanguageSpecConfigBuilder extends ConfigurationBasedLanguageSpecConfigBuilder implements ISpoofaxLanguageSpecConfigBuilder {

    protected final Set<String> pardonedLanguages = new HashSet<>();
    @Nullable protected Format format = null;
    @Nullable protected String externalDef = null;
    @Nullable protected String externalJar = null;
    @Nullable protected String externalJarFlags = null;
    protected final List<String> sdfArgs = new ArrayList<>();
    protected final List<String> strategoArgs = new ArrayList<>();

    /**
     * Initializes a new instance of the {@link ConfigurationBasedSpoofaxLanguageSpecConfigBuilder} class.
     *
     * @param configurationReaderWriter The configuration reader/writer.
     */
    @Inject
    public ConfigurationBasedSpoofaxLanguageSpecConfigBuilder(final ConfigurationReaderWriter configurationReaderWriter) {
        super(configurationReaderWriter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfig build() throws IllegalStateException {
        if (!isValid())
            throw new IllegalStateException(validateOrError());

        JacksonConfiguration configuration = createConfiguration();

        return new ConfigurationBasedSpoofaxLanguageSpecConfig(
                configuration,
                this.identifier,
                this.name,
                this.compileDependencies,
                this.runtimeDependencies,
                this.languageContributions,
                this.pardonedLanguages,
                this.format,
                this.externalDef,
                this.externalJar,
                this.externalJarFlags,
                this.sdfArgs,
                this.strategoArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String validateOrError() {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder reset() {
        super.reset();

        this.pardonedLanguages.clear();
        this.format = null;
        this.sdfArgs.clear();
        this.strategoArgs.clear();
        this.externalDef = null;
        this.externalJar = null;
        this.externalJarFlags = null;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ISpoofaxLanguageSpecConfigBuilder copyFrom(ISpoofaxLanguageSpecConfig config) {
        super.copyFrom(config);

        withPardonedLanguages(config.pardonedLanguages());
        withFormat(config.format());
        withExternalDef(config.externalDef());
        withExternalJar(config.externalJar());
        withExternalJarFlags(config.externalJarFlags());
        withSdfArgs(config.sdfArgs());
        withStrategoArgs(config.strategoArgs());

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withIdentifier(LanguageIdentifier identifier) {
        super.withIdentifier(identifier);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withName(String name) {
        super.withName(name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withCompileDependencies(Iterable<LanguageIdentifier> dependencies) {
        super.withCompileDependencies(dependencies);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder addCompileDependencies(Iterable<LanguageIdentifier> dependencies) {
        super.addCompileDependencies(dependencies);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withRuntimeDependencies(Iterable<LanguageIdentifier> dependencies) {
        super.withRuntimeDependencies(dependencies);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder addRuntimeDependencies(Iterable<LanguageIdentifier> dependencies) {
        super.addRuntimeDependencies(dependencies);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withLanguageContributions(Iterable<LanguageContributionIdentifier> contributions) {
        super.withLanguageContributions(contributions);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder addLanguageContributions(Iterable<LanguageContributionIdentifier> contributions) {
        super.addLanguageContributions(contributions);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withPardonedLanguages(Iterable<String> languages) {
        this.pardonedLanguages.clear();
        return addPardonedLanguages(languages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder addPardonedLanguages(Iterable<String> languages) {
        this.pardonedLanguages.addAll(Lists.newArrayList(languages));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withFormat(Format format) {
        this.format = format;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withExternalDef(String def) {
        this.externalDef = def;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withExternalJar(String jar) {
        this.externalJar = jar;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withExternalJarFlags(String flags) {
        this.externalJarFlags = flags;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withSdfArgs(Iterable<String> args) {
        this.sdfArgs.addAll(Lists.newArrayList(args));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISpoofaxLanguageSpecConfigBuilder withStrategoArgs(Iterable<String> args) {
        this.strategoArgs.addAll(Lists.newArrayList(args));
        return this;
    }
}
