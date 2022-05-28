// Generated from C:/Users/annem/IdeaProjects/Master_Parser/src/main/java/parser\SimpleParser.g4 by ANTLR 4.9.1
package parser.SimpleParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SimpleParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SimpleParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SimpleParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(SimpleParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SimpleParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#prefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefix(SimpleParser.PrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#procReturn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcReturn(SimpleParser.ProcReturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#procReturnValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcReturnValues(SimpleParser.ProcReturnValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#definition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinition(SimpleParser.DefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#variabledefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariabledefinition(SimpleParser.VariabledefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#functiondefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctiondefinition(SimpleParser.FunctiondefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#intParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntParameters(SimpleParser.IntParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#intParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntParameter(SimpleParser.IntParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#c}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitC(SimpleParser.CContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(SimpleParser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#initialization}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialization(SimpleParser.InitializationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#initialDeclara}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialDeclara(SimpleParser.InitialDeclaraContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(SimpleParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#ifState}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfState(SimpleParser.IfStateContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#returnState}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnState(SimpleParser.ReturnStateContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#returnValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnValue(SimpleParser.ReturnValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#returnOptions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnOptions(SimpleParser.ReturnOptionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#eList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEList(SimpleParser.EListContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#procInvoke}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcInvoke(SimpleParser.ProcInvokeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#invokeargs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvokeargs(SimpleParser.InvokeargsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#argsvalues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgsvalues(SimpleParser.ArgsvaluesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#interaction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteraction(SimpleParser.InteractionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#statefulInteraction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatefulInteraction(SimpleParser.StatefulInteractionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#variableInteraction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableInteraction(SimpleParser.VariableInteractionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#selection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelection(SimpleParser.SelectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#e}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitE(SimpleParser.EContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(SimpleParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(SimpleParser.ParametersContext ctx);
}