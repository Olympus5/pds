lexer grammar VSLLexer;

options {
  language = Java;
}

@header {
  package TP2;
}

WS : (' '|'\n'|'\t') -> skip
   ;

COMMENT : '//' (~'\n')* -> skip
        ;

fragment LETTER : 'a'..'z' ;
fragment DIGIT  : '0'..'9' ;
fragment ASCII  : ~('\n'|'"');

// keywords
LP	: '(' ; // Left parenthesis
RP	: ')' ;
PLUS	: '+';
MINUS	: '-';
TIMES	: '*';
DIV	: '/';
AFF	: ':=';
INT : 'INT';
LB : '['; //left bracket
RB : ']';
COMMA : ',';
LBB : '{';
RBB : '}';
IF : 'IF';
THEN : 'THEN';
ELSE : 'ELSE';
ENDIF : 'FI';
WHILE : 'WHILE';
DO : 'DO';
DONE : 'DONE';
PROTO : 'PROTO';
VOID : 'VOID';
FUNC : 'FUNC';
MAIN : 'main';
RETURN : 'RETURN';


// other tokens (no conflict with keywords in VSL)
IDENT   : LETTER (LETTER|DIGIT)*;
TEXT    : '"' (ASCII)* '"' { setText(getText().substring(1, getText().length() - 1)); };
INTEGER : (DIGIT)+ ;
