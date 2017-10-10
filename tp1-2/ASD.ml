type document = Document of sujet list
and sujet = Sujet of entite * predicat list
and predicat = Predicat of entite * objet list
and entite = Entite of string
and objet = ObjetEnt of entite
			| ObjetTexte of string;;

let rec ntriples_of_ast ast =
	match ast with
	| Document [] -> ""
	| Document s::l -> (ntriples_of_sujet s) :: (ntriples_of_ast l);;

let rec ntriples_of_sujet sujet =
	match sujet with
	| Sujet (e, []) -> ""
	| Sujet (e, p::l) -> (ntriples_of_predicat (ntriple_of_entite e) p) ^ (ntriples_of_sujet l);;

let rec ntriples_of_predicat pref predicat =
	match predicat with
	| Predicat (e, []) -> ""
	| Predicat (e, o::l) -> (ntriples_of_objet (ntriples_of_entite pref e) o) ^ (ntriples_of_predicat pref l);;

let ntriples_of_objet pref objet =
	match objet with
	| ObjetEnt e -> ntriples_of_entite pref e
	| ObjetTexte s -> pref ^ s

let ntriples_of_entite pref entite =
	match entite with
	| Entite s -> pref ^ s;;
