package model;

import parser.ExtensionBaseVisitor;
import parser.ExtensionParser.*;

public class ExtensionDeclarationVisitor extends ExtensionBaseVisitor<Environment> {
	
	Environment globalEnvironment = new Environment();
	
	
	//The following functions alter the environment
	
	@Override
	public Environment visitVariableDeclaration(VariableDeclarationContext ctx) {
		String id = ctx.ID().getText();
		globalEnvironment.addVariableDeclaration(id);
		super.visitVariableDeclaration(ctx);
		return globalEnvironment;
	}
	
	@Override
	public Environment visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		String id = ctx.ID().getText();
		globalEnvironment.addFunctionDeclaration(id);
		super.visitFunctionDeclaration(ctx);
		return globalEnvironment;
	}
	
	
	//The following functions do not alter the environment

	@Override
	public Environment visitTypeDeclaration(TypeDeclarationContext ctx) {
		super.visitTypeDeclaration(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitBaseBoolExp(BaseBoolExpContext ctx) {
		super.visitBaseBoolExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitUnmatchedIfStatement(UnmatchedIfStatementContext ctx) {
		super.visitUnmatchedIfStatement(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitBinArithmeticExp(BinArithmeticExpContext ctx) {
		super.visitBinArithmeticExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitVarBoolExp(VarBoolExpContext ctx) {
		super.visitVarBoolExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitMatchedIfStatement(MatchedIfStatementContext ctx) {
		super.visitMatchedIfStatement(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitBinBoolExp(BinBoolExpContext ctx) {
		super.visitBinBoolExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitDeletion(DeletionContext ctx) {
		super.visitDeletion(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitStatement(StatementContext ctx) {
		super.visitStatement(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitNegBoolExp(NegBoolExpContext ctx) {
		super.visitNegBoolExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitBlock(BlockContext ctx) {
		super.visitBlock(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitExp(ExpContext ctx) {
		super.visitExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitFormalParameter(FormalParameterContext ctx) {
		super.visitFormalParameter(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitAssignment(AssignmentContext ctx) {
		super.visitAssignment(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitValBoolExp(ValBoolExpContext ctx) {
		super.visitValBoolExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitArithmeticComparisonExp(ArithmeticComparisonExpContext ctx) {
		super.visitArithmeticComparisonExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitIfStatement(IfStatementContext ctx) {
		super.visitIfStatement(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitActualParameters(ActualParametersContext ctx) {
		super.visitActualParameters(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitVarArithmeticExp(VarArithmeticExpContext ctx) {
		super.visitVarArithmeticExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitPrint(PrintContext ctx) {
		super.visitPrint(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitSimpleStatement(SimpleStatementContext ctx) {
		super.visitSimpleStatement(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitBoolComparisonExp(BoolComparisonExpContext ctx) {
		super.visitBoolComparisonExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitFunctionCall(FunctionCallContext ctx) {
		super.visitFunctionCall(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitActualParameter(ActualParameterContext ctx) {
		super.visitActualParameter(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitFormalParameters(FormalParametersContext ctx) {
		super.visitFormalParameters(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitNegArithmeticExp(NegArithmeticExpContext ctx) {
		super.visitNegArithmeticExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitValArithmeticExp(ValArithmeticExpContext ctx) {
		super.visitValArithmeticExp(ctx);
		return globalEnvironment;
	}

	@Override
	public Environment visitBaseArithmeticExp(BaseArithmeticExpContext ctx) {
		super.visitBaseArithmeticExp(ctx);
		return globalEnvironment;
	}
	
	

}