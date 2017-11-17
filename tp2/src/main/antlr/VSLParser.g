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
    /*: b=bloc { $out = new ASD.Program(null, $b.out); }
    | v=variable e=expression { $out = new ASD.Program($v.out, $e.out, null); }
    | v=variable i=instruction { $out = new ASD.Program($v.out, null, $i.out); }*/
    : p=prototype f=function { $out = new ASD.Program($p.out, $f.out); }
    ;

prototype returns [List<ASD.Prototype> out] locals [List<String> attr, String nom]
  : { $out = new ArrayList(); } ({ $attr = new ArrayList<String>(); } (PROTO INT IDENT { $nom = $IDENT.text; } LP ((IDENT { $attr.add($IDENT.text); }) (COMMA IDENT { $attr.add($IDENT.text); })*)? RP { $out.add(new ASD.IntPrototype($nom, $attr)); }//TODO: Ajouter variable
                                          | PROTO VOID IDENT { $nom = $IDENT.text; } LP ((IDENT { $attr.add($IDENT.text); }) (COMMA IDENT { $attr.add($IDENT.text); })*)? RP { $out.add(new ASD.VoidPrototype($nom, $attr)); }))*
  ;

function returns [List<ASD.Function> out] locals [List<String> attr, String nom]
  //: { $attr = new ArrayList<String>(); } FUNC INT MAIN LP ((IDENT { $attr.add($IDENT.text); }) (COMMA IDENT { $attr.add($IDENT.text); })*)? RP v=variable b=bloc { $out = new ASD.MainFunction(); }
  : {$out = new ArrayList<ASD.Function>(); }
    (FUNC INT MAIN LP RP LBB v=variable b=bloc RBB { $out.add(new ASD.MainFunction($MAIN.text, new ArrayList<String>(), $v.out, $b.out)); }
  | FUNC INT IDENT {$nom = $IDENT.text; } LP ((IDENT { $attr.add($IDENT.text); }) (COMMA IDENT { $attr.add($IDENT.text); })*)? RP LBB b=bloc RBB { $out.add(new ASD.IntFunction($nom, $attr, $b.out)); })+
  /*| FUNC VOID IDENT LP RP b=bloc { $out = new VoidFunction($IDENT.text, $b.out); }*/
  ;

bloc returns [ASD.Bloc out]
  : LBB s=sequence RBB { $out = new ASD.Bloc($s.out); }
  | s=sequence { $out = new ASD.Bloc($s.out); }
  ;

sequence returns [ASD.Sequence out]
  : i=instruction { $out = new ASD.Sequence($i.out); }
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
    : INTEGER { $out = new ASD.IntegerExpression($INTEGER.int); }//Constante dans une expression
    | IDENT { $out = new ASD.VariableExpression($IDENT.text); }//Variable dans une expression
    | LP e=expression RP { $out = $e.out; }//Expression entre parenthèses
    ;

instruction returns [List<ASD.Instruction> out]
	: { $out = new ArrayList<ASD.Instruction>(); }(IDENT AFF e=expression { $out.add(new ASD.AffInstruction($IDENT.text, $e.out)); }
  | IF e=expression THEN b=bloc ENDIF { $out.add(new ASD.IfInstruction($e.out, $b.out)); }
  | IF e=expression THEN b1=bloc ELSE b2=bloc ENDIF { $out.add(new ASD.IfElseInstruction($e.out, $b1.out, $b2.out)); }
  | WHILE e=expression DO b=bloc DONE { $out.add(new ASD.WhileInstruction($e.out, $b.out)); })*
  ;
