package model;

import java.util.ArrayList;
import java.util.List;

import environment.Environment;
import environment.Type;
import environment.behavior.BehavioralType;
import environment.behavior.Consistency;
import environment.behavior.ConsistencyResult;
import util.SemanticError;

public class Block extends Statement {
	
	private List<Statement> statements;
	
	public Block(List<Statement> statements) {
		this.statements = statements;
	}
	
	List<Statement> getStatements() {
		return statements;
	}
	
	@Override
	public List<SemanticError> checkSemantics(Environment env, boolean allowDeletion) {
		ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();
		
		env.enterNewScope();
		for(Statement st : statements)
			semanticErrors.addAll(st.checkSemantics(env, allowDeletion));
		env.exitScope();
		
		return semanticErrors;
	}

	@Override
	public Type checkType(Environment env) {
		Type type = Type.VOID;
		
		env.enterNewScope();
		for(Statement statement : statements)
			if(statement.checkType(env) == null)
				type = null;	//invalidate if one statement does not type correctly
		env.exitScope();
		
		return type;
	}

	@Override
	public BehavioralType checkBehavioralType(Environment env) {
		BehavioralType blockType = new BehavioralType();
		
		env.enterNewScope();
		for(Statement st : statements) {
			BehavioralType stm = st.checkBehavioralType(env);
			if(stm == null)
				//if a statement does not type, invalidate the block's type
				return null;
			
			Consistency result = blockType.incrementType(stm);
			if(result.getResult() == ConsistencyResult.DEL_DEL) {
				//a statement has a D/D (double deletion) conflict with the type of the block so far
				System.out.println("Behavioral error: DEL/DEL conflict on variable '" + result.getOffendingId() + "' in statement \"" + st.toString()+ "\"");
				return null;
			} else if(result.getResult() == ConsistencyResult.DEL_RW) {
				//a statement has a D/RW (access of a deleted variable) conflict with the type of the block so far
				System.out.println("Behavioral error: DEL/RW conflict on variable '" + result.getOffendingId() + "' in statement \"" + st.toString()+ "\"");
				return null;
			} else if(result.getResult() == ConsistencyResult.DEC_RED) {
				//a statement has a D/R (multiple declaration of a variable) conflict with the type of the block so far
				System.out.println("Behavioral error: in statement \"" + st.toString() + "\", id \"" + result.getOffendingId().toString() + "\" is already declared in the same scope.");
				return null;
			}
		}
			
		env.exitScope();
		
		blockType.cleanType(); //Remove local actions
		return blockType;
	}

	@Override
	public String toString() {
		try {
			return "{" + statements.get(0).toString() + "...}";
		} catch (IndexOutOfBoundsException e) {
			return "{}";
		}
	}

	@Override
	public String generateWTMCode(Environment env) {
		env.enterNewScope();
		String code = "push $fp\n"	//For blocks, AL==oldFP
						+ "move $fp $sp\n"
						+ "push $ra\n"; //Useless, pushed for offset consistency
		for (Statement st : statements) {
			code += st.generateWTMCode(env);
		}
		int localVars = env.getNumberOfLocalVariables();
		code += "addi $sp $sp " + (localVars+1) + "\n"	//Undo local variables and RA on the stack
				+ "top $fp\n"	//Restore $fp
				+ "pop\n"; 		//Pop oldFP
		env.exitScope();
		return code;
	}

	
}
