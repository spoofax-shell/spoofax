package org.metaborg.spoofax.eclipse.transform;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.metaborg.spoofax.core.analysis.IAnalysisService;
import org.metaborg.spoofax.core.language.ILanguageIdentifierService;
import org.metaborg.spoofax.core.syntax.ISyntaxService;
import org.metaborg.spoofax.core.transform.ITransformer;
import org.metaborg.spoofax.eclipse.SpoofaxPlugin;
import org.metaborg.spoofax.eclipse.editor.LatestEditorListener;
import org.metaborg.spoofax.eclipse.editor.SpoofaxEditor;
import org.metaborg.spoofax.eclipse.resource.IEclipseResourceService;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class TransformHandler extends AbstractHandler {
    private final IEclipseResourceService resourceService;
    private final ILanguageIdentifierService langaugeIdentifierService;
    private final ISyntaxService<IStrategoTerm> syntaxService;
    private final IAnalysisService<IStrategoTerm, IStrategoTerm> analysisService;
    private final ITransformer<IStrategoTerm, IStrategoTerm, IStrategoTerm> transformer;
    private final LatestEditorListener latestEditorListener;


    public TransformHandler() {
        super();
        
        final Injector injector = SpoofaxPlugin.injector();

        this.resourceService = injector.getInstance(IEclipseResourceService.class);
        this.langaugeIdentifierService = injector.getInstance(ILanguageIdentifierService.class);
        this.syntaxService = injector.getInstance(Key.get(new TypeLiteral<ISyntaxService<IStrategoTerm>>() {}));
        this.analysisService =
            injector.getInstance(Key.get(new TypeLiteral<IAnalysisService<IStrategoTerm, IStrategoTerm>>() {}));
        this.transformer =
            injector.getInstance(Key
                .get(new TypeLiteral<ITransformer<IStrategoTerm, IStrategoTerm, IStrategoTerm>>() {}));
        this.latestEditorListener = injector.getInstance(LatestEditorListener.class);
    }


    @Override public Object execute(ExecutionEvent event) throws ExecutionException {
        final SpoofaxEditor latestEditor = latestEditorListener.latestActive();
        final String actionName = event.getParameter("action-name");
        final Job transformJob =
            new TransformJob(resourceService, langaugeIdentifierService, syntaxService, analysisService, transformer,
                latestEditor, actionName);
        transformJob.schedule();

        return null;
    }
}
