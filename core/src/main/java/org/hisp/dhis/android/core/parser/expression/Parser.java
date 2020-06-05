package org.hisp.dhis.android.core.parser.expression;

/*
 * Copyright (c) 2004-2020, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.hisp.dhis.antlr.ParserErrorListener;
import org.hisp.dhis.antlr.cache.Cache;
import org.hisp.dhis.antlr.cache.SimpleCacheBuilder;
import org.hisp.dhis.parser.expression.antlr.ExpressionBaseListener;
import org.hisp.dhis.parser.expression.antlr.ExpressionBaseVisitor;
import org.hisp.dhis.parser.expression.antlr.ExpressionLexer;
import org.hisp.dhis.parser.expression.antlr.ExpressionParser;

import java.util.concurrent.TimeUnit;

/**
 * Parses an expression parser using the ANTLR4 parser, and traverses the
 * parsed tree using the ANTLR4 visitor or listener patterns.
 *
 * @author Jim Grace
 */
public class Parser {

    private static Cache<ParseTree> EXPRESSION_PARSE_TREES_CACHE = null;

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Parses an expression and visits the parsed nodes using the ANTLR4
     * visitor pattern.
     *
     * @param expr    the expression to parse and visit
     * @param visitor the visitor instance
     * @return the visitor value
     */
    public static Object visit(String expr, ExpressionBaseVisitor<Object> visitor) {
        return visit(expr, visitor, true);
    }

    public static Object visit(String expr, ExpressionBaseVisitor<Object> visitor, Boolean cache) {
        ParseTree parseTree = getParseTree(expr, cache);

        return visitor.visit(parseTree);
    }

    /**
     * Parses an expression and listens while ANTLR4 walks through the parsed
     * nodes using the listener pattern.
     *
     * @param expr     the expression to parse and listen to
     * @param listener the listener instance
     */
    public static void listen(String expr, ExpressionBaseListener listener) {
        listen(expr, listener, true);
    }

    public static void listen(String expr, ExpressionBaseListener listener, Boolean cache) {
        ParseTree parseTree = getParseTree(expr, cache);

        ParseTreeWalker walker = new ParseTreeWalker();

        walker.walk(listener, parseTree);
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Gets the ANTLR4 parse tree for the given expression string,
     * from the cache if possible.
     *
     * @param expr the expression to parse.
     * @return the ANTLR4 parse tree.
     */
    private static ParseTree getParseTree(String expr, Boolean cache) {
        if (cache) {
            return getExpressionParseTreesCache().get(expr, Parser::createParseTree).orNull();
        } else {
            return createParseTree(expr);
        }
    }

    /**
     * Parses an expression into an ANTLR4 ParseTree.
     *
     * @param expr the expression text to parse.
     * @return the ANTLR4 parse tree.
     */
    private static ParseTree createParseTree(String expr) {
        ParserErrorListener errorListener = new ParserErrorListener(); // Custom error listener.

        CharStream input = CharStreams.fromString(expr); // Form an ANTLR lexer input stream.

        ExpressionLexer lexer = new ExpressionLexer(input); // Create a lexer for the input.

        lexer.removeErrorListeners(); // Remove default lexer error listener (prints to console).
        lexer.addErrorListener(errorListener); // Add custom error listener to throw any errors.

        CommonTokenStream tokens = new CommonTokenStream(lexer); // Parse the input into a token stream.

        ExpressionParser parser = new ExpressionParser(tokens); // Create a parser for the token stream.

        parser.removeErrorListeners(); // Remove the default parser error listener (prints to console).
        parser.addErrorListener(errorListener); // Add custom error listener to throw any errors.

        return parser.expression(); // Parse the expression and return the parse tree.
    }

    private static Cache<ParseTree> getExpressionParseTreesCache() {
        if (EXPRESSION_PARSE_TREES_CACHE == null) {
            EXPRESSION_PARSE_TREES_CACHE = new SimpleCacheBuilder<ParseTree>().forRegion("expressionParseTrees")
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .withInitialCapacity(10000)
                    .withMaximumSize(50000)
                    .build();
        }
        return EXPRESSION_PARSE_TREES_CACHE;
    }
}
