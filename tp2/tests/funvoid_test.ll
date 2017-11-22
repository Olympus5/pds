; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

; ModuleID = 'main'


define i32 @main(){
entry: 
%test = alloca i32
%tmp1 = mul i32 4, 2
store i32 %tmp1, i32* %test
%test1 = load i32, i32* %test
%tmp2 = udiv i32 %test1, 4
store i32 %tmp2, i32* %test
%tmp3 = icmp ne i32 0, 0
br i1 %tmp3, label %IF1, label %ELSE2
IF1: 
%test2 = load i32, i32* %test
%tmp4 = sub i32 %test2, 1
store i32 %tmp4, i32* %test
br label %FI3
ELSE2: 
store i32 0, i32* %test
br label %FI3
FI3: 
ret i32 0
}

define void @fact(i32 %n){
entry: 
%n1 = alloca i32
store i32 %n, i32* %n1
%n3 = load i32, i32* %n1
%tmp5 = sub i32 %n3, 17
%tmp6 = icmp ne i32 %tmp5, 0
br i1 %tmp6, label %IF4, label %ELSE5
IF4: 
%n4 = load i32, i32* %n1
%tmp7 = sub i32 32, %n4
store i32 %tmp7, i32* %n1
br label %FI6
ELSE5: 
%n5 = load i32, i32* %n1
%tmp8 = add i32 %n5, 43
store i32 %tmp8, i32* %n1
br label %FI6
FI6: 
ret void 
}


