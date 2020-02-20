// Generated from Extension.g4 by ANTLR 4.4
package parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExtensionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExtensionVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(@NotNull ExtensionParser.TypeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code baseBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseBoolExp(@NotNull ExtensionParser.BaseBoolExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#unmatchedIfStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnmatchedIfStatement(@NotNull ExtensionParser.UnmatchedIfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinArithmeticExp(@NotNull ExtensionParser.BinArithmeticExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarBoolExp(@NotNull ExtensionParser.VarBoolExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#matchedIfStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchedIfStatement(@NotNull ExtensionParser.MatchedIfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinBoolExp(@NotNull ExtensionParser.BinBoolExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#deletion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeletion(@NotNull ExtensionParser.DeletionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull ExtensionParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegBoolExp(@NotNull ExtensionParser.NegBoolExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull ExtensionParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(@NotNull ExtensionParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#formalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameter(@NotNull ExtensionParser.FormalParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(@NotNull ExtensionParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValBoolExp(@NotNull ExtensionParser.ValBoolExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithmeticComparisonExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticComparisonExp(@NotNull ExtensionParser.ArithmeticComparisonExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(@NotNull ExtensionParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#actualParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualParameters(@NotNull ExtensionParser.ActualParametersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarArithmeticExp(@NotNull ExtensionParser.VarArithmeticExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(@NotNull ExtensionParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(@NotNull ExtensionParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#simpleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleStatement(@NotNull ExtensionParser.SimpleStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolComparisonExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolComparisonExp(@NotNull ExtensionParser.BoolComparisonExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(@NotNull ExtensionParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#actualParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualParameter(@NotNull ExtensionParser.ActualParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(@NotNull ExtensionParser.FormalParametersContext ctx);
	/**
	 * Visit a parse tree produced by the {@code negArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegArithmeticExp(@NotNull ExtensionParser.NegArithmeticExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtensionParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(@NotNull ExtensionParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code valArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValArithmeticExp(@NotNull ExtensionParser.ValArithmeticExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code baseArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseArithmeticExp(@NotNull ExtensionParser.BaseArithmeticExpContext ctx);
}