// Generated from Extension.g4 by ANTLR 4.4
package parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExtensionParser}.
 */
public interface ExtensionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(@NotNull ExtensionParser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(@NotNull ExtensionParser.TypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code baseBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterBaseBoolExp(@NotNull ExtensionParser.BaseBoolExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code baseBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitBaseBoolExp(@NotNull ExtensionParser.BaseBoolExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#unmatchedIfStatement}.
	 * @param ctx the parse tree
	 */
	void enterUnmatchedIfStatement(@NotNull ExtensionParser.UnmatchedIfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#unmatchedIfStatement}.
	 * @param ctx the parse tree
	 */
	void exitUnmatchedIfStatement(@NotNull ExtensionParser.UnmatchedIfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void enterBinArithmeticExp(@NotNull ExtensionParser.BinArithmeticExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void exitBinArithmeticExp(@NotNull ExtensionParser.BinArithmeticExpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterVarBoolExp(@NotNull ExtensionParser.VarBoolExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitVarBoolExp(@NotNull ExtensionParser.VarBoolExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#matchedIfStatement}.
	 * @param ctx the parse tree
	 */
	void enterMatchedIfStatement(@NotNull ExtensionParser.MatchedIfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#matchedIfStatement}.
	 * @param ctx the parse tree
	 */
	void exitMatchedIfStatement(@NotNull ExtensionParser.MatchedIfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterBinBoolExp(@NotNull ExtensionParser.BinBoolExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitBinBoolExp(@NotNull ExtensionParser.BinBoolExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#deletion}.
	 * @param ctx the parse tree
	 */
	void enterDeletion(@NotNull ExtensionParser.DeletionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#deletion}.
	 * @param ctx the parse tree
	 */
	void exitDeletion(@NotNull ExtensionParser.DeletionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull ExtensionParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull ExtensionParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterNegBoolExp(@NotNull ExtensionParser.NegBoolExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitNegBoolExp(@NotNull ExtensionParser.NegBoolExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull ExtensionParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull ExtensionParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(@NotNull ExtensionParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(@NotNull ExtensionParser.ExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(@NotNull ExtensionParser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(@NotNull ExtensionParser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(@NotNull ExtensionParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(@NotNull ExtensionParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterValBoolExp(@NotNull ExtensionParser.ValBoolExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valBoolExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitValBoolExp(@NotNull ExtensionParser.ValBoolExpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithmeticComparisonExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticComparisonExp(@NotNull ExtensionParser.ArithmeticComparisonExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithmeticComparisonExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticComparisonExp(@NotNull ExtensionParser.ArithmeticComparisonExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(@NotNull ExtensionParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(@NotNull ExtensionParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#actualParameters}.
	 * @param ctx the parse tree
	 */
	void enterActualParameters(@NotNull ExtensionParser.ActualParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#actualParameters}.
	 * @param ctx the parse tree
	 */
	void exitActualParameters(@NotNull ExtensionParser.ActualParametersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void enterVarArithmeticExp(@NotNull ExtensionParser.VarArithmeticExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void exitVarArithmeticExp(@NotNull ExtensionParser.VarArithmeticExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(@NotNull ExtensionParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(@NotNull ExtensionParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#print}.
	 * @param ctx the parse tree
	 */
	void enterPrint(@NotNull ExtensionParser.PrintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#print}.
	 * @param ctx the parse tree
	 */
	void exitPrint(@NotNull ExtensionParser.PrintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void enterSimpleStatement(@NotNull ExtensionParser.SimpleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void exitSimpleStatement(@NotNull ExtensionParser.SimpleStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolComparisonExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterBoolComparisonExp(@NotNull ExtensionParser.BoolComparisonExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolComparisonExp}
	 * labeled alternative in {@link ExtensionParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitBoolComparisonExp(@NotNull ExtensionParser.BoolComparisonExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(@NotNull ExtensionParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(@NotNull ExtensionParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#actualParameter}.
	 * @param ctx the parse tree
	 */
	void enterActualParameter(@NotNull ExtensionParser.ActualParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#actualParameter}.
	 * @param ctx the parse tree
	 */
	void exitActualParameter(@NotNull ExtensionParser.ActualParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(@NotNull ExtensionParser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(@NotNull ExtensionParser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void enterNegArithmeticExp(@NotNull ExtensionParser.NegArithmeticExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void exitNegArithmeticExp(@NotNull ExtensionParser.NegArithmeticExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExtensionParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(@NotNull ExtensionParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExtensionParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(@NotNull ExtensionParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void enterValArithmeticExp(@NotNull ExtensionParser.ValArithmeticExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void exitValArithmeticExp(@NotNull ExtensionParser.ValArithmeticExpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code baseArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void enterBaseArithmeticExp(@NotNull ExtensionParser.BaseArithmeticExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code baseArithmeticExp}
	 * labeled alternative in {@link ExtensionParser#arithmExp}.
	 * @param ctx the parse tree
	 */
	void exitBaseArithmeticExp(@NotNull ExtensionParser.BaseArithmeticExpContext ctx);
}