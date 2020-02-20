// Generated from WTM.g4 by ANTLR 4.6
package wtm.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WTMLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PUSH=1, TOP=2, POP=3, MOVE=4, ADD=5, ADDINT=6, SUB=7, MULT=8, DIV=9, CHANGESIGN=10, 
		STOREW=11, LOADW=12, LOADINT=13, BRANCH=14, BRANCHEQ=15, BRANCHLESSEQ=16, 
		JUMPANDLINK=17, JUMPREGISTER=18, PRINT=19, HALT=20, LPAR=21, RPAR=22, 
		COL=23, LABEL=24, NUMBER=25, REGISTER=26, WHITESP=27, ERR=28;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"PUSH", "TOP", "POP", "MOVE", "ADD", "ADDINT", "SUB", "MULT", "DIV", "CHANGESIGN", 
		"STOREW", "LOADW", "LOADINT", "BRANCH", "BRANCHEQ", "BRANCHLESSEQ", "JUMPANDLINK", 
		"JUMPREGISTER", "PRINT", "HALT", "LPAR", "RPAR", "COL", "LABEL", "NUMBER", 
		"REGISTER", "WHITESP", "ERR"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'push'", "'top'", "'pop'", "'move'", "'add'", "'addi'", "'sub'", 
		"'mult'", "'div'", "'csig'", "'sw'", "'lw'", "'li'", "'b'", "'beq'", "'bleq'", 
		"'jal'", "'jr'", "'print'", "'halt'", "'('", "')'", "':'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "PUSH", "TOP", "POP", "MOVE", "ADD", "ADDINT", "SUB", "MULT", "DIV", 
		"CHANGESIGN", "STOREW", "LOADW", "LOADINT", "BRANCH", "BRANCHEQ", "BRANCHLESSEQ", 
		"JUMPANDLINK", "JUMPREGISTER", "PRINT", "HALT", "LPAR", "RPAR", "COL", 
		"LABEL", "NUMBER", "REGISTER", "WHITESP", "ERR"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


		public boolean correct = true;


	public WTMLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WTM.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 27:
			ERR_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void ERR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 System.err.println("Invalid char: "+ getText()); correct = false;
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\36\u00c3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\3\2\3\2\3\3"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\30\3\30\3\31\3\31\7\31\u0097\n\31\f\31\16\31\u009a\13"+
		"\31\3\32\3\32\5\32\u009e\n\32\3\32\3\32\7\32\u00a2\n\32\f\32\16\32\u00a5"+
		"\13\32\5\32\u00a7\n\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3"+
		"\33\3\33\3\33\3\33\5\33\u00b6\n\33\3\34\6\34\u00b9\n\34\r\34\16\34\u00ba"+
		"\3\34\3\34\3\35\3\35\3\35\3\35\3\35\2\2\36\3\3\5\4\7\5\t\6\13\7\r\b\17"+
		"\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+"+
		"\27-\30/\31\61\32\63\33\65\34\67\359\36\3\2\5\4\2C\\c|\6\2\62;C\\aac|"+
		"\5\2\13\f\17\17\"\"\u00cc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5@\3\2\2\2\7D\3\2\2\2\tH\3\2\2\2\13M\3"+
		"\2\2\2\rQ\3\2\2\2\17V\3\2\2\2\21Z\3\2\2\2\23_\3\2\2\2\25c\3\2\2\2\27h"+
		"\3\2\2\2\31k\3\2\2\2\33n\3\2\2\2\35q\3\2\2\2\37s\3\2\2\2!w\3\2\2\2#|\3"+
		"\2\2\2%\u0080\3\2\2\2\'\u0083\3\2\2\2)\u0089\3\2\2\2+\u008e\3\2\2\2-\u0090"+
		"\3\2\2\2/\u0092\3\2\2\2\61\u0094\3\2\2\2\63\u00a6\3\2\2\2\65\u00a8\3\2"+
		"\2\2\67\u00b8\3\2\2\29\u00be\3\2\2\2;<\7r\2\2<=\7w\2\2=>\7u\2\2>?\7j\2"+
		"\2?\4\3\2\2\2@A\7v\2\2AB\7q\2\2BC\7r\2\2C\6\3\2\2\2DE\7r\2\2EF\7q\2\2"+
		"FG\7r\2\2G\b\3\2\2\2HI\7o\2\2IJ\7q\2\2JK\7x\2\2KL\7g\2\2L\n\3\2\2\2MN"+
		"\7c\2\2NO\7f\2\2OP\7f\2\2P\f\3\2\2\2QR\7c\2\2RS\7f\2\2ST\7f\2\2TU\7k\2"+
		"\2U\16\3\2\2\2VW\7u\2\2WX\7w\2\2XY\7d\2\2Y\20\3\2\2\2Z[\7o\2\2[\\\7w\2"+
		"\2\\]\7n\2\2]^\7v\2\2^\22\3\2\2\2_`\7f\2\2`a\7k\2\2ab\7x\2\2b\24\3\2\2"+
		"\2cd\7e\2\2de\7u\2\2ef\7k\2\2fg\7i\2\2g\26\3\2\2\2hi\7u\2\2ij\7y\2\2j"+
		"\30\3\2\2\2kl\7n\2\2lm\7y\2\2m\32\3\2\2\2no\7n\2\2op\7k\2\2p\34\3\2\2"+
		"\2qr\7d\2\2r\36\3\2\2\2st\7d\2\2tu\7g\2\2uv\7s\2\2v \3\2\2\2wx\7d\2\2"+
		"xy\7n\2\2yz\7g\2\2z{\7s\2\2{\"\3\2\2\2|}\7l\2\2}~\7c\2\2~\177\7n\2\2\177"+
		"$\3\2\2\2\u0080\u0081\7l\2\2\u0081\u0082\7t\2\2\u0082&\3\2\2\2\u0083\u0084"+
		"\7r\2\2\u0084\u0085\7t\2\2\u0085\u0086\7k\2\2\u0086\u0087\7p\2\2\u0087"+
		"\u0088\7v\2\2\u0088(\3\2\2\2\u0089\u008a\7j\2\2\u008a\u008b\7c\2\2\u008b"+
		"\u008c\7n\2\2\u008c\u008d\7v\2\2\u008d*\3\2\2\2\u008e\u008f\7*\2\2\u008f"+
		",\3\2\2\2\u0090\u0091\7+\2\2\u0091.\3\2\2\2\u0092\u0093\7<\2\2\u0093\60"+
		"\3\2\2\2\u0094\u0098\t\2\2\2\u0095\u0097\t\3\2\2\u0096\u0095\3\2\2\2\u0097"+
		"\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\62\3\2\2"+
		"\2\u009a\u0098\3\2\2\2\u009b\u00a7\7\62\2\2\u009c\u009e\7/\2\2\u009d\u009c"+
		"\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a3\4\63;\2\u00a0"+
		"\u00a2\4\62;\2\u00a1\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2"+
		"\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6"+
		"\u009b\3\2\2\2\u00a6\u009d\3\2\2\2\u00a7\64\3\2\2\2\u00a8\u00b5\7&\2\2"+
		"\u00a9\u00aa\7c\2\2\u00aa\u00b6\7\62\2\2\u00ab\u00ac\7v\2\2\u00ac\u00b6"+
		"\7\63\2\2\u00ad\u00ae\7h\2\2\u00ae\u00b6\7r\2\2\u00af\u00b0\7c\2\2\u00b0"+
		"\u00b6\7n\2\2\u00b1\u00b2\7u\2\2\u00b2\u00b6\7r\2\2\u00b3\u00b4\7t\2\2"+
		"\u00b4\u00b6\7c\2\2\u00b5\u00a9\3\2\2\2\u00b5\u00ab\3\2\2\2\u00b5\u00ad"+
		"\3\2\2\2\u00b5\u00af\3\2\2\2\u00b5\u00b1\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6"+
		"\66\3\2\2\2\u00b7\u00b9\t\4\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00ba\3\2\2"+
		"\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd"+
		"\b\34\2\2\u00bd8\3\2\2\2\u00be\u00bf\13\2\2\2\u00bf\u00c0\b\35\3\2\u00c0"+
		"\u00c1\3\2\2\2\u00c1\u00c2\b\35\2\2\u00c2:\3\2\2\2\t\2\u0098\u009d\u00a3"+
		"\u00a6\u00b5\u00ba\4\2\3\2\3\35\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}