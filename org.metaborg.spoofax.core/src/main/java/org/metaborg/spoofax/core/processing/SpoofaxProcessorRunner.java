package org.metaborg.spoofax.core.processing;

import javax.annotation.Nullable;

import org.metaborg.core.build.BuildInput;
import org.metaborg.core.language.ILanguageService;
import org.metaborg.core.processing.ICancellationToken;
import org.metaborg.core.processing.IProgressReporter;
import org.metaborg.core.processing.ITask;
import org.metaborg.core.processing.ProcessorRunner;
import org.metaborg.spoofax.core.build.ISpoofaxBuildOutput;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnitUpdate;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxTransformUnit;

import com.google.inject.Inject;

/**
 * Typedef class for {@link ProcessorRunner} with Spoofax interfaces.
 */
public class SpoofaxProcessorRunner
    extends ProcessorRunner<ISpoofaxParseUnit, ISpoofaxAnalyzeUnit, ISpoofaxAnalyzeUnitUpdate, ISpoofaxTransformUnit<?>>
    implements ISpoofaxProcessorRunner {
    @Inject public SpoofaxProcessorRunner(ISpoofaxProcessor processor, ILanguageService languageService) {
        super(processor, languageService);
    }


    @SuppressWarnings("unchecked") public ITask<ISpoofaxBuildOutput> build(BuildInput input,
        @Nullable IProgressReporter progressReporter, @Nullable ICancellationToken cancellationToken) {
        return (ITask<ISpoofaxBuildOutput>) super.build(input, progressReporter, cancellationToken);
    }
}
