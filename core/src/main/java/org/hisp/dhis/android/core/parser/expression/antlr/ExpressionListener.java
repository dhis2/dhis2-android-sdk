// Generated from org/hisp/dhis/parser/expression/antlr/Expression.g4 by ANTLR 4.7.2
package org.hisp.dhis.android.core.parser.expression.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionParser}.
 */
public interface ExpressionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExpressionParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ExpressionParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ExpressionParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#variableName}.
	 * @param ctx the parse tree
	 */
	void enterVariableName(ExpressionParser.VariableNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#variableName}.
	 * @param ctx the parse tree
	 */
	void exitVariableName(ExpressionParser.VariableNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(ExpressionParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(ExpressionParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(ExpressionParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(ExpressionParser.BooleanLiteralContext ctx);
}