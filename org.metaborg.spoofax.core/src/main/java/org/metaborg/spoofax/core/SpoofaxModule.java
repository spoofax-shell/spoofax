package org.metaborg.spoofax.core;

import org.metaborg.core.MetaborgModule;
import org.metaborg.core.action.IActionService;
import org.metaborg.core.analysis.IAnalysisService;
import org.metaborg.core.analysis.IAnalyzer;
import org.metaborg.core.build.IBuildOutputInternal;
import org.metaborg.core.build.IBuilder;
import org.metaborg.core.build.paths.ILanguagePathProvider;
import org.metaborg.core.completion.ICompletionService;
import org.metaborg.core.context.IContextFactory;
import org.metaborg.core.language.ILanguageDiscoveryService;
import org.metaborg.core.language.dialect.IDialectIdentifier;
import org.metaborg.core.language.dialect.IDialectProcessor;
import org.metaborg.core.language.dialect.IDialectService;
import org.metaborg.core.menu.IMenuService;
import org.metaborg.core.outline.IOutlineService;
import org.metaborg.core.processing.IProcessor;
import org.metaborg.core.processing.IProcessorRunner;
import org.metaborg.core.processing.analyze.IAnalysisResultProcessor;
import org.metaborg.core.processing.analyze.IAnalysisResultRequester;
import org.metaborg.core.processing.analyze.IAnalysisResultUpdater;
import org.metaborg.core.processing.parse.IParseResultProcessor;
import org.metaborg.core.processing.parse.IParseResultRequester;
import org.metaborg.core.processing.parse.IParseResultUpdater;
import org.metaborg.core.style.ICategorizerService;
import org.metaborg.core.style.IStylerService;
import org.metaborg.core.syntax.IParser;
import org.metaborg.core.syntax.ISyntaxService;
import org.metaborg.core.tracing.IHoverService;
import org.metaborg.core.tracing.IResolverService;
import org.metaborg.core.tracing.ITracingService;
import org.metaborg.core.transform.ITransformService;
import org.metaborg.core.transform.ITransformer;
import org.metaborg.core.unit.IInputUnitService;
import org.metaborg.core.unit.IUnitService;
import org.metaborg.runtime.task.primitives.TaskLibrary;
import org.metaborg.spoofax.core.action.ActionService;
import org.metaborg.spoofax.core.analysis.AnalysisCommon;
import org.metaborg.spoofax.core.analysis.ISpoofaxAnalysisService;
import org.metaborg.spoofax.core.analysis.ISpoofaxAnalyzer;
import org.metaborg.spoofax.core.analysis.SpoofaxAnalysisService;
import org.metaborg.spoofax.core.analysis.legacy.StrategoAnalyzer;
import org.metaborg.spoofax.core.analysis.taskengine.TaskEngineAnalyzer;
import org.metaborg.spoofax.core.build.ISpoofaxBuilder;
import org.metaborg.spoofax.core.build.SpoofaxBuildOutput;
import org.metaborg.spoofax.core.build.SpoofaxBuilder;
import org.metaborg.spoofax.core.build.paths.BuiltinLanguagePathProvider;
import org.metaborg.spoofax.core.completion.JSGLRCompletionService;
import org.metaborg.spoofax.core.context.IndexTaskContextFactory;
import org.metaborg.spoofax.core.context.LegacyContextFactory;
import org.metaborg.spoofax.core.language.LanguageDiscoveryService;
import org.metaborg.spoofax.core.language.dialect.DialectIdentifier;
import org.metaborg.spoofax.core.language.dialect.DialectProcessor;
import org.metaborg.spoofax.core.language.dialect.DialectService;
import org.metaborg.spoofax.core.menu.MenuService;
import org.metaborg.spoofax.core.outline.ISpoofaxOutlineService;
import org.metaborg.spoofax.core.outline.OutlineService;
import org.metaborg.spoofax.core.processing.ISpoofaxProcessor;
import org.metaborg.spoofax.core.processing.ISpoofaxProcessorRunner;
import org.metaborg.spoofax.core.processing.SpoofaxBlockingProcessor;
import org.metaborg.spoofax.core.processing.SpoofaxProcessorRunner;
import org.metaborg.spoofax.core.processing.analyze.ISpoofaxAnalysisResultProcessor;
import org.metaborg.spoofax.core.processing.analyze.ISpoofaxAnalysisResultRequester;
import org.metaborg.spoofax.core.processing.analyze.ISpoofaxAnalysisResultUpdater;
import org.metaborg.spoofax.core.processing.analyze.SpoofaxAnalysisResultProcessor;
import org.metaborg.spoofax.core.processing.parse.ISpoofaxParseResultProcessor;
import org.metaborg.spoofax.core.processing.parse.ISpoofaxParseResultRequester;
import org.metaborg.spoofax.core.processing.parse.ISpoofaxParseResultUpdater;
import org.metaborg.spoofax.core.processing.parse.SpoofaxParseResultProcessor;
import org.metaborg.spoofax.core.stratego.IStrategoCommon;
import org.metaborg.spoofax.core.stratego.IStrategoRuntimeService;
import org.metaborg.spoofax.core.stratego.StrategoCommon;
import org.metaborg.spoofax.core.stratego.StrategoRuntimeService;
import org.metaborg.spoofax.core.stratego.primitives.ComponentsPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.DummyPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.ForeignCallPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LanguageIncludeFilesPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LanguageIncludeLocationsPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LanguageSourceFilesPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LanguageSourceLocationsPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LegacyLanguageIncludeLocationsPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LegacyLanguageSourceLocationsPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LocalPathPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.LocalReplicatePrimitive;
import org.metaborg.spoofax.core.stratego.primitives.ParseFilePrimitive;
import org.metaborg.spoofax.core.stratego.primitives.ParseFilePtPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.ProjectPathPrimitive;
import org.metaborg.spoofax.core.stratego.primitives.SpoofaxJSGLRLibrary;
import org.metaborg.spoofax.core.stratego.primitives.SpoofaxPrimitiveLibrary;
import org.metaborg.spoofax.core.stratego.strategies.ParseFileStrategy;
import org.metaborg.spoofax.core.stratego.strategies.ParseStrategoFileStrategy;
import org.metaborg.spoofax.core.style.CategorizerService;
import org.metaborg.spoofax.core.style.ISpoofaxCategorizerService;
import org.metaborg.spoofax.core.style.ISpoofaxStylerService;
import org.metaborg.spoofax.core.style.StylerService;
import org.metaborg.spoofax.core.syntax.ISpoofaxParser;
import org.metaborg.spoofax.core.syntax.ISpoofaxSyntaxService;
import org.metaborg.spoofax.core.syntax.JSGLRParseService;
import org.metaborg.spoofax.core.syntax.JSGLRParserConfiguration;
import org.metaborg.spoofax.core.syntax.SpoofaxSyntaxService;
import org.metaborg.spoofax.core.terms.ITermFactoryService;
import org.metaborg.spoofax.core.terms.TermFactoryService;
import org.metaborg.spoofax.core.tracing.HoverService;
import org.metaborg.spoofax.core.tracing.ISpoofaxHoverService;
import org.metaborg.spoofax.core.tracing.ISpoofaxResolverService;
import org.metaborg.spoofax.core.tracing.ISpoofaxTracingService;
import org.metaborg.spoofax.core.tracing.ResolverService;
import org.metaborg.spoofax.core.tracing.TracingCommon;
import org.metaborg.spoofax.core.tracing.TracingService;
import org.metaborg.spoofax.core.transform.ISpoofaxTransformService;
import org.metaborg.spoofax.core.transform.IStrategoTransformer;
import org.metaborg.spoofax.core.transform.SpoofaxTransformService;
import org.metaborg.spoofax.core.transform.StrategoTransformer;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnitUpdate;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnitService;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxTransformUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxUnitService;
import org.metaborg.spoofax.core.unit.UnitService;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOperatorRegistry;
import org.spoofax.interpreter.library.index.primitives.legacy.LegacyIndexLibrary;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Guice module that specifies which implementations to use for services and factories.
 */
