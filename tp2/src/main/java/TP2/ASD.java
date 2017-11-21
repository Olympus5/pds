package TP2;

import com.sun.org.apache.bcel.internal.generic.Instruction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ASD {
    static SymbolTable symTable = new SymbolTable();
    static String scope = "main";
    static SymbolTable scopeTable = symTable;

    static public class Program {
        List<Prototype> p;
        List<Function> f;

        public Program(List<Prototype> p, List<Function> f) {
            this.p = p;
            this.f = f;
        }

        public String pp() {
            String ret = "";

            for(Prototype proto : p) {
                ret += proto.pp() + "\n";
            }

            for(Function fun : f) {
                ret += fun.pp();
            }

            return ret;
        }

        public Llvm.IR toIR() throws TypeException {
            Function.RetFunction ret = null;

            for(Function fun : f) {
                if(ret == null) ret = fun.toIR();
                else ret.ir.append(fun.toIR().ir);
            }

            return ret.ir;
        }
    }

    static public abstract class Prototype {
        String name;
        List<String> attrs;

        public Prototype(String name, List<String> attrs) {
            this.name = name;
            this.attrs = attrs;
        }

        public abstract String pp();
    }


    static public class IntPrototype extends Prototype {
        public IntPrototype(String name, List<String> attrs) {
            super(name, attrs);
            SymbolTable paramTable = new SymbolTable(symTable);

            for(String attr : super.attrs) {
                if(!paramTable.add(new SymbolTable.VariableSymbol(new IntType(), attr))) System.err.println(attr + "(attr): symbole déjà existant");
            }

            SymbolTable.Symbol sym = new SymbolTable.FunctionSymbol(new IntType(), name, paramTable, true);//TODO: Ajouter les arguments
            //On stocke la variable en table des symboles pour par la suite pouvoir travailler avec (affectation, calcul sur des exprs, etc.)
            if (!symTable.add(sym)) System.err.println(name + ": symbole déjà existant");
        }

        public String pp() {
            String ret = "PROTO INT " + this.name + "(";

            int i = 0;
            for(String attr : super.attrs) {
                if(i == 0) {
                    i++;
                } else {
                    ret += ",";
                }

                ret += attr;
            }

            return ret+")\n";
        }
    }

    static public class VoidPrototype extends Prototype {
        public VoidPrototype(String name, List<String> attrs) {
            super(name, attrs);
            SymbolTable paramTable = new SymbolTable(symTable);

            int i = 0;
            for(String attr : super.attrs) {
                if(!paramTable.add(new SymbolTable.VariableSymbol(new IntType(), attr))) System.err.println(attr + "(attr): symbole déjà existant");
                if(!paramTable.add(new SymbolTable.VariableSymbol(new IntType(), attr + "1"))) System.err.println(attr + "(attr): symbole déjà existant");
            }

            SymbolTable.Symbol sym = new SymbolTable.FunctionSymbol(new VoidType(), name, paramTable, true);//TODO: Ajouter les arguments
            //On stocke la variable en table des symboles pour par la suite pouvoir travailler avec (affectation, calcul sur des exprs, etc.)
            if (!symTable.add(sym)) System.err.println(name + ": symbole déjà existant");
        }

        public String pp() {
            String ret = "PROTO VOID " + this.name + "(";

            int i = 0;
            for(String attr : super.attrs) {
                if(i == 0) {
                    i++;
                } else {
                    ret += ",";
                }

                ret += attr;
            }

            return ret+")\n";
        }
    }

    /**
     * Object representation of Variant Function
     */
    static public abstract class Function {
        /**
         * @var String
         */
        String name;

        /**
         * @var List<String>
         */
        List<String> attrs;

        /**
         * Default constructor
         */
        private Function() {}

        /**
         * Constructor
         * @param name the function name
         * @param attrs the list of attributs
         */
        public Function(String name, List<String> attrs) {
            this.name = name;
            this.attrs = attrs;
        }

        /**
         * Pretty-Printer
         * @return the VSL+ function code on string representation
         */
        public abstract String pp();

        /**
         * Generate an object that abstract the LLVM function code
         * @return the LLVM abstraction
         */
        public abstract RetFunction toIR() throws TypeException;

        /**
         * Object reprensation of Function instructions
         */
        static public class RetFunction {
            // The LLVM IR:
            public Llvm.IR ir;
            // And additional stuff:
            public Type type; // The type of the function
            public String result; // The name containing the function result
            // (either an identifier, or an immediate value)

            public RetFunction(Llvm.IR ir, Type type, String result) {
                this.ir = ir;
                this.type = type;
                this.result = result;
            }
        }
    }

    /**
     * Object representation of Constructor MainFunction
     */
    static public class MainFunction extends Function {
        /**
         * @var List
         */
        List<Variable> vars;

        /**
         * @var Bloc
         */
        Bloc b;

        /**
         * Default Constructor
         */
        private MainFunction() {}

        /**
         * Constructor
         */
        public MainFunction(String name, List<String> attrs, List<Variable> vars, Bloc b) {
            super(name, attrs);
            this.vars = vars;
            this.b = b;
        }

        @Override
        public String pp() {
            String ret = "FUNC INT " + super.name + " (){\n";

            if(!vars.isEmpty()) {
                Iterator<Variable> it = vars.iterator();

                ret += "INT " + it.next().pp();

                while(it.hasNext()) {
                    ret += ", " + it.next().pp();
                }
            }

            return ret + "\n" + b.pp() + "\n}\n";
        }

        @Override
        public RetFunction toIR() throws TypeException {
            scopeTable = symTable;
            Variable.RetVariable retVar = null;
            RetFunction retFun;
            String result = "";

            retFun = new RetFunction((new Llvm.IR(Llvm.empty(), Llvm.empty())), new IntType(), result);

            Llvm.Commentary moduleID = new Llvm.Commentary("ModuleID = 'main'");
            Llvm.Instruction decl = new Llvm.Define((new IntType()).toLlvmType(), "@" + this.name, this.attrs);
            Llvm.Instruction entry = new Llvm.Label("entry");
            Llvm.Instruction end = new Llvm.EndFunction();

            retFun.ir.appendHeader(moduleID);
            retFun.ir.appendCode(decl);
            retFun.ir.appendCode(entry);

            if(!vars.isEmpty()) {
                Iterator<Variable> it = vars.iterator();

                retVar = it.next().toIR();

                while (it.hasNext()) {
                    retVar.ir.append(it.next().toIR().ir);
                }

                retFun.ir.append(retVar.ir);
            }

            Instruction.RetInstruction retIns = b.toIR();

            if(retIns != null) retFun.ir.append(retIns.ir);

            Llvm.Instruction ret = new Llvm.Return((new IntType()).toLlvmType(), "0");

            retFun.ir.appendCode(ret);

            retFun.ir.appendCode(end);

            return retFun;
        }
    }

    /**
     * Object representation of constructor IntFunction
     */
    static public class IntFunction extends Function {
        /**
         * @var Bloc
         */
        Bloc b;

        /**
         * Default Constructor
         */
        private IntFunction() {}

        /**
         * Constructor
         */
        public IntFunction(String name, List<String> attrs, Bloc b) {
            super(name, attrs);
            this.b = b;
        }

        @Override
        public String pp() {
            String ret = "FUNC INT " + super.name + " (";

            return ret + "){\n" + "\n" + b.pp() + "\n}\n";
        }

        @Override
        public RetFunction toIR() throws TypeException {
            SymbolTable.FunctionSymbol sym = (SymbolTable.FunctionSymbol) symTable.lookup(super.name);

            if(sym == null) {
                System.err.println("Erreur, symbole '" + super.name + "' non présent dans la table des symboles");
                System.exit(0);
            } else {
                scopeTable = sym.arguments;
            }

            scope = super.name;

            RetFunction retFun;
            String result = "";

            retFun = new RetFunction((new Llvm.IR(Llvm.empty(), Llvm.empty())), new IntType(), result);

            Llvm.Instruction decl = new Llvm.Define((new IntType()).toLlvmType(), "@" + this.name, this.attrs);
            Llvm.Instruction entry = new Llvm.Label("entry");
            Llvm.Instruction attrs = new Llvm.Attrs(this.attrs);
            Llvm.Instruction end = new Llvm.EndFunction();

            retFun.ir.appendCode(decl);
            retFun.ir.appendCode(entry);
            retFun.ir.appendCode(attrs);

            Instruction.RetInstruction retIns = b.toIR();

            if(retIns != null) retFun.ir.append(retIns.ir);

            Llvm.Instruction ret = new Llvm.Return((new IntType()).toLlvmType(), "0");

            retFun.ir.appendCode(ret);

            retFun.ir.appendCode(end);

            return retFun;
        }
    }

    static public class Bloc {
        Sequence s;

        public Bloc(Sequence s) {
            this.s = s;
        }

        // Pretty-printer
        public String pp() {
            return "{\n" + s.pp() + "}\n";
        }

        // IR generation
        public Instruction.RetInstruction toIR() throws TypeException {
            return s.toIR();
        }
    }

    static public class Sequence {
        List < Instruction > i;

        public Sequence(List < Instruction > i) {
            this.i = i;
        }

        public String pp() {
            String ret = "";

            if (!this.i.isEmpty()) {
                Iterator < Instruction > it = this.i.iterator();

                while (it.hasNext()) {
                    ret += it.next().pp() + "\n";
                }
            }

            return ret;
        }

        public Instruction.RetInstruction toIR() throws TypeException {
            Instruction.RetInstruction retIns = null;

            if (!this.i.isEmpty()) {
                if (!this.i.isEmpty()) {
                    Iterator < Instruction > it = this.i.iterator();
                    // computes the IR of the variable
                    retIns = it.next().toIR();

                    while (it.hasNext()) {
                        retIns.ir.append(it.next().toIR().ir);
                    }
                }
            }

            return retIns;
        }
    }

    // All toIR methods returns the IR, plus extra information (synthesized attributes)
    // They can take extra arguments (inherited attributes)

    static public abstract class Expression {
        public abstract String pp();
        public abstract RetExpression toIR() throws TypeException;

        // Object returned by toIR on expressions, with IR + synthesized attributes
        static public class RetExpression {
            // The LLVM IR:
            public Llvm.IR ir;
            // And additional stuff:
            public Type type; // The type of the expression
            public String result; // The name containing the expression's result
            // (either an identifier, or an immediate value)

            public RetExpression(Llvm.IR ir, Type type, String result) {
                this.ir = ir;
                this.type = type;
                this.result = result;
            }
        }
    }

    // Concrete class for Expression: add case
    static public class AddExpression extends Expression {
        Expression left;
        Expression right;

        public AddExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        // Pretty-printer
        public String pp() {
            return "(" + left.pp() + " + " + right.pp() + ")";
        }

        // IR generation
        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            // We check if the types mismatches
            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            // We base our build on the left generated IR:
            // append right code

            // allocate a new identifier for the result
            String result = Utils.newtmp();

            // new add instruction result = left + right
            Llvm.Instruction add = new Llvm.Add(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);
            leftRet.ir.append(rightRet.ir);

            // append this instruction
            leftRet.ir.appendCode(add);

            // return the generated IR, plus the type of this expression
            // and where to find its result
            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }

    // Concrete class for Expression: sub case
    static public class SubExpression extends Expression {
        Expression left;
        Expression right;

        public SubExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String pp() {
            return "(" + left.pp() + " - " + right.pp() + ")";
        }

        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            leftRet.ir.append(rightRet.ir);

            String result = Utils.newtmp();

            Llvm.Instruction sub = new Llvm.Sub(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            leftRet.ir.appendCode(sub);

            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }

    // Concrete class for Expression: mul case
    static public class MulExpression extends Expression {
        Expression left;
        Expression right;

        public MulExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String pp() {
            return "(" + left.pp() + " * " + right.pp() + ")";
        }

        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            leftRet.ir.append(rightRet.ir);

            String result = Utils.newtmp();

            Llvm.Instruction mul = new Llvm.Mul(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            leftRet.ir.appendCode(mul);

            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }

    //Concrete class for Expression: udiv case
    static public class DivExpression extends Expression {
        Expression left;
        Expression right;

        public DivExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String pp() {
            return "(" + left.pp() + " / " + right.pp() + ")";
        }

        public RetExpression toIR() throws TypeException {
            RetExpression leftRet = left.toIR();
            RetExpression rightRet = right.toIR();

            if (!leftRet.type.equals(rightRet.type)) {
                throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
            }

            leftRet.ir.append(rightRet.ir);

            String result = Utils.newtmp();

            Llvm.Instruction div = new Llvm.Div(leftRet.type.toLlvmType(), leftRet.result, rightRet.result, result);

            leftRet.ir.appendCode(div);

            return new RetExpression(leftRet.ir, leftRet.type, result);
        }
    }

    // Concrete class for Expression: constant (integer) case
    static public class IntegerExpression extends Expression {
        int value;
        public IntegerExpression(int value) {
            this.value = value;
        }

        public String pp() {
            return "" + value;
        }

        public RetExpression toIR() throws TypeException {
            // Here we simply return an empty IR
            // the `result' of this expression is the integer itself (as string)
            return new RetExpression(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), "" + value);
        }
    }

    static public class VariableExpression extends Expression {
        String name;

        public VariableExpression(String name) {
            this.name = name;
        }

        public String pp() {
            return "" + this.name;
        }

        //%res = load i32, i32* %var

        public RetExpression toIR() throws TypeException {
            SymbolTable.VariableSymbol sym = (SymbolTable.VariableSymbol) scopeTable.lookup(this.name);
            Expression.RetExpression ret = null;
            String name1 = this.name;

            if (sym != null) {
                String result = Utils.newglob(this.name);

                if (!scopeTable.add(new SymbolTable.VariableSymbol(sym.type, result))) {
                    System.err.println("Erreur, symbole '" + result + "' déjà déclaré dans la table des symbole");
                    System.exit(0);
                }

                if(!scope.equals("main")) name1 += "1";

                Llvm.Instruction load = new Llvm.Load(new IntType().toLlvmType(), "%" + result, new IntType().toLlvmType(), "%" + name1);

                ret = new RetExpression(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), "%" + result);

                ret.ir.appendCode(load);
            } else {
                System.err.println("Erreur, symbole '" + this.name + "' non présent dans la table des symboles");
                System.exit(0);
            }

            return ret;
        }
    }

    static public abstract class Variable {
        public abstract String pp();
        public abstract RetVariable toIR() throws TypeException;

        static public class RetVariable {
            //The LLVM IR:
            public Llvm.IR ir;
            //And additional stuff
            public Type type; // The type of the variable
            public String result; // The name containing the expression's result
            // (either an identifier, or an immediate value)

            public RetVariable(Llvm.IR ir, Type type, String result) {
                this.ir = ir;
                this.type = type;
                this.result = result;
            }
        }
    }

    // Concrete class for Variable: integer variable case
    static public class IntegerVariable extends Variable {
        String name;

        public IntegerVariable(String name) {
            this.name = name;
            SymbolTable.Symbol sym = new SymbolTable.VariableSymbol(new IntType(), name);
            //On stocke la variable en table des symboles pour par la suite pouvoir travailler avec (affectation, calcul sur des exprs, etc.)
            if (!symTable.add(sym)) System.err.println(name + ": symbole déjà existant");
        }

        public String pp() {
            return this.name;
        }

        public RetVariable toIR() throws TypeException {
            String result = "%" + this.name;
            SymbolTable.Symbol var;

            Llvm.Instruction varInt = new Llvm.VarInt((new IntType()).toLlvmType(), result);

            RetVariable ret = new RetVariable(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);
            ret.ir.appendCode(varInt);

            return ret;
        }
    }

    //TODO: A REFAIRE !!!!!!!!!!!
    // Concrete class for Variable: tab variable case
    static public class TabVariable extends Variable {
        String name;
        int size;

        public TabVariable(String name, int size) {
            this.name = name;
            this.size = size;

            //TODO: attention un tableau de taille n est une autre table de symbole avec n éléments !!! A corriger
            //On stocke la variable en table des symboles pour par la suite pouvoir travailler avec (affectation, calcul sur des exprs, etc.)
            symTable.add(new SymbolTable.VariableSymbol(new IntType(), name));
        }

        public String pp() {
            return this.name + "[" + this.size + "]";
        }

        public RetVariable toIR() throws TypeException {
            String result = "%" + name;

            Llvm.Instruction varTab = new Llvm.VarTab((new IntType()).toLlvmType(), (new IntType()).toLlvmType(), size, result);

            RetVariable ret = new RetVariable(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);
            ret.ir.appendCode(varTab);

            return ret;
        }
    }

    static public abstract class Instruction {
        public abstract String pp();

        public abstract RetInstruction toIR() throws TypeException;

        static public class RetInstruction {
            // The LLVM IR:
            public Llvm.IR ir;
            // And additional stuff:
            public Type type; // The type of the instruction
            public String result; // The name containing the instruction's result
            // (either an identifier, or an immediate value)

            public RetInstruction(Llvm.IR ir, Type type, String result) {
                this.ir = ir;
                this.type = type;
                this.result = result;
            }
        }
    }

    // Concrete class for Instruction: store case
    static public class AffInstruction extends Instruction {
        String left;
        Expression right;

        public AffInstruction(String left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String pp() {
            return left + " := " + right.pp();
        }

        public RetInstruction toIR() throws TypeException {
            SymbolTable.VariableSymbol leftRet = (SymbolTable.VariableSymbol) scopeTable.lookup(left);
            RetInstruction ret = null;
            String result = "";
            Expression.RetExpression rightRet = right.toIR();
            String name1 = leftRet.ident;

            if (leftRet != null) {
                // We check if the types mismatches
                if (!leftRet.type.equals(rightRet.type)) {
                    throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
                }

                if(!scope.equals("main")) name1 += "1";

                // new store instruction result = left := right
                Llvm.Instruction aff = new Llvm.Aff(rightRet.type.toLlvmType(), rightRet.result, leftRet.type.toLlvmType(), "%" + name1);

                ret = new RetInstruction(new Llvm.IR(Llvm.empty(), Llvm.empty()), leftRet.type, result);

                // append this instruction
                ret.ir.appendCode(aff);
                rightRet.ir.append(ret.ir);
            } else {
                System.err.println("Erreur, '" + left + "' symbole non présent dans la table des symboles");
                System.exit(0);
            }

            return new RetInstruction(rightRet.ir, leftRet.type, result);
        }
    }

    static public class IfInstruction extends Instruction {
        Expression e;
        Bloc b;

        public IfInstruction(Expression e, Bloc b) {
            this.e = e;
            this.b = b;
        }

        public String pp() {
            return "IF " + e.pp() + "\nTHEN\n" + b.pp() + "FI";
        }

        public RetInstruction toIR() throws TypeException {
            Expression.RetExpression cond = e.toIR();

            // We check if the types mismatches
            if (!cond.type.equals(new IntType())) {
                throw new TypeException("type mismatch: have ");
            }

            String result = Utils.newtmp();
            String iftrue = Utils.newlab("IF");
            String iffalse = Utils.newlab("FI");

            Llvm.Instruction iF = new Llvm.Icmp(cond.type.toLlvmType(), cond.result, result);
            Llvm.Instruction brCond = new Llvm.BrCond(result, "%" + iftrue, "%" + iffalse);
            Llvm.Instruction brUncond = new Llvm.BrUncond("%" + iffalse);

            RetInstruction ret = new RetInstruction(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);

            ret.ir.append(cond.ir);
            ret.ir.appendCode(iF);
            ret.ir.appendCode(brCond);
            ret.ir.appendCode(new Llvm.Label(iftrue));
            ret.ir.append(b.toIR().ir);
            ret.ir.appendCode(brUncond);
            ret.ir.appendCode(new Llvm.Label(iffalse));

            return ret;
        }
    }

    static public class IfElseInstruction extends Instruction {
        Expression e;
        Bloc b1;
        Bloc b2;

        public IfElseInstruction(Expression e, Bloc b1, Bloc b2) {
            this.e = e;
            this.b1 = b1;
            this.b2 = b2;
        }

        public String pp() {
            return "IF " + e.pp() + "\nTHEN\n" + b1.pp() + "\nELSE\n" + b2.pp() + "FI";
        }

        public RetInstruction toIR() throws TypeException {
            Expression.RetExpression cond = e.toIR();

            // We check if the types mismatches
            if (!cond.type.equals(new IntType())) {
                throw new TypeException("type mismatch: have ");
            }

            String result = Utils.newtmp();
            String iftrue = Utils.newlab("IF");
            String iffalse = Utils.newlab("ELSE");
            String endif = Utils.newlab("FI");

            Llvm.Instruction iF = new Llvm.Icmp(cond.type.toLlvmType(), cond.result, result);
            Llvm.Instruction brCond = new Llvm.BrCond(result, "%" + iftrue, "%" + iffalse);
            Llvm.Instruction brUncond = new Llvm.BrUncond("%" + endif);

            RetInstruction ret = new RetInstruction(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);

            ret.ir.append(cond.ir);
            ret.ir.appendCode(iF);
            ret.ir.appendCode(brCond);
            ret.ir.appendCode(new Llvm.Label(iftrue));
            ret.ir.append(b1.toIR().ir);
            ret.ir.appendCode(brUncond);
            ret.ir.appendCode(new Llvm.Label(iffalse));
            ret.ir.append(b2.toIR().ir);
            ret.ir.appendCode(brUncond);
            ret.ir.appendCode(new Llvm.Label(endif));

            return ret;
        }
    }

    static public class WhileInstruction extends Instruction {
        Expression e;
        Bloc b;

        public WhileInstruction(Expression e, Bloc b) {
            this.e = e;
            this.b = b;
        }

        public String pp() {
            return "WHILE " + e.pp() + " \nDO\n " + b.pp() + " DONE\n";
        }

        public RetInstruction toIR() throws TypeException {
            Expression.RetExpression cond = e.toIR();

            // We check if the types mismatches
            if (!cond.type.equals(new IntType())) {
                throw new TypeException("type mismatch: have ");
            }

            String result = Utils.newtmp();
            String loop = Utils.newlab("LOOP");
            String dO = Utils.newlab("DO");
            String done = Utils.newlab("DONE");

            Llvm.Instruction whilE = new Llvm.Icmp(cond.type.toLlvmType(), cond.result, result);
            Llvm.Instruction brCond = new Llvm.BrCond(result, "%" + dO, "%" + done);
            Llvm.Instruction brUncond = new Llvm.BrUncond("%" + loop);

            RetInstruction ret = new RetInstruction(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);

            ret.ir.appendCode(brUncond);
            ret.ir.appendCode(new Llvm.Label(loop));
            ret.ir.append(cond.ir);
            ret.ir.appendCode(whilE);
            ret.ir.appendCode(brCond);
            ret.ir.appendCode(new Llvm.Label(dO));
            ret.ir.append(b.toIR().ir);
            ret.ir.appendCode(brUncond);
            ret.ir.appendCode(new Llvm.Label(done));

            return ret;
        }
    }

    static public class Return extends Instruction {
        Expression e;

        public Return(Expression e) {
            this.e = e;
        }

        public String pp() {
            return "RETURN " + e.pp() + "\n";
        }

        public RetInstruction toIR() throws TypeException {
            
        }
    }

    // Warning: this is the type from VSL+, not the LLVM types!
    static public abstract class Type {
        public abstract String pp();
        public abstract Llvm.Type toLlvmType();
    }

    static class IntType extends Type {
        public String pp() {
            return "INT";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IntType;
        }

        public Llvm.Type toLlvmType() {
            return new Llvm.IntType();
        }
    }

    static class VoidType extends Type {
        public String pp() {
            return "VOID";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof VoidType;
        }

        public Llvm.Type toLlvmType() {
            return new Llvm.VoidType();
        }
    }
}
