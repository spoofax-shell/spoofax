package org.metaborg.core.config;

/**
 * Language-specific single-file export.
 */
public class LangFileExport implements IExportConfig {
    /**
     * Name of the language for which source files are exported.
     */
    public final String language;

    /**
     * File which is exported, relative to the location of the language component that exports sources.
     */
    public final String file;


    public LangFileExport(String languageName, String file) {
        this.language = languageName;
        this.file = file;
    }


    @Override public void accept(IExportVisitor visitor) {
        visitor.visit(this);
    }
}