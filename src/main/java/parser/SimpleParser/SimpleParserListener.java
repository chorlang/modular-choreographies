// Generated from C:/Users/annem/IdeaProjects/Master_Parser/src/main/java/parser\SimpleParser.g4 by ANTLR 4.9.1
package parser.SimpleParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SimpleParser}.
 */
public interface SimpleParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SimpleParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(SimpleParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(SimpleParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SimpleParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SimpleParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#prefix}.
	 * @param ctx the parse tree
	 */
	void enterPrefix(SimpleParser.PrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#prefix}.
	 * @param ctx the parse tree
	 */
	void exitPrefix(SimpleParser.PrefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#procReturn}.
	 * @param ctx the parse tree
	 */
	void enterProcReturn(SimpleParser.ProcReturnContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#procReturn}.
	 * @param ctx the parse tree
	 */
	void exitProcReturn(SimpleParser.ProcReturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#procReturnValues}.
	 * @param ctx the parse tree
	 */
	void enterProcReturnValues(SimpleParser.ProcReturnValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#procReturnValues}.
	 * @param ctx the parse tree
	 */
	void exitProcReturnValues(SimpleParser.ProcReturnValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#definition}.
	 * @param ctx the parse tree
	 */
	void enterDefinition(SimpleParser.DefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#definition}.
	 * @param ctx the parse tree
	 */
	void exitDefinition(SimpleParser.DefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#variabledefinition}.
	 * @param ctx the parse tree
	 */
	void enterVariabledefinition(SimpleParser.VariabledefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#variabledefinition}.
	 * @param ctx the parse tree
	 */
	void exitVariabledefinition(SimpleParser.VariabledefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#functiondefinition}.
	 * @param ctx the parse tree
	 */
	void enterFunctiondefinition(SimpleParser.FunctiondefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#functiondefinition}.
	 * @param ctx the parse tree
	 */
	void exitFunctiondefinition(SimpleParser.FunctiondefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#intParameters}.
	 * @param ctx the parse tree
	 */
	void enterIntParameters(SimpleParser.IntParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#intParameters}.
	 * @param ctx the parse tree
	 */
	void exitIntParameters(SimpleParser.IntParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#intParameter}.
	 * @param ctx the parse tree
	 */
	void enterIntParameter(SimpleParser.IntParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#intParameter}.
	 * @param ctx the parse tree
	 */
	void exitIntParameter(SimpleParser.IntParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#c}.
	 * @param ctx the parse tree
	 */
	void enterC(SimpleParser.CContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#c}.
	 * @param ctx the parse tree
	 */
	void exitC(SimpleParser.CContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(SimpleParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(SimpleParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#initialization}.
	 * @param ctx the parse tree
	 */
	void enterInitialization(SimpleParser.InitializationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#initialization}.
	 * @param ctx the parse tree
	 */
	void exitInitialization(SimpleParser.InitializationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#initialDeclara}.
	 * @param ctx the parse tree
	 */
	void enterInitialDeclara(SimpleParser.InitialDeclaraContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#initialDeclara}.
	 * @param ctx the parse tree
	 */
	void exitInitialDeclara(SimpleParser.InitialDeclaraContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(SimpleParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(SimpleParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#ifState}.
	 * @param ctx the parse tree
	 */
	void enterIfState(SimpleParser.IfStateContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#ifState}.
	 * @param ctx the parse tree
	 */
	void exitIfState(SimpleParser.IfStateContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#returnState}.
	 * @param ctx the parse tree
	 */
	void enterReturnState(SimpleParser.ReturnStateContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#returnState}.
	 * @param ctx the parse tree
	 */
	void exitReturnState(SimpleParser.ReturnStateContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#returnValue}.
	 * @param ctx the parse tree
	 */
	void enterReturnValue(SimpleParser.ReturnValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#returnValue}.
	 * @param ctx the parse tree
	 */
	void exitReturnValue(SimpleParser.ReturnValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#returnOptions}.
	 * @param ctx the parse tree
	 */
	void enterReturnOptions(SimpleParser.ReturnOptionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#returnOptions}.
	 * @param ctx the parse tree
	 */
	void exitReturnOptions(SimpleParser.ReturnOptionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#eList}.
	 * @param ctx the parse tree
	 */
	void enterEList(SimpleParser.EListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#eList}.
	 * @param ctx the parse tree
	 */
	void exitEList(SimpleParser.EListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#procInvoke}.
	 * @param ctx the parse tree
	 */
	void enterProcInvoke(SimpleParser.ProcInvokeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#procInvoke}.
	 * @param ctx the parse tree
	 */
	void exitProcInvoke(SimpleParser.ProcInvokeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#invokeargs}.
	 * @param ctx the parse tree
	 */
	void enterInvokeargs(SimpleParser.InvokeargsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#invokeargs}.
	 * @param ctx the parse tree
	 */
	void exitInvokeargs(SimpleParser.InvokeargsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#argsvalues}.
	 * @param ctx the parse tree
	 */
	void enterArgsvalues(SimpleParser.ArgsvaluesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#argsvalues}.
	 * @param ctx the parse tree
	 */
	void exitArgsvalues(SimpleParser.ArgsvaluesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#interaction}.
	 * @param ctx the parse tree
	 */
	void enterInteraction(SimpleParser.InteractionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#interaction}.
	 * @param ctx the parse tree
	 */
	void exitInteraction(SimpleParser.InteractionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#statefulInteraction}.
	 * @param ctx the parse tree
	 */
	void enterStatefulInteraction(SimpleParser.StatefulInteractionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#statefulInteraction}.
	 * @param ctx the parse tree
	 */
	void exitStatefulInteraction(SimpleParser.StatefulInteractionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#variableInteraction}.
	 * @param ctx the parse tree
	 */
	void enterVariableInteraction(SimpleParser.VariableInteractionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#variableInteraction}.
	 * @param ctx the parse tree
	 */
	void exitVariableInteraction(SimpleParser.VariableInteractionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#selection}.
	 * @param ctx the parse tree
	 */
	void enterSelection(SimpleParser.SelectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#selection}.
	 * @param ctx the parse tree
	 */
	void exitSelection(SimpleParser.SelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#e}.
	 * @param ctx the parse tree
	 */
	void enterE(SimpleParser.EContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#e}.
	 * @param ctx the parse tree
	 */
	void exitE(SimpleParser.EContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(SimpleParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(SimpleParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimpleParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(SimpleParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimpleParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(SimpleParser.ParametersContext ctx);
}