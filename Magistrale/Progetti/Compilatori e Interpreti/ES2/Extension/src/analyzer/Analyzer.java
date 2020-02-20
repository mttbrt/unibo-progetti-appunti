package analyzer;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import model.Environment;
import model.ExtensionDeclarationVisitor;
import parser.ExtensionLexer;
import parser.ExtensionParser;

public class Analyzer {
	
	public static void main(String[] args) {
		
		String fileName = "test.txt";
		
		try {   
			FileInputStream is = new FileInputStream(fileName);
			ANTLRInputStream input = new ANTLRInputStream(is);

			//create lexer
			ExtensionLexer lexer = new ExtensionLexer(input);
			//create parser
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			ExtensionParser parser = new ExtensionParser(tokens);
			//tell the parser to build the AST
			parser.setBuildParseTree(true);
			
			//build custom visitor
			ExtensionDeclarationVisitor visitor = new ExtensionDeclarationVisitor();
			//visit the root, this will recursively visit the whole tree
			Environment programEnvironment = visitor.visit(parser.block());
			
			// Exercise 2
			int funDecNum = programEnvironment.getNumberOfFunctionDeclarations();
			if(funDecNum > 0)
				System.out.println("\n(EX2) Number of function declarations: " + funDecNum);
			else
				System.out.println("\n(EX2) No function declarations.");
			
			// Exercise 3
			if(programEnvironment.duplicateFree())
				System.out.println("\n(EX3) There are no multiple declarations of the same identifier.");
			else {
				ArrayList<String> duplicates = programEnvironment.getDuplicates();
				System.out.println("\n(EX3) " + (duplicates.size() > 1 ? "There are " : "There is ") + duplicates.size() + " multiple declaration" + (duplicates.size() > 1 ? "s" : "") + ".");
				System.out.println("Duplicate declaration" + (duplicates.size() > 1 ? "s include:" : " is:"));
				String str = "";
				for(String elem : duplicates)
					str += elem + ", ";
				System.out.println(str.substring(0, str.length()-2));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}