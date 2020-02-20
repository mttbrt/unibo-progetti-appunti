package model;

import java.util.ArrayList;
import java.util.List;

import environment.Type;
import parser.ComplexStaticAnalysisBaseVisitor;
import parser.ComplexStaticAnalysisParser.AssignmentContext;
import parser.ComplexStaticAnalysisParser.BlockContext;
import parser.ComplexStaticAnalysisParser.BooleanFactorContext;
import parser.ComplexStaticAnalysisParser.BooleanValueContext;
import parser.ComplexStaticAnalysisParser.DeletionContext;
import parser.ComplexStaticAnalysisParser.ExpContext;
import parser.ComplexStaticAnalysisParser.ExpValueContext;
import parser.ComplexStaticAnalysisParser.FunctionDeclarationContext;
import parser.ComplexStaticAnalysisParser.FunctioncallContext;
import parser.ComplexStaticAnalysisParser.IfthenelseContext;
import parser.ComplexStaticAnalysisParser.IntegerValueContext;
import parser.ComplexStaticAnalysisParser.ParameterContext;
import parser.ComplexStaticAnalysisParser.PrintContext;
import parser.ComplexStaticAnalysisParser.RelationalFactorContext;
import parser.ComplexStaticAnalysisParser.StatementContext;
import parser.ComplexStaticAnalysisParser.TermContext;
import parser.ComplexStaticAnalysisParser.VariableDeclarationContext;
import parser.ComplexStaticAnalysisParser.VariableValueContext;
import util.BooleanOperator;
import util.Operator;
import util.RelationalOperator;

public class ComplexStaticAnalysisVisitorImpl extends ComplexStaticAnalysisBaseVisitor<Node> {
	
	

	@Override
	public Node visitRelationalFactor(RelationalFactorContext ctx) {
		Value left = (Value) visit(ctx.left);
		RelationalOperator rop = null;
		Value right = null;
		
		if(ctx.rop != null) {
			String symbol = ctx.rop.getText();
			if(symbol.equals("==")) {
				rop = RelationalOperator.EQUALS;
			} else if (symbol.equals(">")) {
				rop = RelationalOperator.GREATER;
			} else if (symbol.equals("<")) {
				rop = RelationalOperator.LESS;
			} else if (symbol.equals(">=")) {
				rop = RelationalOperator.GEQ;
			} else if (symbol.equals("<=")) {
				rop = RelationalOperator.LEQ;
			} else if (symbol.equals("!=")) {
				rop = RelationalOperator.NEQ;
			}
				
			right = (Value) visit(ctx.right);
		}
		
		return new RelationalFactor(left, rop, right);
	}

	@Override
	public Node visitBooleanFactor(BooleanFactorContext ctx) {
		Value left = (Value) visit(ctx.left);
		BooleanOperator bop = null;
		Value right = null;
		
		if(ctx.bop != null) {
			String symbol = ctx.bop.getText();
			if(symbol.equals("&&"))
				bop = BooleanOperator.AND;
			else if (symbol.equals("||"))
				bop = BooleanOperator.OR;
				
			right = (Value) visit(ctx.right);
		}
		
		return new BooleanFactor(left, bop, right);
	}

	@Override
	public Node visitVariableValue(VariableValueContext ctx) {
		String id = ctx.getChild(0).getText();
		return new VariableValue(id);
	}

	@Override
	public Node visitAssignment(AssignmentContext ctx) {
		String id = ctx.ID().getText();
		Exp expression = (Exp) visitExp(ctx.exp());
		
		return new Assignment(id, expression);
	}

	@Override
	public Node visitFunctioncall(FunctioncallContext ctx) {
		String id = ctx.ID().getText();
		List<Exp> arguments = new ArrayList<Exp>();
		for(ExpContext ec : ctx.exp())
			arguments.add((Exp) visitExp(ec));
		
		return new FunctionCall(id, arguments);
	}

	@Override
	public Node visitVariableDeclaration(VariableDeclarationContext ctx) {
		Type type = ctx.type().getText().equals("int") ? Type.INT : Type.BOOL;
		String id = ctx.ID().getText();
		Exp expression = (Exp) visitExp(ctx.exp());
		
		return new VariableDeclaration(type, id, expression);
	}

	@Override
	public Node visitPrint(PrintContext ctx) {
		Exp expression = (Exp) visitExp(ctx.exp());
		
		return new Print(expression);
	}

	@Override
	public Node visitDeletion(DeletionContext ctx) {
		String id = ctx.ID().getText();
		
		return new Deletion(id);
	}

	@Override
	public Node visitParameter(ParameterContext ctx) {
		boolean var = ctx.var!=null;
		Type type = ctx.type().getText().equals("int") ? Type.INT : Type.BOOL;
		String id = ctx.ID().getText();
		
		return new Parameter(var, type, id);
	}

	@Override
	public Node visitStatement(StatementContext ctx) {
		return visit(ctx.getChild(0));
	}

	@Override
	public Node visitIfthenelse(IfthenelseContext ctx) {
		Exp condition = (Exp) visitExp(ctx.condition);
		Block thenBranch = (Block) visitBlock(ctx.thenBranch);
		Block elseBranch = (Block) visitBlock(ctx.elseBranch);
		
		return new IfThenElse(condition, thenBranch, elseBranch);
	}

	@Override
	public Node visitBlock(BlockContext ctx) {
		List<Statement> statements = new ArrayList<Statement>();
		for(StatementContext sc : ctx.statement())
			statements.add((Statement) visit(sc));
		
		return new Block(statements);
	}

	@Override
	public Node visitTerm(TermContext ctx) {
		Factor factor = (Factor) visit(ctx.factor());
		Operator op = null;
		Term term = null;
		
		if(ctx.op!=null) {
			String symbol = ctx.op.getText();
			if(symbol.equals("*"))
				op = Operator.TIMES;
			else if(symbol.equals("/"))
				op = Operator.DIVIDE;
			
			term = (Term) visitTerm(ctx.right);
		}
		
		return new Term(factor, op, term);
	}

	@Override
	public Node visitIntegerValue(IntegerValueContext ctx) {
		int value = Integer.parseInt(ctx.getChild(0).getText());
		return new IntegerValue(value);
	}

	@Override
	public Node visitBooleanValue(BooleanValueContext ctx) {
		boolean value = ctx.getChild(0).getText().equals("true");	//is true iff lexem is "true"
		return new BooleanValue(value);
	}

	@Override
	public Node visitExp(ExpContext ctx) {
		boolean negative = ctx.minus!=null;
		Term term = (Term) visitTerm(ctx.term());
		Operator op = null;
		Exp expression = null;
		
		if(ctx.op!=null) {
			String symbol = ctx.op.getText();
			if(symbol.equals("+"))
				op = Operator.PLUS;
			else if(symbol.equals("-"))
				op = Operator.MINUS;
			
			expression = (Exp) visitExp(ctx.exp());
		}
		
		return new Exp(negative, term, op, expression);
	}
	
	@Override
	public Node visitFunctionDeclaration(FunctionDeclarationContext ctx) {
		String id = ctx.ID().getText();
		List<Parameter> parameters = new ArrayList<Parameter>();
		for(ParameterContext pc : ctx.parameter())
			parameters.add((Parameter) visitParameter(pc));
		Block body = (Block) visitBlock(ctx.block());
		
		return new FunctionDeclaration(id, parameters, body);
	}
	

	@Override
	public Node visitExpValue(ExpValueContext ctx) {
		Exp expression = (Exp) visitExp(ctx.exp());
		return new ExpValue(expression);
	}

}
