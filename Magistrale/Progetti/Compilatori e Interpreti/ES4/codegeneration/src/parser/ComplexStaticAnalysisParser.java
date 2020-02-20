// Generated from ComplexStaticAnalysis.g4 by ANTLR 4.4
package parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ComplexStaticAnalysisParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__22=1, T__21=2, T__20=3, T__19=4, T__18=5, T__17=6, T__16=7, T__15=8, 
		T__14=9, T__13=10, T__12=11, T__11=12, T__10=13, T__9=14, T__8=15, T__7=16, 
		T__6=17, T__5=18, T__4=19, T__3=20, T__2=21, T__1=22, T__0=23, ROP=24, 
		INTEGER=25, ID=26, WS=27, LINECOMENTS=28, BLOCKCOMENTS=29, ERR=30;
	public static final String[] tokenNames = {
		"<INVALID>", "'/'", "'var'", "'true'", "'||'", "';'", "'{'", "'&&'", "'='", 
		"'}'", "'bool'", "'if'", "'delete'", "'int'", "'else'", "'print'", "'('", 
		"')'", "'*'", "'+'", "'then'", "','", "'-'", "'false'", "ROP", "INTEGER", 
		"ID", "WS", "LINECOMENTS", "BLOCKCOMENTS", "ERR"
	};
	public static final int
		RULE_block = 0, RULE_statement = 1, RULE_assignment = 2, RULE_deletion = 3, 
		RULE_print = 4, RULE_functioncall = 5, RULE_ifthenelse = 6, RULE_declaration = 7, 
		RULE_type = 8, RULE_parameter = 9, RULE_exp = 10, RULE_term = 11, RULE_factor = 12, 
		RULE_value = 13;
	public static final String[] ruleNames = {
		"block", "statement", "assignment", "deletion", "print", "functioncall", 
		"ifthenelse", "declaration", "type", "parameter", "exp", "term", "factor", 
		"value"
	};

	@Override
	public String getGrammarFileName() { return "ComplexStaticAnalysis.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ComplexStaticAnalysisParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class BlockContext extends ParserRuleContext {
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28); match(T__17);
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__13) | (1L << T__12) | (1L << T__11) | (1L << T__10) | (1L << T__8) | (1L << ID))) != 0)) {
				{
				{
				setState(29); statement();
				}
				}
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(35); match(T__14);
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

	public static class StatementContext extends ParserRuleContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public DeletionContext deletion() {
			return getRuleContext(DeletionContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public FunctioncallContext functioncall() {
			return getRuleContext(FunctioncallContext.class,0);
		}
		public IfthenelseContext ifthenelse() {
			return getRuleContext(IfthenelseContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public PrintContext print() {
			return getRuleContext(PrintContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(52);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(37); assignment();
				setState(38); match(T__18);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(40); deletion();
				setState(41); match(T__18);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(43); print();
				setState(44); match(T__18);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(46); functioncall();
				setState(47); match(T__18);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(49); ifthenelse();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(50); declaration();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(51); block();
				}
				break;
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

	public static class AssignmentContext extends ParserRuleContext {
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54); match(ID);
			setState(55); match(T__15);
			setState(56); exp();
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

	public static class DeletionContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public DeletionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deletion; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitDeletion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeletionContext deletion() throws RecognitionException {
		DeletionContext _localctx = new DeletionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_deletion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58); match(T__11);
			setState(59); match(ID);
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

	public static class PrintContext extends ParserRuleContext {
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public PrintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_print; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitPrint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrintContext print() throws RecognitionException {
		PrintContext _localctx = new PrintContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_print);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61); match(T__8);
			setState(62); exp();
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

	public static class FunctioncallContext extends ParserRuleContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public FunctioncallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functioncall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitFunctioncall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctioncallContext functioncall() throws RecognitionException {
		FunctioncallContext _localctx = new FunctioncallContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_functioncall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64); match(ID);
			setState(65); match(T__7);
			setState(74);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__7) | (1L << T__1) | (1L << T__0) | (1L << INTEGER) | (1L << ID))) != 0)) {
				{
				setState(66); exp();
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(67); match(T__2);
					setState(68); exp();
					}
					}
					setState(73);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(76); match(T__6);
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

	public static class IfthenelseContext extends ParserRuleContext {
		public ExpContext condition;
		public BlockContext thenBranch;
		public BlockContext elseBranch;
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public IfthenelseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifthenelse; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitIfthenelse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfthenelseContext ifthenelse() throws RecognitionException {
		IfthenelseContext _localctx = new IfthenelseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_ifthenelse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78); match(T__12);
			setState(79); match(T__7);
			setState(80); ((IfthenelseContext)_localctx).condition = exp();
			setState(81); match(T__6);
			setState(82); match(T__3);
			setState(83); ((IfthenelseContext)_localctx).thenBranch = block();
			setState(84); match(T__9);
			setState(85); ((IfthenelseContext)_localctx).elseBranch = block();
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

	public static class DeclarationContext extends ParserRuleContext {
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
	 
		public DeclarationContext() { }
		public void copyFrom(DeclarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FunctionDeclarationContext extends DeclarationContext {
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FunctionDeclarationContext(DeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitFunctionDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableDeclarationContext extends DeclarationContext {
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VariableDeclarationContext(DeclarationContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_declaration);
		int _la;
		try {
			setState(107);
			switch (_input.LA(1)) {
			case T__13:
			case T__10:
				_localctx = new VariableDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(87); type();
				setState(88); match(ID);
				setState(89); match(T__15);
				setState(90); exp();
				setState(91); match(T__18);
				}
				break;
			case ID:
				_localctx = new FunctionDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(93); match(ID);
				setState(94); match(T__7);
				setState(103);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__21) | (1L << T__13) | (1L << T__10))) != 0)) {
					{
					setState(95); parameter();
					setState(100);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__2) {
						{
						{
						setState(96); match(T__2);
						setState(97); parameter();
						}
						}
						setState(102);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(105); match(T__6);
				setState(106); block();
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

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_la = _input.LA(1);
			if ( !(_la==T__13 || _la==T__10) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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

	public static class ParameterContext extends ParserRuleContext {
		public Token var;
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_parameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			_la = _input.LA(1);
			if (_la==T__21) {
				{
				setState(111); ((ParameterContext)_localctx).var = match(T__21);
				}
			}

			setState(114); type();
			setState(115); match(ID);
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

	public static class ExpContext extends ParserRuleContext {
		public Token minus;
		public TermContext left;
		public Token op;
		public ExpContext right;
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpContext exp() throws RecognitionException {
		ExpContext _localctx = new ExpContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_exp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(117); ((ExpContext)_localctx).minus = match(T__1);
				}
			}

			setState(120); ((ExpContext)_localctx).left = term();
			setState(123);
			_la = _input.LA(1);
			if (_la==T__4 || _la==T__1) {
				{
				setState(121);
				((ExpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__4 || _la==T__1) ) {
					((ExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(122); ((ExpContext)_localctx).right = exp();
				}
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

	public static class TermContext extends ParserRuleContext {
		public FactorContext left;
		public Token op;
		public TermContext right;
		public FactorContext factor() {
			return getRuleContext(FactorContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_term);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125); ((TermContext)_localctx).left = factor();
			setState(128);
			_la = _input.LA(1);
			if (_la==T__22 || _la==T__5) {
				{
				setState(126);
				((TermContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__22 || _la==T__5) ) {
					((TermContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(127); ((TermContext)_localctx).right = term();
				}
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

	public static class FactorContext extends ParserRuleContext {
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
	 
		public FactorContext() { }
		public void copyFrom(FactorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RelationalFactorContext extends FactorContext {
		public ValueContext left;
		public Token rop;
		public ValueContext right;
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public TerminalNode ROP() { return getToken(ComplexStaticAnalysisParser.ROP, 0); }
		public RelationalFactorContext(FactorContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitRelationalFactor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanFactorContext extends FactorContext {
		public ValueContext left;
		public Token bop;
		public ValueContext right;
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public BooleanFactorContext(FactorContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitBooleanFactor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_factor);
		int _la;
		try {
			setState(140);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new RelationalFactorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(130); ((RelationalFactorContext)_localctx).left = value();
				setState(133);
				_la = _input.LA(1);
				if (_la==ROP) {
					{
					setState(131); ((RelationalFactorContext)_localctx).rop = match(ROP);
					setState(132); ((RelationalFactorContext)_localctx).right = value();
					}
				}

				}
				break;
			case 2:
				_localctx = new BooleanFactorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(135); ((BooleanFactorContext)_localctx).left = value();
				setState(138);
				_la = _input.LA(1);
				if (_la==T__19 || _la==T__16) {
					{
					setState(136);
					((BooleanFactorContext)_localctx).bop = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__19 || _la==T__16) ) {
						((BooleanFactorContext)_localctx).bop = (Token)_errHandler.recoverInline(this);
					}
					consume();
					setState(137); ((BooleanFactorContext)_localctx).right = value();
					}
				}

				}
				break;
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

	public static class ValueContext extends ParserRuleContext {
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
	 
		public ValueContext() { }
		public void copyFrom(ValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class VariableValueContext extends ValueContext {
		public TerminalNode ID() { return getToken(ComplexStaticAnalysisParser.ID, 0); }
		public VariableValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitVariableValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntegerValueContext extends ValueContext {
		public TerminalNode INTEGER() { return getToken(ComplexStaticAnalysisParser.INTEGER, 0); }
		public IntegerValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitIntegerValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanValueContext extends ValueContext {
		public BooleanValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitBooleanValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpValueContext extends ValueContext {
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public ExpValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ComplexStaticAnalysisVisitor ) return ((ComplexStaticAnalysisVisitor<? extends T>)visitor).visitExpValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_value);
		int _la;
		try {
			setState(149);
			switch (_input.LA(1)) {
			case INTEGER:
				_localctx = new IntegerValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(142); match(INTEGER);
				}
				break;
			case T__20:
			case T__0:
				_localctx = new BooleanValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(143);
				_la = _input.LA(1);
				if ( !(_la==T__20 || _la==T__0) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case T__7:
				_localctx = new ExpValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(144); match(T__7);
				setState(145); exp();
				setState(146); match(T__6);
				}
				break;
			case ID:
				_localctx = new VariableValueContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(148); match(ID);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3 \u009a\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\7\2!\n\2\f\2\16\2$\13"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\5\3\67\n\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\7\7H\n\7\f\7\16\7K\13\7\5\7M\n\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\7\te\n\t\f\t\16"+
		"\th\13\t\5\tj\n\t\3\t\3\t\5\tn\n\t\3\n\3\n\3\13\5\13s\n\13\3\13\3\13\3"+
		"\13\3\f\5\fy\n\f\3\f\3\f\3\f\5\f~\n\f\3\r\3\r\3\r\5\r\u0083\n\r\3\16\3"+
		"\16\3\16\5\16\u0088\n\16\3\16\3\16\3\16\5\16\u008d\n\16\5\16\u008f\n\16"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u0098\n\17\3\17\2\2\20\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\2\7\4\2\f\f\17\17\4\2\25\25\30\30\4\2\3"+
		"\3\24\24\4\2\6\6\t\t\4\2\5\5\31\31\u00a1\2\36\3\2\2\2\4\66\3\2\2\2\68"+
		"\3\2\2\2\b<\3\2\2\2\n?\3\2\2\2\fB\3\2\2\2\16P\3\2\2\2\20m\3\2\2\2\22o"+
		"\3\2\2\2\24r\3\2\2\2\26x\3\2\2\2\30\177\3\2\2\2\32\u008e\3\2\2\2\34\u0097"+
		"\3\2\2\2\36\"\7\b\2\2\37!\5\4\3\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#"+
		"\3\2\2\2#%\3\2\2\2$\"\3\2\2\2%&\7\13\2\2&\3\3\2\2\2\'(\5\6\4\2()\7\7\2"+
		"\2)\67\3\2\2\2*+\5\b\5\2+,\7\7\2\2,\67\3\2\2\2-.\5\n\6\2./\7\7\2\2/\67"+
		"\3\2\2\2\60\61\5\f\7\2\61\62\7\7\2\2\62\67\3\2\2\2\63\67\5\16\b\2\64\67"+
		"\5\20\t\2\65\67\5\2\2\2\66\'\3\2\2\2\66*\3\2\2\2\66-\3\2\2\2\66\60\3\2"+
		"\2\2\66\63\3\2\2\2\66\64\3\2\2\2\66\65\3\2\2\2\67\5\3\2\2\289\7\34\2\2"+
		"9:\7\n\2\2:;\5\26\f\2;\7\3\2\2\2<=\7\16\2\2=>\7\34\2\2>\t\3\2\2\2?@\7"+
		"\21\2\2@A\5\26\f\2A\13\3\2\2\2BC\7\34\2\2CL\7\22\2\2DI\5\26\f\2EF\7\27"+
		"\2\2FH\5\26\f\2GE\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JM\3\2\2\2KI\3"+
		"\2\2\2LD\3\2\2\2LM\3\2\2\2MN\3\2\2\2NO\7\23\2\2O\r\3\2\2\2PQ\7\r\2\2Q"+
		"R\7\22\2\2RS\5\26\f\2ST\7\23\2\2TU\7\26\2\2UV\5\2\2\2VW\7\20\2\2WX\5\2"+
		"\2\2X\17\3\2\2\2YZ\5\22\n\2Z[\7\34\2\2[\\\7\n\2\2\\]\5\26\f\2]^\7\7\2"+
		"\2^n\3\2\2\2_`\7\34\2\2`i\7\22\2\2af\5\24\13\2bc\7\27\2\2ce\5\24\13\2"+
		"db\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2gj\3\2\2\2hf\3\2\2\2ia\3\2\2\2"+
		"ij\3\2\2\2jk\3\2\2\2kl\7\23\2\2ln\5\2\2\2mY\3\2\2\2m_\3\2\2\2n\21\3\2"+
		"\2\2op\t\2\2\2p\23\3\2\2\2qs\7\4\2\2rq\3\2\2\2rs\3\2\2\2st\3\2\2\2tu\5"+
		"\22\n\2uv\7\34\2\2v\25\3\2\2\2wy\7\30\2\2xw\3\2\2\2xy\3\2\2\2yz\3\2\2"+
		"\2z}\5\30\r\2{|\t\3\2\2|~\5\26\f\2}{\3\2\2\2}~\3\2\2\2~\27\3\2\2\2\177"+
		"\u0082\5\32\16\2\u0080\u0081\t\4\2\2\u0081\u0083\5\30\r\2\u0082\u0080"+
		"\3\2\2\2\u0082\u0083\3\2\2\2\u0083\31\3\2\2\2\u0084\u0087\5\34\17\2\u0085"+
		"\u0086\7\32\2\2\u0086\u0088\5\34\17\2\u0087\u0085\3\2\2\2\u0087\u0088"+
		"\3\2\2\2\u0088\u008f\3\2\2\2\u0089\u008c\5\34\17\2\u008a\u008b\t\5\2\2"+
		"\u008b\u008d\5\34\17\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f"+
		"\3\2\2\2\u008e\u0084\3\2\2\2\u008e\u0089\3\2\2\2\u008f\33\3\2\2\2\u0090"+
		"\u0098\7\33\2\2\u0091\u0098\t\6\2\2\u0092\u0093\7\22\2\2\u0093\u0094\5"+
		"\26\f\2\u0094\u0095\7\23\2\2\u0095\u0098\3\2\2\2\u0096\u0098\7\34\2\2"+
		"\u0097\u0090\3\2\2\2\u0097\u0091\3\2\2\2\u0097\u0092\3\2\2\2\u0097\u0096"+
		"\3\2\2\2\u0098\35\3\2\2\2\21\"\66ILfimrx}\u0082\u0087\u008c\u008e\u0097";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}