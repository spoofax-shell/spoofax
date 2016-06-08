package org.metaborg.spoofax.core.shell;

import java.util.Map;

import javax.annotation.Nullable;

import org.metaborg.core.language.IFacet;

/**
 * Facet for the interactive shell of a language.
 */
public class ShellFacet implements IFacet {
    private final Map<String, ReplCommandDefinition> commandNameToDefinition;
    private final String commandPrefix;
    private final String evaluationMethod;
    private final String shellStartSymbol;

    public ShellFacet(Map<String, ReplCommandDefinition> commands, @Nullable String commandPrefix,
                      @Nullable String evaluationMethod, String shellStartSymbol) {
        this.commandNameToDefinition = commands;
        this.commandPrefix = commandPrefix;
        this.evaluationMethod = evaluationMethod;
        this.shellStartSymbol = shellStartSymbol;
    }

    /**
     * @return The {@link ReplCommandDefinition}s, with the command names as their key.
     */
    public Map<String, ReplCommandDefinition> getCommandDefinitions() {
        return commandNameToDefinition;
    }

    /**
     * @return The prefix for all commands entered in the REPL.
     */
    public @Nullable String getCommandPrefix() {
        return commandPrefix;
    }

    /**
     * @return The evaluation method to use.
     */
    public @Nullable String getEvaluationMethod() {
        return evaluationMethod;
    }

    /**
     * @return The start symbol for shell-specific language syntax.
     */
    public String getShellStartSymbol() {
        return shellStartSymbol;
    }

    @Override
    public String toString() {
        return "ShellFacet [commandNameToDefinition=" + commandNameToDefinition + ", commandPrefix="
               + commandPrefix + ", evaluationMethod=" + evaluationMethod + ", shellStartSymbol="
               + shellStartSymbol + "]";
    }

}
