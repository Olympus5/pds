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


// TODO : other rules

program returns [ASD.Program out]
    : e=expression { $out = new ASD.Program($e.out); }
    | v=variable e=expression { $out = new ASD.Program($v.out, $e.out); }// TODO : change when you extend the language
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
    // TODO : that's all?
    ;

factor returns [ASD.Expression out]
    : l=primary TIMES r=factor { $out = new ASD.MulExpression($l.out, $r.out); }
    | l=primary DIV r=factor { $out = new ASD.DivExpression($l.out, $r.out); }
    | p=primary { $out = $p.out; }
    // TODO : that's all?
    ;

primary returns [ASD.Expression out]
    : INTEGER { $out = new ASD.IntegerExpression($INTEGER.int); }
    | LP e=expression RP { $out = $e.out; }
    // TODO : that's all?
    ;
/*instruction returns [ASD.Instruction out]
	: IDENT AFF e=expression { $out = new ASD.AffInstruction($e.out); }
	;*/
