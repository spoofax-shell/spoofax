package org.metaborg.spoofax.core.shell;

import javax.annotation.Nullable;

public class ReplCommandDefinition {
    private final String commandStrategyName;
    private final String commandDescription;

    public ReplCommandDefinition(String commandStrategyName, @Nullable String commandDescription) {
        this.commandStrategyName = commandStrategyName;
        this.commandDescription = commandDescription;
    }

    /**
     * @return The name of the Stratego strategy of the command.
     */
    public String getCommandStrategyName() {
        return commandStrategyName;
    }

    /**
     * @return The description of the command.
     */
    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String toString() {
        return "ReplCommandDefinition [commandStrategyName=" + commandStrategyName
               + ", commandDescription=" + commandDescription + "]";
    }
}
