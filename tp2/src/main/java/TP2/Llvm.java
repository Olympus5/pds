package TP2;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

// This file contains a simple LLVM IR representation
// and methods to generate its string representation

public class Llvm {
  static public class IR {
    List<Instruction> header; // IR instructions to be placed before the code (global definitions)
    List<Instruction> code;   // main code

    public IR(List<Instruction> header, List<Instruction> code) {
      this.header = header;
      this.code = code;
    }

    // append an other IR
    public IR append(IR other) {
      header.addAll(other.header);
      code.addAll(other.code);
      return this;
    }

    // append a code instruction
    public IR appendCode(Instruction inst) {
      code.add(inst);
      return this;
    }

    // append a code header
    public IR appendHeader(Instruction inst) {
      header.add(inst);
      return this;
    }

    // Final string generation
    public String toString() {
      // This header describe to LLVM the target
      // and declare the external function printf
      StringBuilder r = new StringBuilder("; Target\n" +
        "target triple = \"x86_64-unknown-linux-gnu\"\n" +
        "; External declaration of the printf function\n" +
        "declare i32 @printf(i8* noalias nocapture, ...)\n" +
        "\n; Actual code begins\n\n");

      for(Instruction inst: header)
        r.append(inst);

      r.append("\n\n");

      // We create the function main
      //TODO: remove this when you extend the language
      //r.append("define i32 @main() {\n");


      for(Instruction inst: code)
        r.append(inst);

      //TODO: remove this when you extend the language
      //r.append("}\n");

      return r.toString();
    }
  }

  // Returns a new empty list of instruction, handy
  static public List<Instruction> empty() {
    return new ArrayList<Instruction>();
  }


  // LLVM Types
  static public abstract class Type {
    public abstract String toString();
  }

  static public class IntType extends Type {
    public String toString() {
      return "i32";
    }
  }

  static public class VoidType extends Type {
    public String toString() {
      return "void";
    }
  }

  //TODO: other types


  // LLVM IR Instructions
  static public abstract class Instruction {
    public abstract String toString();
  }

  static public class Add extends Instruction {
    Type type;
    String left;
    String right;
    String lvalue;

    public Add(Type type, String left, String right, String lvalue) {
      this.type = type;
      this.left = left;
      this.right = right;
      this.lvalue = lvalue;
    }

    public String toString() {
      return lvalue + " = add " + type + " " + left + ", " + right +  "\n";
    }
  }

  static public class Return extends Instruction {
    Type type;
    String value;

    public Return(Type type, String value) {
      this.type = type;
      this.value = value;
    }

    public String toString() {
      return "ret " + type + " " + value + "\n";
    }
  }

  static public class Sub extends Instruction {
    Type type;
    String left;
    String right;
    String lvalue;

    public Sub(Type type, String left, String right, String lvalue) {
      this.type = type;
      this.left = left;
      this.right = right;
      this.lvalue = lvalue;
    }

    public String toString() {
      return lvalue + " = sub " + type + " " + left + ", " + right +  "\n";
    }
  }

  static public class Mul extends Instruction {
    Type type;
    String left;
    String right;
    String lvalue;

    public Mul(Type type, String left, String right, String lvalue) {
      this.type = type;
      this.left = left;
      this.right = right;
      this.lvalue = lvalue;
    }

    public String toString() {
      return lvalue + " = mul " + type + " " + left + ", " + right +  "\n";
    }
  }

  static public class Div extends Instruction {
    Type type;
    String left;
    String right;
    String lvalue;

    public Div(Type type, String left, String right, String lvalue) {
      this.type = type;
      this.left = left;
      this.right = right;
      this.lvalue = lvalue;
    }

    public String toString() {
      return lvalue + " = udiv " + type + " " + left + ", " + right +  "\n";
    }
  }

  static public class VarInt extends Instruction {
    Type type;
    String lvalue;

    public VarInt(Type type, String lvalue) {
      this.type = type;
      this.lvalue = lvalue;
    }

    public String toString() {
      return lvalue + " = alloca " + type + "\n";
    }
  }

