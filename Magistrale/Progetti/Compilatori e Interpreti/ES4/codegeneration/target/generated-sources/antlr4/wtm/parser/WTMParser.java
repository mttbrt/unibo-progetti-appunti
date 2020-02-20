// Generated from WTM.g4 by ANTLR 4.4
package wtm.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WTMParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PUSH=1, TOP=2, POP=3, MOVE=4, ADD=5, ADDINT=6, SUB=7, MULT=8, DIV=9, CHANGESIGN=10, 
		STOREW=11, LOADW=12, LOADINT=13, BRANCH=14, BRANCHEQ=15, BRANCHLESSEQ=16, 
		JUMPANDLINK=17, JUMPREGISTER=18, PRINT=19, HALT=20, LPAR=21, RPAR=22, 
		COL=23, LABEL=24, NUMBER=25, REGISTER=26, WHITESP=27, ERR=28;
	public static final String[] tokenNames = {
		"<INVALID>", "'push'", "'top'", "'pop'", "'move'", "'add'", "'addi'", 
		"'sub'", "'mult'", "'div'", "'csig'", "'sw'", "'lw'", "'li'", "'b'", "'beq'", 
		"'bleq'", "'jal'", "'jr'", "'print'", "'halt'", "'('", "')'", "':'", "LABEL", 
		"NUMBER", "REGISTER", "WHITESP", "ERR"
	};
	public static final int
		RULE_assembly = 0, RULE_instruction = 1;
	public static final String[] ruleNames = {
		"assembly", "instruction"
	};

	@Override
	public String getGrammarFileName() { return "WTM.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WTMParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class AssemblyContext extends ParserRuleContext {
		public InstructionContext instruction(int i) {
			return getRuleContext(InstructionContext.class,i);
		}
		public List<InstructionContext> instruction() {
			return getRuleContexts(InstructionContext.class);
		}
		public AssemblyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assembly; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WTMVisitor ) return ((WTMVisitor<? extends T>)visitor).visitAssembly(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssemblyContext assembly() throws RecognitionException {
		AssemblyContext _localctx = new AssemblyContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_assembly);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PUSH) | (1L << TOP) | (1L << POP) | (1L << MOVE) | (1L << ADD) | (1L << ADDINT) | (1L << SUB) | (1L << MULT) | (1L << DIV) | (1L << CHANGESIGN) | (1L << STOREW) | (1L << LOADW) | (1L << LOADINT) | (1L << BRANCH) | (1L << BRANCHEQ) | (1L << BRANCHLESSEQ) | (1L << JUMPANDLINK) | (1L << JUMPREGISTER) | (1L << PRINT) | (1L << HALT) | (1L << LABEL))) != 0)) {
				{
				{
				setState(4); instruction();
				}
				}
				setState(9);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InstructionContext extends ParserRuleContext {
		public Token register;
		public Token dest;
		public Token src;
		public Token left;
		public Token right;
		public Token value;
		public Token offset;
		public Token label;
		public TerminalNode BRANCH() { return getToken(WTMParser.BRANCH, 0); }
		public TerminalNode CHANGESIGN() { return getToken(WTMParser.CHANGESIGN, 0); }
		public TerminalNode BRANCHLESSEQ() { return getToken(WTMParser.BRANCHLESSEQ, 0); }
		public TerminalNode REGISTER(int i) {
			return getToken(WTMParser.REGISTER, i);
		}
		public TerminalNode BRANCHEQ() { return getToken(WTMParser.BRANCHEQ, 0); }
		public TerminalNode ADD() { return getToken(WTMParser.ADD, 0); }
		public TerminalNode LOADW() { return getToken(WTMParser.LOADW, 0); }
		public TerminalNode COL() { return getToken(WTMParser.COL, 0); }
		public TerminalNode DIV() { return getToken(WTMParser.DIV, 0); }
		public TerminalNode JUMPREGISTER() { return getToken(WTMParser.JUMPREGISTER, 0); }
		public TerminalNode LOADINT() { return getToken(WTMParser.LOADINT, 0); }
		public TerminalNode PRINT() { return getToken(WTMParser.PRINT, 0); }
		public TerminalNode MULT() { return getToken(WTMParser.MULT, 0); }
		public List<TerminalNode> REGISTER() { return getTokens(WTMParser.REGISTER); }
		public TerminalNode ADDINT() { return getToken(WTMParser.ADDINT, 0); }
		public TerminalNode SUB() { return getToken(WTMParser.SUB, 0); }
		public TerminalNode JUMPANDLINK() { return getToken(WTMParser.JUMPANDLINK, 0); }
		public TerminalNode TOP() { return getToken(WTMParser.TOP, 0); }
		public TerminalNode LPAR() { return getToken(WTMParser.LPAR, 0); }
		public TerminalNode MOVE() { return getToken(WTMParser.MOVE, 0); }
		public TerminalNode PUSH() { return getToken(WTMParser.PUSH, 0); }
		public TerminalNode POP() { return getToken(WTMParser.POP, 0); }
		public TerminalNode LABEL() { return getToken(WTMParser.LABEL, 0); }
		public TerminalNode STOREW() { return getToken(WTMParser.STOREW, 0); }
		public TerminalNode HALT() { return getToken(WTMParser.HALT, 0); }
		public TerminalNode NUMBER() { return getToken(WTMParser.NUMBER, 0); }
		public TerminalNode RPAR() { return getToken(WTMParser.RPAR, 0); }
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WTMVisitor ) return ((WTMVisitor<? extends T>)visitor).visitInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_instruction);
		try {
			setState(75);
			switch (_input.LA(1)) {
			case PUSH:
				enterOuterAlt(_localctx, 1);
				{
				setState(10); match(PUSH);
				setState(11); ((InstructionContext)_localctx).register = match(REGISTER);
				}
				break;
			case TOP:
				enterOuterAlt(_localctx, 2);
				{
				setState(12); match(TOP);
				setState(13); ((InstructionContext)_localctx).register = match(REGISTER);
				}
				break;
			case POP:
				enterOuterAlt(_localctx, 3);
				{
				setState(14); match(POP);
				}
				break;
			case MOVE:
				enterOuterAlt(_localctx, 4);
				{
				setState(15); match(MOVE);
				setState(16); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(17); ((InstructionContext)_localctx).src = match(REGISTER);
				}
				break;
			case ADD:
				enterOuterAlt(_localctx, 5);
				{
				setState(18); match(ADD);
				setState(19); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(20); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(21); ((InstructionContext)_localctx).right = match(REGISTER);
				}
				break;
			case ADDINT:
				enterOuterAlt(_localctx, 6);
				{
				setState(22); match(ADDINT);
				setState(23); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(24); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(25); ((InstructionContext)_localctx).value = match(NUMBER);
				}
				break;
			case SUB:
				enterOuterAlt(_localctx, 7);
				{
				setState(26); match(SUB);
				setState(27); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(28); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(29); ((InstructionContext)_localctx).right = match(REGISTER);
				}
				break;
			case MULT:
				enterOuterAlt(_localctx, 8);
				{
				setState(30); match(MULT);
				setState(31); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(32); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(33); ((InstructionContext)_localctx).right = match(REGISTER);
				}
				break;
			case DIV:
				enterOuterAlt(_localctx, 9);
				{
				setState(34); match(DIV);
				setState(35); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(36); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(37); ((InstructionContext)_localctx).right = match(REGISTER);
				}
				break;
			case CHANGESIGN:
				enterOuterAlt(_localctx, 10);
				{
				setState(38); match(CHANGESIGN);
				setState(39); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(40); ((InstructionContext)_localctx).src = match(REGISTER);
				}
				break;
			case STOREW:
				enterOuterAlt(_localctx, 11);
				{
				setState(41); match(STOREW);
				setState(42); ((InstructionContext)_localctx).src = match(REGISTER);
				setState(43); ((InstructionContext)_localctx).offset = match(NUMBER);
				setState(44); match(LPAR);
				setState(45); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(46); match(RPAR);
				}
				break;
			case LOADW:
				enterOuterAlt(_localctx, 12);
				{
				setState(47); match(LOADW);
				setState(48); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(49); ((InstructionContext)_localctx).offset = match(NUMBER);
				setState(50); match(LPAR);
				setState(51); ((InstructionContext)_localctx).src = match(REGISTER);
				setState(52); match(RPAR);
				}
				break;
			case LOADINT:
				enterOuterAlt(_localctx, 13);
				{
				setState(53); match(LOADINT);
				setState(54); ((InstructionContext)_localctx).dest = match(REGISTER);
				setState(55); ((InstructionContext)_localctx).value = match(NUMBER);
				}
				break;
			case LABEL:
				enterOuterAlt(_localctx, 14);
				{
				setState(56); ((InstructionContext)_localctx).label = match(LABEL);
				setState(57); match(COL);
				}
				break;
			case BRANCH:
				enterOuterAlt(_localctx, 15);
				{
				setState(58); match(BRANCH);
				setState(59); ((InstructionContext)_localctx).label = match(LABEL);
				}
				break;
			case BRANCHEQ:
				enterOuterAlt(_localctx, 16);
				{
				setState(60); match(BRANCHEQ);
				setState(61); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(62); ((InstructionContext)_localctx).right = match(REGISTER);
				setState(63); ((InstructionContext)_localctx).label = match(LABEL);
				}
				break;
			case BRANCHLESSEQ:
				enterOuterAlt(_localctx, 17);
				{
				setState(64); match(BRANCHLESSEQ);
				setState(65); ((InstructionContext)_localctx).left = match(REGISTER);
				setState(66); ((InstructionContext)_localctx).right = match(REGISTER);
				setState(67); ((InstructionContext)_localctx).label = match(LABEL);
				}
				break;
			case JUMPANDLINK:
				enterOuterAlt(_localctx, 18);
				{
				setState(68); match(JUMPANDLINK);
				setState(69); ((InstructionContext)_localctx).label = match(LABEL);
				}
				break;
			case JUMPREGISTER:
				enterOuterAlt(_localctx, 19);
				{
				setState(70); match(JUMPREGISTER);
				setState(71); ((InstructionContext)_localctx).register = match(REGISTER);
				}
				break;
			case PRINT:
				enterOuterAlt(_localctx, 20);
				{
				setState(72); match(PRINT);
				setState(73); ((InstructionContext)_localctx).register = match(REGISTER);
				}
				break;
			case HALT:
				enterOuterAlt(_localctx, 21);
				{
				setState(74); match(HALT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\36P\4\2\t\2\4\3\t"+
		"\3\3\2\7\2\b\n\2\f\2\16\2\13\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\5\3N\n\3\3\3\2\2\4\2\4\2\2b\2\t\3\2\2\2\4M\3\2\2\2\6\b"+
		"\5\4\3\2\7\6\3\2\2\2\b\13\3\2\2\2\t\7\3\2\2\2\t\n\3\2\2\2\n\3\3\2\2\2"+
		"\13\t\3\2\2\2\f\r\7\3\2\2\rN\7\34\2\2\16\17\7\4\2\2\17N\7\34\2\2\20N\7"+
		"\5\2\2\21\22\7\6\2\2\22\23\7\34\2\2\23N\7\34\2\2\24\25\7\7\2\2\25\26\7"+
		"\34\2\2\26\27\7\34\2\2\27N\7\34\2\2\30\31\7\b\2\2\31\32\7\34\2\2\32\33"+
		"\7\34\2\2\33N\7\33\2\2\34\35\7\t\2\2\35\36\7\34\2\2\36\37\7\34\2\2\37"+
		"N\7\34\2\2 !\7\n\2\2!\"\7\34\2\2\"#\7\34\2\2#N\7\34\2\2$%\7\13\2\2%&\7"+
		"\34\2\2&\'\7\34\2\2\'N\7\34\2\2()\7\f\2\2)*\7\34\2\2*N\7\34\2\2+,\7\r"+
		"\2\2,-\7\34\2\2-.\7\33\2\2./\7\27\2\2/\60\7\34\2\2\60N\7\30\2\2\61\62"+
		"\7\16\2\2\62\63\7\34\2\2\63\64\7\33\2\2\64\65\7\27\2\2\65\66\7\34\2\2"+
		"\66N\7\30\2\2\678\7\17\2\289\7\34\2\29N\7\33\2\2:;\7\32\2\2;N\7\31\2\2"+
		"<=\7\20\2\2=N\7\32\2\2>?\7\21\2\2?@\7\34\2\2@A\7\34\2\2AN\7\32\2\2BC\7"+
		"\22\2\2CD\7\34\2\2DE\7\34\2\2EN\7\32\2\2FG\7\23\2\2GN\7\32\2\2HI\7\24"+
		"\2\2IN\7\34\2\2JK\7\25\2\2KN\7\34\2\2LN\7\26\2\2M\f\3\2\2\2M\16\3\2\2"+
		"\2M\20\3\2\2\2M\21\3\2\2\2M\24\3\2\2\2M\30\3\2\2\2M\34\3\2\2\2M \3\2\2"+
		"\2M$\3\2\2\2M(\3\2\2\2M+\3\2\2\2M\61\3\2\2\2M\67\3\2\2\2M:\3\2\2\2M<\3"+
		"\2\2\2M>\3\2\2\2MB\3\2\2\2MF\3\2\2\2MH\3\2\2\2MJ\3\2\2\2ML\3\2\2\2N\5"+
		"\3\2\2\2\4\tM";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}