package TP2;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Iterator;

public class ASD {
  static SymbolTable symTable = new SymbolTable();

  static public class Program {
    Expression e; // What a program contains. TODO : change when you extend the language
    List<Variable> v;
    Instruction i;

    public Program(List<Variable> v, Expression e, Instruction i) {
      this.v = v;
      this.e = e;
      this.i = i;
    }

    // Pretty-printer
    public String pp() {
      String ret = "";

      if(v != null) {
        if(!v.isEmpty()) {
          Iterator<Variable> it = v.iterator();
          ret = "INT " + it.next().pp();

          while(it.hasNext()) {
            ret += ", " + it.next().pp();
          }

          ret += "\n";
        }
      }

      ret += (e != null) ? e.pp() : i.pp();

      return ret;
    }

    // IR generation
    public Llvm.IR toIR() throws TypeException {
      // TODO : change when you extend the language
      Variable.RetVariable retVar = null;

      if(v != null) {
        if(!v.isEmpty()) {
          Iterator<Variable> it = v.iterator();
          // computes the IR of the variable
          retVar = it.next().toIR();

          while(it.hasNext()) {
            retVar.ir.append(it.next().toIR().ir);
          }
        }
      }

      if(e != null) {
        // computes the IR of the expression
        Expression.RetExpression retExpr = e.toIR();

        // add a return instruction
        Llvm.Instruction ret = new Llvm.Return(retExpr.type.toLlvmType(), retExpr.result);

        retExpr.ir.appendCode(ret);

        // add expression
        if(v != null) {
          retVar.ir.append(retExpr.ir);
          return retVar.ir;
        }

        return retExpr.ir;
      }

      Instruction.RetInstruction retIns = i.toIR();

      Llvm.Instruction ret = new Llvm.Return((new IntType()).toLlvmType(), "0");

      retIns.ir.appendCode(ret);

      if(v != null) {
        retVar.ir.append(retIns.ir);
        return retVar.ir;
      }

      return retIns.ir;
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
      if(!leftRet.type.equals(rightRet.type)) {
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

      if(!leftRet.type.equals(rightRet.type)) {
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

      if(!leftRet.type.equals(rightRet.type)) {
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

     if(!leftRet.type.equals(rightRet.type)) {
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

    public RetExpression toIR() {
      SymbolTable.VariableSymbol sym = (SymbolTable.VariableSymbol) symTable.lookup(name);
      Expression.RetExpression ret = null;

      if(sym != null) {
        String result = Utils.newlab(this.name);

        if(!symTable.add(new SymbolTable.VariableSymbol(sym.type, result))) {
          System.err.println("Erreur, symbole '"+ result +"' déjà déclaré dans la table des symbole");
          System.exit(0);
        }


        Llvm.Instruction load = new Llvm.Load(new IntType().toLlvmType(), "%" + result, new IntType().toLlvmType(), "%" + this.name);

        ret = new RetExpression(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), "%" + result);

        ret.ir.appendCode(load);
      } else {
        System.err.println("Erreur, symbole '"+ this.name +"' non présent dans la table des symboles");
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
      if(!symTable.add(sym)) System.err.println(name + ": symbole déjà existant");
    }

    public String pp() {
      return this.name;
    }

    public RetVariable toIR() throws TypeException {
      String result = "%" + name;

      Llvm.Instruction varInt = new Llvm.VarInt((new IntType()).toLlvmType(), result);

      RetVariable ret =  new RetVariable(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);
      ret.ir.appendCode(varInt);

      return ret;
    }
  }

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

      RetVariable ret =  new RetVariable(new Llvm.IR(Llvm.empty(), Llvm.empty()), new IntType(), result);
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
      SymbolTable.VariableSymbol leftRet = (SymbolTable.VariableSymbol) symTable.lookup(left);
      RetInstruction ret = null;
      String result = "";
      Expression.RetExpression rightRet = right.toIR();

      if(leftRet != null) {
        // We check if the types mismatches
        if(!leftRet.type.equals(rightRet.type)) {
          throw new TypeException("type mismatch: have " + leftRet.type + " and " + rightRet.type);
        }

        // new store instruction result = left := right
        Llvm.Instruction aff = new Llvm.Aff(rightRet.type.toLlvmType(), rightRet.result, leftRet.type.toLlvmType(), "%" + leftRet.ident);

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

  // Warning: this is the type from VSL+, not the LLVM types!
  static public abstract class Type {
    public abstract String pp();
    public abstract Llvm.Type toLlvmType();
  }

  static class IntType extends Type {
    public String pp() {
      return "INT";
    }

    @Override public boolean equals(Object obj) {
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

    public boolean equals(Object obj) {
      return obj instanceof VoidType;
    }

    public Llvm.Type toLlvmType() {
      return new Llvm.VoidType();
    }
  }
}