  //TODO: A modifier pour gérer la deuxieme table des symboles
  static public class VarTab extends Instruction {
    Type type1;
    Type type2;
    int size;
    String lvalue;

    public VarTab(Type type1, Type type2, int size, String lvalue) {
      this.type1 = type1;
      this.type2 = type2;
      this.size = size;
      this.lvalue = lvalue;
    }

    public String toString() {
      return lvalue + " = alloca " + type1 + "," + type2 + " " + size + "\n";
    }
  }

  static public class Aff extends Instruction {
    Type typeValue;
    String value;
    Type typePtr;
    String ptr;

    public Aff(Type typeValue, String value, Type typePtr, String ptr) {
      this.typeValue = typeValue;
      this.value = value;
      this.typePtr = typePtr;
      this.ptr = ptr;
    }

    public String toString() {
      return "store " + typeValue + " " + value + ", " + typePtr + "* " + ptr + "\n";
    }
  }

  static public class Load extends Instruction {
    Type typeValue;
    Type typePtr;
    String ptr;
    String lvalue;

    public Load(Type typeValue, String lvalue, Type typePtr, String ptr) {
      this.typeValue = typeValue;
      this.lvalue = lvalue;
      this.typePtr = typePtr;
      this.ptr = ptr;
    }

    public String toString() {
      return this.lvalue + " = load " + this.typeValue + ", " + this.typePtr + "* " + this.ptr + "\n";
    }
  }

  static public class Icmp extends Instruction {
    Type typeCond;
    String op1;
    String lvalue;

    public Icmp(Type typeCond, String op1, String lvalue) {
      this.typeCond = typeCond;
      this.op1 = op1;
      this.lvalue = lvalue;
    }

    public String toString() {
      return this.lvalue + " = icmp ne " + this.typeCond + " " + this.op1 + ", 0\n";
    }
  }

  static public class BrCond extends Instruction {
    String cond;
    String label1;
    String label2;

    public BrCond(String cond, String label1, String label2) {
      this.cond = cond;
      this.label1 = label1;
      this.label2 = label2;
    }

    public String toString() {
      return "br i1 " + cond + ", label " + label1 + ", label " + label2 + "\n";
    }
  }

  static public class BrUncond extends Instruction {
    String label;

    public BrUncond(String label) {
      this.label = label;
    }

    public String toString() {
      return "br label " + label + "\n";
    }
  }

  static public class Label extends Instruction {
    String name;

    public Label(String name) {
      this.name = name;
    }

    public String toString() {
      return name + ": \n";
    }
  }
  //TODO: Fonction
  static public class Define extends Instruction {
      Type typeFunction;
      String name;
      List<String> attrs;

      public Define(Type typeFunction, String name, List<String> attrs) {
          this.typeFunction = typeFunction;
          this.name = name;
          this.attrs = attrs;
      }

      public String toString() {
          String ret = "define " + this.typeFunction + " " + this.name + "(";
          if(!attrs.isEmpty()) {
              Iterator<String> it = attrs.iterator();

              ret += new IntType().toString() + " %" + it.next();

              while(it.hasNext()) {
                  ret += ", " + new IntType().toString() + " %" + it.next();
              }
          }

          return ret + "){\n";
      }
  }

  static public class Attrs extends Instruction {
      List<String> attrs;

      public Attrs(List<String> attrs) {
          this.attrs = attrs;
      }

      public String toString() {
          String ret = "";

          for(String attr : this.attrs) {
              ret += "%" + attr + "1 = alloca i32\n";
              ret += "store i32 %" + attr + ", i32* %" + attr + "1\n";
          }

          return ret ;
      }
  }

  static public class EndFunction extends Instruction {

      public EndFunction() {
      }

      public String toString() {
          return "}\n\n";
      }
  }
  //TODO: Tableau: alloca et getelementptr


  static public class Commentary extends Instruction {
      String comment;

      public Commentary(String comment) {
          this.comment = comment;
      }

      public String toString() {
          return "; " + comment + "\n";
      }
  }
}
