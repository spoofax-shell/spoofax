package org.metaborg.spoofax.core.menu;

import java.util.Collection;

import javax.annotation.Nullable;

import org.metaborg.core.language.LanguageIdentifier;
import org.metaborg.core.menu.IMenu;
import org.metaborg.core.menu.Menu;
import org.metaborg.core.menu.Separator;
import org.metaborg.spoofax.core.esv.ESVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.Lists;

public class MenusFacetFromESV {
    private static final Logger logger = LoggerFactory.getLogger(MenusFacetFromESV.class);


    public static @Nullable MenuFacet create(IStrategoAppl esv, LanguageIdentifier inputLanguageId) {
        final Iterable<IStrategoAppl> menuTerms = ESVReader.collectTerms(esv, "ToolbarMenu");
        final Collection<IMenu> menus = Lists.newLinkedList();
        for(IStrategoAppl menuTerm : menuTerms) {
            final IMenu submenu = menu(menuTerm, new StrategoTransformActionFlags(), inputLanguageId);
            menus.add(submenu);
        }
        if(menus.isEmpty()) {
            return null;
        }
        return new MenuFacet(menus);
    }

    private static Menu menu(IStrategoTerm menuTerm, StrategoTransformActionFlags flags, LanguageIdentifier inputLanguageId) {
        final String name = name(menuTerm.getSubterm(0));
        final StrategoTransformActionFlags extraFlags = flags(menuTerm.getSubterm(1));
        final StrategoTransformActionFlags mergedFlags = StrategoTransformActionFlags.merge(flags, extraFlags);
        final Iterable<IStrategoTerm> items = menuTerm.getSubterm(2);
        final Menu menu = new Menu(name);
        for(IStrategoTerm item : items) {
            final String constructor = Tools.constructorName(item);
            if(constructor == null) {
                logger.error("Could not interpret menu item from term {}", item);
                continue;
            }
            switch(constructor) {
                case "Submenu":
                    final Menu submenu = menu(item, mergedFlags, inputLanguageId);
                    menu.add(submenu);
                    break;
                case "Action":
                    final StrategoTransformAction action = action(item, mergedFlags, inputLanguageId);
                    menu.add(action);
                    break;
                case "Separator":
                    final Separator separator = new Separator();
                    menu.add(separator);
                    break;
                default:
                    logger.warn("Unhandled menu item term {}", item);
                    break;
            }
        }
        return menu;
    }

    private static String name(IStrategoTerm nameTerm) {
        // For some reason, names in menus have different shapes of names that need to be handled:
        // * ToolbarMenu: Label(String("\"Name\""))
        // * Submenu: String("\"Name\"")
        // * Action: String("\"Name\"")
        final IStrategoTerm term;
        if(Tools.hasConstructor((IStrategoAppl) nameTerm, "Label")) {
            term = nameTerm.getSubterm(0);
        } else {
            term = nameTerm;
        }
        return ESVReader.termContents(term);
    }

    private static StrategoTransformAction action(IStrategoTerm action, StrategoTransformActionFlags flags, LanguageIdentifier inputLanguageId) {
        final String name = name(action.getSubterm(0));
        final String stategy = Tools.asJavaString(action.getSubterm(1).getSubterm(0));
        final StrategoTransformActionFlags extraFlags = flags(action.getSubterm(2));
        final StrategoTransformActionFlags mergedFlags = StrategoTransformActionFlags.merge(flags, extraFlags);
        return new StrategoTransformAction(name, inputLanguageId, null, stategy, mergedFlags);
    }

    private static StrategoTransformActionFlags flags(Iterable<IStrategoTerm> flagTerms) {
        final StrategoTransformActionFlags flags = new StrategoTransformActionFlags();
        for(IStrategoTerm flagTerm : flagTerms) {
            final String constructor = Tools.constructorName(flagTerm);
            if(constructor == null) {
                logger.error("Could not interpret flag from term {}", flagTerm);
                continue;
            }
            switch(constructor) {
                case "Source":
                    flags.parsed = true;
                    break;
                case "Meta":
                    flags.meta = true;
                    break;
                case "OpenEditor":
                    flags.openEditor = true;
                    break;
                case "RealTime":
                    flags.realtime = true;
                    break;
                default:
                    logger.warn("Unhandled flag term {}", flagTerm);
                    break;
            }
        }
        return flags;
    }
}