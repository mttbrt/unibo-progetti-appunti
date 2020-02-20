// Generated from ComplexStaticAnalysis.g4 by ANTLR 4.4
package parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ComplexStaticAnalysisParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ComplexStaticAnalysisVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code relationalFactor}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalFactor(@NotNull ComplexStaticAnalysisParser.RelationalFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variableValue}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableValue(@NotNull ComplexStaticAnalysisParser.VariableValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(@NotNull ComplexStaticAnalysisParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#functioncall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctioncall(@NotNull ComplexStaticAnalysisParser.FunctioncallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(@NotNull ComplexStaticAnalysisParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variableDeclaration}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(@NotNull ComplexStaticAnalysisParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(@NotNull ComplexStaticAnalysisParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#deletion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeletion(@NotNull ComplexStaticAnalysisParser.DeletionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(@NotNull ComplexStaticAnalysisParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull ComplexStaticAnalysisParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#ifthenelse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfthenelse(@NotNull ComplexStaticAnalysisParser.IfthenelseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull ComplexStaticAnalysisParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(@NotNull ComplexStaticAnalysisParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code integerValue}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerValue(@NotNull ComplexStaticAnalysisParser.IntegerValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanValue}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanValue(@NotNull ComplexStaticAnalysisParser.BooleanValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanFactor}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanFactor(@NotNull ComplexStaticAnalysisParser.BooleanFactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ComplexStaticAnalysisParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(@NotNull ComplexStaticAnalysisParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionDeclaration}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(@NotNull ComplexStaticAnalysisParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expValue}
	 * labeled alternative in {@link ComplexStaticAnalysisParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpValue(@NotNull ComplexStaticAnalysisParser.ExpValueContext ctx);
}