public class SpoofaxModule extends MetaborgModule {
    private MapBinder<String, IParser<ISpoofaxInputUnit, ISpoofaxParseUnit>> parserBinder;
    private MapBinder<String, ISpoofaxParser> spoofaxParserBinder;
    private MapBinder<String, IAnalyzer<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate>> analyzerBinder;
    private MapBinder<String, ISpoofaxAnalyzer> spoofaxAnalyzerBinder;
    private Multibinder<ClassLoader> strategoRuntimeClassloaderBinder;


    public SpoofaxModule() {
        this(SpoofaxModule.class.getClassLoader());
    }

    public SpoofaxModule(ClassLoader resourceClassLoader) {
        super(resourceClassLoader);
    }

    @Override protected void configure() {
        super.configure();

        parserBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {},
            new TypeLiteral<IParser<ISpoofaxInputUnit, ISpoofaxParseUnit>>() {});
        spoofaxParserBinder = MapBinder.newMapBinder(binder(), String.class, ISpoofaxParser.class);
        analyzerBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {},
            new TypeLiteral<IAnalyzer<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate>>() {});
        spoofaxAnalyzerBinder = MapBinder.newMapBinder(binder(), String.class, ISpoofaxAnalyzer.class);

        // Permit duplicate entries, on non-OSGI systems there will probably only be a single class loader.
        strategoRuntimeClassloaderBinder = Multibinder.newSetBinder(binder(), ClassLoader.class).permitDuplicates();

        bindUnit();
        bindSyntax();
        bindParsers(parserBinder, spoofaxParserBinder);
        bindAnalyzers(analyzerBinder, spoofaxAnalyzerBinder);
        bindCompletion();
        bindAction();
        bindTransformer();
        bindCategorizer();
        bindStyler();
        bindTracing();
        bindOutline();
        bindMenu();
    }


    protected void bindUnit() {
        bind(UnitService.class).in(Singleton.class);
        bind(ISpoofaxUnitService.class).to(UnitService.class);
        bind(
            new TypeLiteral<IUnitService<ISpoofaxInputUnit, ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate, ISpoofaxTransformUnit<ISpoofaxParseUnit>, ISpoofaxTransformUnit<ISpoofaxAnalyzeUnit>>>() {})
                .to(UnitService.class);
        bind(new TypeLiteral<IUnitService<?, ?, ?, ?, ?, ?>>() {}).to(UnitService.class);
        bind(IUnitService.class).to(UnitService.class);

        bind(ISpoofaxInputUnitService.class).to(UnitService.class);
        bind(new TypeLiteral<IInputUnitService<ISpoofaxInputUnit>>() {}).to(UnitService.class);
        bind(new TypeLiteral<IInputUnitService<?>>() {}).to(UnitService.class);
        bind(IInputUnitService.class).to(UnitService.class);
    }

    @Override protected void bindLanguage() {
        super.bindLanguage();

        bind(ILanguageDiscoveryService.class).to(LanguageDiscoveryService.class).in(Singleton.class);

        bind(IDialectService.class).to(DialectService.class).in(Singleton.class);
        bind(IDialectIdentifier.class).to(DialectIdentifier.class).in(Singleton.class);
        bind(IDialectProcessor.class).to(DialectProcessor.class).in(Singleton.class);
    }


    @Override protected void bindLanguagePathProviders(Multibinder<ILanguagePathProvider> binder) {
        // Bind builtin path provider before other providers such that builtin paths have preference over others.
        binder.addBinding().to(BuiltinLanguagePathProvider.class);

        super.bindLanguagePathProviders(binder);
    }

    @Override protected void bindContextFactories(MapBinder<String, IContextFactory> binder) {
        super.bindContextFactories(binder);

        binder.addBinding(IndexTaskContextFactory.name).to(IndexTaskContextFactory.class).in(Singleton.class);
        binder.addBinding(LegacyContextFactory.name).to(LegacyContextFactory.class).in(Singleton.class);
    }

    protected void bindSyntax() {
        bind(SpoofaxSyntaxService.class).in(Singleton.class);
        bind(ISpoofaxSyntaxService.class).to(SpoofaxSyntaxService.class);
        bind(new TypeLiteral<ISyntaxService<ISpoofaxInputUnit, ISpoofaxParseUnit>>() {}).to(SpoofaxSyntaxService.class);
        bind(new TypeLiteral<ISyntaxService<?, ?>>() {}).to(SpoofaxSyntaxService.class);
        bind(ISyntaxService.class).to(SpoofaxSyntaxService.class);

        bind(TermFactoryService.class).in(Singleton.class);
        bind(ITermFactoryService.class).to(TermFactoryService.class);
        languageCacheBinder.addBinding().to(TermFactoryService.class);
    }

    protected void bindParsers(MapBinder<String, IParser<ISpoofaxInputUnit, ISpoofaxParseUnit>> parserBinder,
        MapBinder<String, ISpoofaxParser> spoofaxParserBinder) {
        bind(JSGLRParseService.class).in(Singleton.class);
        parserBinder.addBinding(JSGLRParseService.name).to(JSGLRParseService.class);
        spoofaxParserBinder.addBinding(JSGLRParseService.name).to(JSGLRParseService.class);
        languageCacheBinder.addBinding().to(JSGLRParseService.class);

        bind(JSGLRParserConfiguration.class).toInstance(new JSGLRParserConfiguration());
    }

    /**
     * Overrides {@link MetaborgModule#bindAnalysis()} to provide Spoofax-specific bindings with Spoofax interfaces, and
     * to provide analyzers.
     */
    @Override protected void bindAnalysis() {
        // Analysis service
        bind(SpoofaxAnalysisService.class).in(Singleton.class);
        bind(ISpoofaxAnalysisService.class).to(SpoofaxAnalysisService.class);
        bind(new TypeLiteral<IAnalysisService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate>>() {})
            .to(SpoofaxAnalysisService.class);
        bind(new TypeLiteral<IAnalysisService<?, ?, ?>>() {}).to(SpoofaxAnalysisService.class);
        bind(IAnalysisService.class).to(SpoofaxAnalysisService.class);

        // Stratego runtime
        bind(StrategoRuntimeService.class).in(Singleton.class);
        bind(IStrategoRuntimeService.class).to(StrategoRuntimeService.class);
        languageCacheBinder.addBinding().to(StrategoRuntimeService.class);


        // Utilities
        bind(IStrategoCommon.class).to(StrategoCommon.class).in(Singleton.class);
        bind(AnalysisCommon.class).in(Singleton.class);

        // Stratego primitives
        bind(ParseFileStrategy.class).in(Singleton.class);
        bind(ParseStrategoFileStrategy.class).in(Singleton.class);

        final Multibinder<IOperatorRegistry> libraryBinder =
            Multibinder.newSetBinder(binder(), IOperatorRegistry.class);
        bindPrimitiveLibrary(libraryBinder, TaskLibrary.class);
        bindPrimitiveLibrary(libraryBinder, LegacyIndexLibrary.class);
        bindPrimitiveLibrary(libraryBinder, SpoofaxPrimitiveLibrary.class);
        bindPrimitiveLibrary(libraryBinder, SpoofaxJSGLRLibrary.class);

        final Multibinder<AbstractPrimitive> spoofaxPrimitiveLibrary =
            Multibinder.newSetBinder(binder(), AbstractPrimitive.class, Names.named("SpoofaxPrimitiveLibrary"));
        bindPrimitive(spoofaxPrimitiveLibrary, ProjectPathPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LanguageSourceLocationsPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LegacyLanguageSourceLocationsPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LanguageSourceFilesPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LanguageIncludeLocationsPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LegacyLanguageIncludeLocationsPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LanguageIncludeFilesPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, ForeignCallPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, ComponentsPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LocalPathPrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, LocalReplicatePrimitive.class);
        bindPrimitive(spoofaxPrimitiveLibrary, new DummyPrimitive("SSL_EXT_set_total_work_units", 0, 0));
        bindPrimitive(spoofaxPrimitiveLibrary, new DummyPrimitive("SSL_EXT_set_markers", 0, 1));
        bindPrimitive(spoofaxPrimitiveLibrary, new DummyPrimitive("SSL_EXT_refreshresource", 0, 1));
        bindPrimitive(spoofaxPrimitiveLibrary, new DummyPrimitive("SSL_EXT_queue_strategy", 0, 2));
        bindPrimitive(spoofaxPrimitiveLibrary, new DummyPrimitive("SSL_EXT_complete_work_unit", 0, 0));
        bindPrimitive(spoofaxPrimitiveLibrary, new DummyPrimitive("SSL_EXT_pluginpath", 0, 0));

        final Multibinder<AbstractPrimitive> spoofaxJSGLRLibrary =
            Multibinder.newSetBinder(binder(), AbstractPrimitive.class, Names.named("SpoofaxJSGLRLibrary"));
        bindPrimitive(spoofaxJSGLRLibrary, ParseFilePrimitive.class);
        bindPrimitive(spoofaxJSGLRLibrary, ParseFilePtPrimitive.class);
        bindPrimitive(spoofaxJSGLRLibrary, new DummyPrimitive("STRSGLR_open_parse_table", 0, 1));
        bindPrimitive(spoofaxJSGLRLibrary, new DummyPrimitive("STRSGLR_close_parse_table", 0, 1));
    }

    private void bindAnalyzers(
        MapBinder<String, IAnalyzer<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate>> analyzerBinder,
        MapBinder<String, ISpoofaxAnalyzer> spoofaxAnalyzerBinder) {
        bind(StrategoAnalyzer.class).in(Singleton.class);
        bind(TaskEngineAnalyzer.class).in(Singleton.class);

        analyzerBinder.addBinding(StrategoAnalyzer.name).to(StrategoAnalyzer.class);
        spoofaxAnalyzerBinder.addBinding(StrategoAnalyzer.name).to(StrategoAnalyzer.class);
        analyzerBinder.addBinding(TaskEngineAnalyzer.name).to(TaskEngineAnalyzer.class);
        spoofaxAnalyzerBinder.addBinding(TaskEngineAnalyzer.name).to(TaskEngineAnalyzer.class);
    }

    protected void bindAction() {
        bind(IActionService.class).to(ActionService.class).in(Singleton.class);
    }

    protected void bindTransformer() {
        // Analysis service
        bind(SpoofaxTransformService.class).in(Singleton.class);
        bind(ISpoofaxTransformService.class).to(SpoofaxTransformService.class);
        bind(
            new TypeLiteral<ITransformService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxTransformUnit<ISpoofaxParseUnit>, ISpoofaxTransformUnit<ISpoofaxAnalyzeUnit>>>() {})
                .to(SpoofaxTransformService.class);
        bind(new TypeLiteral<ITransformService<?, ?, ?, ?>>() {}).to(SpoofaxTransformService.class);
        bind(ITransformService.class).to(SpoofaxTransformService.class);

        // Analyzers
        bind(StrategoTransformer.class).in(Singleton.class);
        bind(IStrategoTransformer.class).to(StrategoTransformer.class);
        bind(
            new TypeLiteral<ITransformer<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxTransformUnit<ISpoofaxParseUnit>, ISpoofaxTransformUnit<ISpoofaxAnalyzeUnit>>>() {})
                .to(StrategoTransformer.class);
        bind(new TypeLiteral<ITransformer<?, ?, ?, ?>>() {}).to(StrategoTransformer.class);
        bind(ITransformer.class).to(StrategoTransformer.class);
    }

    /**
     * Overrides {@link MetaborgModule#bindBuilder()} to provide Spoofax-specific bindings with generics filled in as
     * {@link IStrategoTerm}.
     */
    @Override protected void bindBuilder() {
        bind(SpoofaxParseResultProcessor.class).in(Singleton.class);

        bind(ISpoofaxParseResultRequester.class).to(SpoofaxParseResultProcessor.class);
        bind(new TypeLiteral<IParseResultRequester<ISpoofaxInputUnit, ISpoofaxParseUnit>>() {})
            .to(SpoofaxParseResultProcessor.class);
        bind(new TypeLiteral<IParseResultRequester<?, ?>>() {}).to(SpoofaxParseResultProcessor.class);
        bind(IParseResultRequester.class).to(SpoofaxParseResultProcessor.class);

        bind(ISpoofaxParseResultUpdater.class).to(SpoofaxParseResultProcessor.class);
        bind(new TypeLiteral<IParseResultUpdater<ISpoofaxParseUnit>>() {}).to(SpoofaxParseResultProcessor.class);
        bind(new TypeLiteral<IParseResultUpdater<?>>() {}).to(SpoofaxParseResultProcessor.class);
        bind(IParseResultUpdater.class).to(SpoofaxParseResultProcessor.class);

        bind(ISpoofaxParseResultProcessor.class).to(SpoofaxParseResultProcessor.class);
        bind(new TypeLiteral<IParseResultProcessor<ISpoofaxInputUnit, ISpoofaxParseUnit>>() {})
            .to(SpoofaxParseResultProcessor.class);
        bind(new TypeLiteral<IParseResultProcessor<?, ?>>() {}).to(SpoofaxParseResultProcessor.class);
        bind(IParseResultProcessor.class).to(SpoofaxParseResultProcessor.class);


        bind(SpoofaxAnalysisResultProcessor.class).in(Singleton.class);

        bind(ISpoofaxAnalysisResultRequester.class).to(SpoofaxAnalysisResultProcessor.class);
        bind(new TypeLiteral<IAnalysisResultRequester<ISpoofaxInputUnit, ISpoofaxAnalyzeUnit>>() {})
            .to(SpoofaxAnalysisResultProcessor.class);
        bind(new TypeLiteral<IAnalysisResultRequester<?, ?>>() {}).to(SpoofaxAnalysisResultProcessor.class);
        bind(IAnalysisResultRequester.class).to(SpoofaxAnalysisResultProcessor.class);

        bind(ISpoofaxAnalysisResultUpdater.class).to(SpoofaxAnalysisResultProcessor.class);
        bind(new TypeLiteral<IAnalysisResultUpdater<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit>>() {})
            .to(SpoofaxAnalysisResultProcessor.class);
        bind(new TypeLiteral<IAnalysisResultUpdater<?, ?>>() {}).to(SpoofaxAnalysisResultProcessor.class);
        bind(IAnalysisResultUpdater.class).to(SpoofaxAnalysisResultProcessor.class);

        bind(ISpoofaxAnalysisResultProcessor.class).to(SpoofaxAnalysisResultProcessor.class);
        bind(new TypeLiteral<IAnalysisResultProcessor<ISpoofaxInputUnit, ISpoofaxParseUnit, ISpoofaxAnalyzeUnit>>() {})
            .to(SpoofaxAnalysisResultProcessor.class);
        bind(new TypeLiteral<IAnalysisResultProcessor<?, ?, ?>>() {}).to(SpoofaxAnalysisResultProcessor.class);
        bind(IAnalysisResultProcessor.class).to(SpoofaxAnalysisResultProcessor.class);


        bind(SpoofaxBuilder.class).in(Singleton.class);
        bind(ISpoofaxBuilder.class).to(SpoofaxBuilder.class);
        bind(
            new TypeLiteral<IBuilder<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate, ISpoofaxTransformUnit<?>>>() {})
                .to(SpoofaxBuilder.class);
        bind(new TypeLiteral<IBuilder<?, ?, ?, ?>>() {}).to(SpoofaxBuilder.class);
        bind(IBuilder.class).to(SpoofaxBuilder.class);

        // No scope for build output, new instance for every request.
        bind(
            new TypeLiteral<IBuildOutputInternal<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate, ISpoofaxTransformUnit<?>>>() {})
                .to(SpoofaxBuildOutput.class);
    }

    /**
     * Overrides {@link MetaborgModule#bindProcessorRunner()} to provide Spoofax-specific bindings with generics filled
     * in as {@link IStrategoTerm}.
     */
    @Override protected void bindProcessorRunner() {
        bind(SpoofaxProcessorRunner.class).in(Singleton.class);
        bind(ISpoofaxProcessorRunner.class).to(SpoofaxProcessorRunner.class);
        bind(
            new TypeLiteral<IProcessorRunner<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate, ISpoofaxTransformUnit<?>>>() {})
                .to(SpoofaxProcessorRunner.class);
        bind(new TypeLiteral<IProcessorRunner<?, ?, ?, ?>>() {}).to(SpoofaxProcessorRunner.class);
        bind(IProcessorRunner.class).to(SpoofaxProcessorRunner.class);
    }

    /**
     * Overrides {@link MetaborgModule#bindProcessor()} to provide Spoofax-specific bindings with generics filled in as
     * {@link IStrategoTerm}.
     */
    @Override protected void bindProcessor() {
        bind(SpoofaxBlockingProcessor.class).in(Singleton.class);
        bind(ISpoofaxProcessor.class).to(SpoofaxBlockingProcessor.class);
        bind(
            new TypeLiteral<IProcessor<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate, ISpoofaxTransformUnit<?>>>() {})
                .to(SpoofaxBlockingProcessor.class);
        bind(new TypeLiteral<IProcessor<?, ?, ?, ?>>() {}).to(SpoofaxBlockingProcessor.class);
        bind(IProcessor.class).to(SpoofaxBlockingProcessor.class);
    }

    protected void bindCategorizer() {
        bind(CategorizerService.class).in(Singleton.class);
        bind(ISpoofaxCategorizerService.class).to(CategorizerService.class);
        bind(new TypeLiteral<ICategorizerService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, IStrategoTerm>>() {})
            .to(CategorizerService.class);
        bind(new TypeLiteral<ICategorizerService<?, ?, ?>>() {}).to(CategorizerService.class);
        bind(ICategorizerService.class).to(CategorizerService.class);
    }

    protected void bindStyler() {
        bind(StylerService.class).in(Singleton.class);
        bind(ISpoofaxStylerService.class).to(StylerService.class);
        bind(new TypeLiteral<IStylerService<IStrategoTerm>>() {}).to(StylerService.class);
        bind(new TypeLiteral<IStylerService<?>>() {}).to(StylerService.class);
        bind(IStylerService.class).to(StylerService.class);
    }

    protected void bindTracing() {
        bind(TracingService.class).in(Singleton.class);
        bind(ISpoofaxTracingService.class).to(TracingService.class);
        bind(
            new TypeLiteral<ITracingService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxTransformUnit<?>, IStrategoTerm>>() {})
                .to(TracingService.class);
        bind(new TypeLiteral<ITracingService<?, ?, ?, ?>>() {}).to(TracingService.class);
        bind(ITracingService.class).to(TracingService.class);

        bind(TracingCommon.class).in(Singleton.class);

        bind(ResolverService.class).in(Singleton.class);
        bind(ISpoofaxResolverService.class).to(ResolverService.class);
        bind(new TypeLiteral<IResolverService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit>>() {}).to(ResolverService.class);
        bind(new TypeLiteral<IResolverService<?, ?>>() {}).to(ResolverService.class);
        bind(IResolverService.class).to(ResolverService.class);

        bind(HoverService.class).in(Singleton.class);
        bind(ISpoofaxHoverService.class).to(HoverService.class);
        bind(new TypeLiteral<IHoverService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit>>() {}).to(HoverService.class);
        bind(new TypeLiteral<IHoverService<?, ?>>() {}).to(HoverService.class);
        bind(IHoverService.class).to(HoverService.class);
    }

    protected void bindCompletion() {
        bind(JSGLRCompletionService.class).in(Singleton.class);
        bind(new TypeLiteral<ICompletionService<ISpoofaxParseUnit>>() {}).to(JSGLRCompletionService.class);
        bind(new TypeLiteral<ICompletionService<?>>() {}).to(JSGLRCompletionService.class);
        bind(ICompletionService.class).to(JSGLRCompletionService.class);
    }

    protected void bindOutline() {
        bind(OutlineService.class).in(Singleton.class);
        bind(ISpoofaxOutlineService.class).to(OutlineService.class);
        bind(new TypeLiteral<IOutlineService<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit>>() {}).to(OutlineService.class);
        bind(new TypeLiteral<IOutlineService<?, ?>>() {}).to(OutlineService.class);
        bind(IOutlineService.class).to(OutlineService.class);
    }

    protected void bindMenu() {
        bind(IMenuService.class).to(MenuService.class).in(Singleton.class);
    }


    protected static void bindPrimitive(Multibinder<AbstractPrimitive> binder, AbstractPrimitive primitive) {
        binder.addBinding().toInstance(primitive);
    }

    protected static void bindPrimitive(Multibinder<AbstractPrimitive> binder,
        Class<? extends AbstractPrimitive> primitive) {
        binder.addBinding().to(primitive).in(Singleton.class);
    }

    protected static void bindPrimitiveLibrary(Multibinder<IOperatorRegistry> binder,
        Class<? extends IOperatorRegistry> primitiveLibrary) {
        binder.addBinding().to(primitiveLibrary).in(Singleton.class);
    }
}
