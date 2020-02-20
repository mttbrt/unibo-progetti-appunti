package compiler;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import environment.Environment;
import model.Block;
import model.ComplexStaticAnalysisVisitorImpl;
import parser.ComplexStaticAnalysisLexer;
import parser.ComplexStaticAnalysisParser;
import util.SemanticError;
import wtm.WTM;
import wtm.parser.WTMLexer;
import wtm.parser.WTMParser;
import wtm.parser.WTMProcessingVisitor;


public class Compiler {
	
	public static void main(String[] args) throws Exception {
		
		String[] testCases = {
				/*"tests/assignment3_code1.txt",
				"tests/assignment3_fun1.txt",
				"tests/assignment3_fun2.txt",
				"tests/assignment3_fun3.txt",
				"tests/assignment3_fun4.txt",
				"tests/assignment4_fun1.txt",
				"tests/assignment4_fun2.txt",
				"tests/assignment4_fun3.txt",
				/*"tests/semantic_correct.txt",
				"tests/semantic_undeclared.txt",
				"tests/semantic_declared_twice.txt",
				"tests/semantic_parameters.txt",

				"tests/type_correct.txt",
				"tests/type_misassignment.txt",
				"tests/type_mix_int_bool.txt",
				"tests/type_parameters.txt",
				
				"tests/deletion_correct.txt",
				"tests/deletion_false_positive.txt",
				"tests/deletion_function_correct.txt",
				"tests/deletion_function_incorrect.txt",
				"tests/deletion_recursion_correct.txt",
				"tests/deletion_recursion_incorrect.txt",
				"tests/deletion_aliasing_correct.txt",
				"tests/deletion_aliasing_incorrect.txt",*/
				
				"tests/multipurpose.txt"};
    	
		boolean allowDelete = true;
		/*
		Scanner reader = new Scanner(System.in);
		System.out.print("\nEnter exercise number (1) or (2): ");
		int exNum = reader.nextInt();
		boolean allowDelete = exNum == 2;
		reader.close();
		*/
		
        for (String fileName : testCases) {
        	
        	//ANALYSIS
        	Block ast = analyze(fileName, allowDelete);
        	if(ast == null) {
        		//If code is semantically inconsistent, skip it
        		continue;
        	}
        	
        	//CODE GENERATION
        	String wtmFilePath = "generated_code/" + fileNameFromPath(fileName) + ".wtm";
        	if(!compile(ast, wtmFilePath)) {
        		//If compilation is unsuccessful, skip file
        		continue;
        	}
        	
        	//EXECUTION
        	executeWTM(wtmFilePath);
        	
        }
	}
	
	public static Block analyze(String fileName, boolean allowDelete) {
		try {   
        	FileInputStream is = new FileInputStream(fileName);
            ANTLRInputStream input = new ANTLRInputStream(is);
            
            System.out.println("\n*** " + fileName + " ***\n");

			// Create lexer
            ComplexStaticAnalysisLexer lexer = new ComplexStaticAnalysisLexer(input);
            lexer.removeErrorListeners();	//forward errors to parser
			// Create parser
			ComplexStaticAnalysisParser parser = new ComplexStaticAnalysisParser(new CommonTokenStream(lexer));
			parser.setBuildParseTree(true);	// Tell the parser to build the AST
			parser.setErrorHandler(new BailErrorStrategy());	//Throw exception on parsing error
			parser.removeErrorListeners();	//Do not report parsing errors directly to the console
			
			// Build custom visitor
			ComplexStaticAnalysisVisitorImpl visitor = new ComplexStaticAnalysisVisitorImpl();
			Block ast;
			try {
				ast = (Block)visitor.visit(parser.block());	
			} catch (ParseCancellationException e) {
				System.out.println("Error: could not parse input in " + fileName + ".");
				return null;
			}
			
	        List<SemanticError> semanticErrors = ast.checkSemantics(new Environment(), allowDelete);
	        
	        boolean correct = true;
	        
	        if (semanticErrors.size() > 0) {
	        	correct = false;
	        	for(SemanticError err : semanticErrors) 
	        		System.out.println(err);
	        } else {
	        	System.out.println("No semantic errors.");
	        	if(ast.checkType(new Environment()) == null) {
	        		//Errors are printed during the visit
	        		correct = false;
	        	} else {
		        	System.out.println("No type errors.");
		        	
		        	if(allowDelete && ast.checkBehavioralType(new Environment()) == null) {
		        		//Errors are printed during the visit
		        		correct = false;
		        	} else {
		        		System.out.println("No deletion errors.");
		        	}
	        	}
	        }
	        
	        if(correct) {
	        	System.out.println("\nProgram is correct.\n");
	        	return ast;
	        } else {
	        	System.out.println("\nProgram has one ore more semantic issues.\n");
	        	return null;
	        }
	        
		} catch (IOException e) {
			System.out.println("Error: could not read file " + fileName + ".");
			return null;
		}
	}
	
	public static boolean compile(Block ast, String compiledFileName) {
		try {
			String code = ast.generateWTMCode(new Environment()); //generate code string
	        BufferedWriter out = new BufferedWriter(new FileWriter(compiledFileName)); 
	        out.write(code);	//write intermediate code to file
	        out.close();
	        System.out.println("Intermediate code generated in " + compiledFileName + ".");
	        return true;
		} catch (IOException e) {
			System.out.println("Error : could not write intermediate code to " + compiledFileName + ".");
			return false;
		}
	}
	
	public static void executeWTM(String fileName) {
		try {
			FileInputStream is = new FileInputStream(fileName);
	        ANTLRInputStream input = new ANTLRInputStream(is);
	        System.out.println("Running intermediate code...");
	        
	        //Build lexer, parser and run visitor on the ast to obtain runnable code
	        WTMLexer WTMlexer = new WTMLexer(input);
	        WTMParser WTMparser = new WTMParser(new CommonTokenStream(WTMlexer));
	        WTMProcessingVisitor WTMvisitor = new WTMProcessingVisitor();
	        WTMvisitor.visit(WTMparser.assembly());
	        
	        //Lexical errors are counted within the lexer
	        if(!WTMlexer.correct) {
	        	System.out.println("Error: there are syntax errors in WTM file " + fileName + ".");
	        	System.exit(1);
	        }
	        
	        System.out.println("Starting World Turtle Machine (WTM)...");
	        WTM vm = new WTM(WTMvisitor.code);
	        vm.execute();	//run the code
		} catch (IOException e) {
			System.out.println("Error: could not read WTM file " + fileName + ".");
		}
	}
	
	public static String fileNameFromPath(String filePath) {
		String[] splitPath = filePath.split("/");
    	String fileName = splitPath[splitPath.length - 1];
    	fileName = fileName.substring(0, fileName.length() - 4);
    	
    	return fileName;
	}
	
}
