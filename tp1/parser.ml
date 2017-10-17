open ASD


type token =
  | LOWER
  | UPPER
  | QUOTE
  | PTVIRGULE
  | VIRGULE
  | STRING of string

let document = parser
  | [< s=sujet >] -> s
and rec sujet = parser
  | [< e=entite; p=predicat; s=sujet >] -> p
  | [< e=entite; p=predicat >] -> p
and rec predicat = parser
  | [< e=entite; o=objet; p=predicat; 'PTVIRGULE >] -> p
  | [< e=entite; o=objet >] -> o
and rec objet = parser
  | [< 'QUOTE; 'STRING str; 'QUOTE; o=objet; 'VIRGULE>] ->o
  | [< 'QUOTE; 'STRING str; 'QUOTE >] -> o
  | [< e=entite; o=objet; 'VIRGULE >] ->
  | [< e=entite >] -> e
and entite = parser
  | [< 'LOWER; 'STRING str; 'UPPER >] -> Entite str;;
