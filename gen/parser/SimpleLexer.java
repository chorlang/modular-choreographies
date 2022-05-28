// Generated from C:/Users/annem/IdeaProjects/Master_Parser/src/main/java/parser\SimpleLexer.g4 by ANTLR 4.9.1
package parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SimpleLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PROC=1, RETURN=2, BOOL=3, WORD=4, FLOAT=5, NUMBER=6, STRING=7, ARROW=8, 
		ISEP=9, WHITESPACE=10, COLON=11, OPENCURLY=12, CLOSECURLY=13, OPENPARA=14, 
		CLOSEPARA=15, COMMA=16, PERIOD=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"P", "R", "O", "C", "E", "T", "U", "N", "DIGIT", "PROC", "RETURN", "BOOL", 
			"WORD", "FLOAT", "NUMBER", "STRING", "ARROW", "ISEP", "WHITESPACE", "COLON", 
			"OPENCURLY", "CLOSECURLY", "OPENPARA", "CLOSEPARA", "COMMA", "PERIOD"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "'->'", null, "' '", 
			"':'", "'{'", "'}'", "'('", "')'", "','", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PROC", "RETURN", "BOOL", "WORD", "FLOAT", "NUMBER", "STRING", 
			"ARROW", "ISEP", "WHITESPACE", "COLON", "OPENCURLY", "CLOSECURLY", "OPENPARA", 
			"CLOSEPARA", "COMMA", "PERIOD"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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


	public SimpleLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SimpleLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23\u009b\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r_\n\r\3\16\6\16b\n"+
		"\16\r\16\16\16c\3\17\6\17g\n\17\r\17\16\17h\3\17\3\17\7\17m\n\17\f\17"+
		"\16\17p\13\17\3\20\6\20s\n\20\r\20\16\20t\3\21\3\21\3\21\3\21\7\21{\n"+
		"\21\f\21\16\21~\13\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\5\23\u0088"+
		"\n\23\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\2\2\34\3\2\5\2\7\2\t\2\13\2\r\2\17\2\21\2\23"+
		"\2\25\3\27\4\31\5\33\6\35\7\37\b!\t#\n%\13\'\f)\r+\16-\17/\20\61\21\63"+
		"\22\65\23\3\2\6\3\2\62;\4\2C\\c|\3\2$$\4\2\f\f==\2\u0099\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\3\67\3\2\2\2\5"+
		"9\3\2\2\2\7;\3\2\2\2\t=\3\2\2\2\13?\3\2\2\2\rA\3\2\2\2\17C\3\2\2\2\21"+
		"E\3\2\2\2\23G\3\2\2\2\25I\3\2\2\2\27N\3\2\2\2\31^\3\2\2\2\33a\3\2\2\2"+
		"\35f\3\2\2\2\37r\3\2\2\2!v\3\2\2\2#\u0081\3\2\2\2%\u0087\3\2\2\2\'\u0089"+
		"\3\2\2\2)\u008d\3\2\2\2+\u008f\3\2\2\2-\u0091\3\2\2\2/\u0093\3\2\2\2\61"+
		"\u0095\3\2\2\2\63\u0097\3\2\2\2\65\u0099\3\2\2\2\678\7r\2\28\4\3\2\2\2"+
		"9:\7t\2\2:\6\3\2\2\2;<\7q\2\2<\b\3\2\2\2=>\7e\2\2>\n\3\2\2\2?@\7g\2\2"+
		"@\f\3\2\2\2AB\7v\2\2B\16\3\2\2\2CD\7w\2\2D\20\3\2\2\2EF\7p\2\2F\22\3\2"+
		"\2\2GH\t\2\2\2H\24\3\2\2\2IJ\5\3\2\2JK\5\5\3\2KL\5\7\4\2LM\5\t\5\2M\26"+
		"\3\2\2\2NO\5\5\3\2OP\5\13\6\2PQ\5\r\7\2QR\5\17\b\2RS\5\5\3\2ST\5\21\t"+
		"\2T\30\3\2\2\2UV\7v\2\2VW\7t\2\2WX\7w\2\2X_\7g\2\2YZ\7h\2\2Z[\7c\2\2["+
		"\\\7n\2\2\\]\7u\2\2]_\7g\2\2^U\3\2\2\2^Y\3\2\2\2_\32\3\2\2\2`b\t\3\2\2"+
		"a`\3\2\2\2bc\3\2\2\2ca\3\2\2\2cd\3\2\2\2d\34\3\2\2\2eg\5\23\n\2fe\3\2"+
		"\2\2gh\3\2\2\2hf\3\2\2\2hi\3\2\2\2ij\3\2\2\2jn\7\60\2\2km\5\23\n\2lk\3"+
		"\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2o\36\3\2\2\2pn\3\2\2\2qs\5\23\n\2"+
		"rq\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3\2\2\2u \3\2\2\2v|\7$\2\2w{\n\4\2\2"+
		"xy\7^\2\2y{\7$\2\2zw\3\2\2\2zx\3\2\2\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2\2}"+
		"\177\3\2\2\2~|\3\2\2\2\177\u0080\7$\2\2\u0080\"\3\2\2\2\u0081\u0082\7"+
		"/\2\2\u0082\u0083\7@\2\2\u0083$\3\2\2\2\u0084\u0088\t\5\2\2\u0085\u0086"+
		"\7\17\2\2\u0086\u0088\7\f\2\2\u0087\u0084\3\2\2\2\u0087\u0085\3\2\2\2"+
		"\u0088&\3\2\2\2\u0089\u008a\7\"\2\2\u008a\u008b\3\2\2\2\u008b\u008c\b"+
		"\24\2\2\u008c(\3\2\2\2\u008d\u008e\7<\2\2\u008e*\3\2\2\2\u008f\u0090\7"+
		"}\2\2\u0090,\3\2\2\2\u0091\u0092\7\177\2\2\u0092.\3\2\2\2\u0093\u0094"+
		"\7*\2\2\u0094\60\3\2\2\2\u0095\u0096\7+\2\2\u0096\62\3\2\2\2\u0097\u0098"+
		"\7.\2\2\u0098\64\3\2\2\2\u0099\u009a\7\60\2\2\u009a\66\3\2\2\2\13\2^c"+
		"hntz|\u0087\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}