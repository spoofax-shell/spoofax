package org.metaborg.core.project;

import javax.annotation.Nullable;

/**
 * Service for getting a language specification.
 */
public interface ILanguageSpecService {

    /**
     * Gets a language specification from the specified project.
     *
     * @param project The project; or <code>null</code>.
     * @return The language specification; or <code>null</code>
     * when the project is not a language specification project or <code>null</code>.
     */
    @Nullable
    ILanguageSpec get(@Nullable IProject project);

}
