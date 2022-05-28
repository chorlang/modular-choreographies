parser grammar SimpleParser;

options {tokenVocab = SimpleLexer;}
/*
 * Parser rule
 */
start               :   program EOF;
program             :   procedure=WORD OPENPARA (prefix)? CLOSEPARA COLON OPENPARA (procReturn)? CLOSEPARA OPENCURLY (ISEP)? c CLOSECURLY (ISEP program)? | ;
prefix              :   process=WORD COLON PROC OPENPARA definition CLOSEPARA (COMMA prefix)? | ;
procReturn          :   process=WORD COLON PROC OPENPARA procReturnValues CLOSEPARA (COMMA procReturn)? | ;
procReturnValues    :   WORD (COMMA WORD)* | ;
definition          :   functiondefinition | variabledefinition | ;
variabledefinition  :   id=WORD COLON type=WORD (COMMA definition)?;
functiondefinition  :   id=WORD COLON OPENPARA intParameters CLOSEPARA ARROW returnvalue=WORD (COMMA definition)?;
intParameters       :   intParameter (COMMA intParameters)? | ;
intParameter        :   WORD;
c                   :   instruction (ISEP c)? | ;
instruction         :   interaction | statefulInteraction | variableInteraction | returnState | procInvoke | ifState | declaration | initialization | initialDeclara | selection;
initialization      :   process=WORD PERIOD var=WORD EQUAL e ;
initialDeclara      :   VAR process=WORD PERIOD var=WORD COLON type=WORD EQUAL e ;
declaration         :   VAR process=WORD PERIOD varName=WORD COLON type=WORD ;
ifState             :   IF process=WORD PERIOD e OPENCURLY (ISEP)? c  (ISEP)? CLOSECURLY ELSE OPENCURLY (ISEP)? c (ISEP)? CLOSECURLY ;
returnState         :   RETURN returnValue;
returnValue         :   process=WORD PERIOD returnOptions (COMMA returnValue)? | ;
returnOptions       :   OPENPARA eList CLOSEPARA | e ;
eList               :   e (COMMA eList)? | ;
procInvoke          :   (invokeargs EQUAL)? procedure=WORD OPENPARA returnValue CLOSEPARA;
invokeargs          :   process=WORD PERIOD argsvalues (COMMA invokeargs)?;
argsvalues          :   OPENPARA e (COMMA e)? CLOSEPARA | e ;
interaction         :   sender=WORD ARROW receiver=WORD;
statefulInteraction :   sender=WORD PERIOD e ARROW receiver=WORD PERIOD variable=WORD;
variableInteraction :   sender=WORD PERIOD e ARROW VAR receiver=WORD PERIOD variable=WORD COLON type=WORD ;
selection           :   sender=WORD ARROW receiver=WORD OPENSQUA label=WORD CLOSESQUA ;
e                   :   function | NUMBER | STRING | BOOL | FLOAT | WORD ;
function            :   functionName=WORD OPENPARA parameters CLOSEPARA ;
parameters          :   (process=WORD PERIOD)? e (COMMA parameters)? | ;

