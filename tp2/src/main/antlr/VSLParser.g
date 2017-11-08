parser grammar VSLParser;

options {
  language = Java;
  tokenVocab = VSLLexer;
}

@header {
  package TP2;

  import java.util.stream.Collectors;
  import java.util.Arrays;
}

program returns [ASD.Program out]
    /*: e=expression { $out = new ASD.Program(null, $e.out, null); }
    | v=variable e=expression { $out = new ASD.Program($v.out, $e.out, null); }
    | v=variable i=instruction { $out = new ASD.Program($v.out, null, $i.out); }*/
    : v=variable b=bloc { $out = new ASD.Program($v.out, $b.out); }
    ;

bloc returns [ASD.Bloc out]
  : LA i=instruction RA { $out = new ASD.Bloc($i.out); }
  | i = instruction { $out = new ASD.Bloc($i.out); }
  ;

variable returns [List<ASD.Variable> out]
    : INT { $out = new ArrayList<ASD.Variable>(); } (IDENT { $out.add(new ASD.IntegerVariable($IDENT.text)); }
                                                    | IDENT LB INTEGER RB { $out.add(new ASD.TabVariable($IDENT.text, $INTEGER.int)); })
                                                    (COMMA IDENT { $out.add(new ASD.IntegerVariable($IDENT.text)); }
                                                    | COMMA IDENT LB INTEGER RB { $out.add(new ASD.TabVariable($IDENT.text, $INTEGER.int)); })*
    ;

expression returns [ASD.Expression out]
    : l=factor PLUS r=expression  { $out = new ASD.AddExpression($l.out, $r.out); }
    | l=factor MINUS r=expression { $out = new ASD.SubExpression($l.out, $r.out); }
    | f=factor { $out = $f.out; }
    ;

factor returns [ASD.Expression out]
    : l=primary TIMES r=factor { $out = new ASD.MulExpression($l.out, $r.out); }
    | l=primary DIV r=factor { $out = new ASD.DivExpression($l.out, $r.out); }
    | p=primary { $out = $p.out; }
    ;

primary returns [ASD.Expression out]
    : INTEGER { $out = new ASD.IntegerExpression($INTEGER.int); }
    | IDENT { $out = new ASD.VariableExpression($IDENT.text); }
    | LP e=expression RP { $out = $e.out; }
    ;

instruction returns [ASD.Instruction out]
	: IDENT AFF e=expression { $out = new ASD.AffInstruction($IDENT.text, $e.out); }
  | IF e=expression THEN b=bloc ENDIF { $out = new ASD.IfInstruction($e.out, $b.out); }
  | IF e=expression THEN b1=bloc ELSE b2=bloc ENDIF { $out = new ASD.IfElseInstruction($e.out, $b1.out, $b2.out); }
  | WHILE e=expression DO b=bloc DONE { $out = new ASD.WhileInstruction($e.out, $b.out); }
	;
