lexer grammar SimpleLexer;
/*
 * Lexer rule
 */
fragment P          :   'p';
fragment R          :   'r';
fragment O          :   'o';
fragment C          :   'c';
fragment E          :   'e';
fragment T          :   't';
fragment U          :   'u';
fragment N          :   'n';
fragment I          :   'i';
fragment F          :   'f';
fragment L          :   'l';
fragment S          :   's';
fragment V          :   'v';
fragment A          :   'a';
fragment DIGIT      :   [0-9];


PROC        :   P R O C;
RETURN      :   R E T U R N;
IF          :   I F;
ELSE        :   E L S E;
VAR         :   V A R;
BOOL        :   'true' | 'false';
WORD        :   [a-zA-Z]+;
FLOAT       :   DIGIT+ '.' DIGIT*;
NUMBER      :   DIGIT+;
STRING      :   '"' (~'"'|'\\"')* '"';


EQUAL       :   '=';
ARROW       :   '->';
ISEP        :   ';' | '\n' | '\r\n' ;
WHITESPACE  :   ' ' -> skip;
COLON       :   ':';
OPENCURLY   :   '{';
CLOSECURLY  :   '}';
OPENPARA    :   '(';
CLOSEPARA   :   ')';
OPENSQUA    :   '[';
CLOSESQUA   :   ']';
COMMA       :   ',';
PERIOD      :   '.';