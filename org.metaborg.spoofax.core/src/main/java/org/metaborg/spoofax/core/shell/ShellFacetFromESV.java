package org.metaborg.spoofax.core.shell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.metaborg.spoofax.core.esv.ESVReader;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.google.common.collect.Maps;

public class ShellFacetFromESV {
    private static final ILogger logger = LoggerUtils.logger(ShellFacetFromESV.class);

    public static @Nullable ShellFacet create(IStrategoAppl esv) {
        IStrategoAppl shellTerm = ESVReader.findTerm(esv, "Shell");
        if (shellTerm == null) {
            return null;
        }
        Map<String, ReplCommandDefinition> commands = commandDefinitions(shellTerm);
        String commandPrefix = commandPrefix(shellTerm);
        String evaluationMethod = evaluationMethod(shellTerm);
        return new ShellFacet(commands, commandPrefix, evaluationMethod);
    }

    private static Map<String, ReplCommandDefinition> commandDefinitions(IStrategoAppl shellTerm) {
        final List<IStrategoAppl> definitionTerms =
            ESVReader.collectTerms(shellTerm, "CommandDefinition");
        final HashMap<String, ReplCommandDefinition> commandNamesToDefinitions = Maps.newHashMap();

        for (IStrategoAppl defTerm : definitionTerms) {
            addReplCommandDefinition(commandNamesToDefinitions, defTerm);
        }
        return commandNamesToDefinitions;
    }

    private static void addReplCommandDefinition(
                                                 Map<String, ReplCommandDefinition> commandNamesToDefinitions,
                                                 IStrategoAppl defTerm) {
        String commandName = ESVReader.termContents(defTerm.getSubterm(0));
        String commandDescription =
            ensureSingleProperty(defTerm, "CommandDescription", "command descriptions");
        String commandStrategyName =
            ensureSingleProperty(defTerm, "CommandStrategy", "command strategies");
        if (commandStrategyName == null) {
            logger.error("Missing strategy definition for command \"{}\", command ignored."
                         + " Add a strategy using \"strategy = <strategy-name>\"", commandName);
            return;
        }

        commandNamesToDefinitions
            .put(commandName, new ReplCommandDefinition(commandStrategyName, commandDescription));
    }

    private static @Nullable String ensureSingleProperty(IStrategoAppl defTerm, String propertyName,
                                                         String propertyDescription) {
        String termContent = null;
        List<IStrategoAppl> propertyTerms = ESVReader.collectTerms(defTerm, propertyName);
        if (!propertyTerms.isEmpty()) {
            termContent = ESVReader.termContents(propertyTerms.get(0));
            if (propertyTerms.size() > 1) {
                logger.error("Multiple {} found. Using the first: \"{}\"", propertyDescription,
                             termContent);
            }
        }
        return termContent;
    }

    private static @Nullable String commandPrefix(IStrategoAppl term) {
        return ESVReader.getProperty(term, "CommandPrefix");
    }

    private static @Nullable String evaluationMethod(IStrategoAppl term) {
        return ESVReader.getProperty(term, "EvaluationMethod");
    }
}