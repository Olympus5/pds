; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%res = alloca i32
%test = alloca i32
%res1 = load i32, i32* %res
%tmp1 = sub i32 %res1, 4
%tmp2 = icmp ne i32 %tmp1, 0
br i1 %tmp2, label %IF1, label %ELSE2
IF1: 
%test2 = load i32, i32* %test
%tmp3 = mul i32 %test2, 4
store i32 %tmp3, i32* %res
br label %FI3
ELSE2: 
%test3 = load i32, i32* %test
%tmp4 = mul i32 %test3, 30
store i32 %tmp4, i32* %res
br label %FI3
FI3: 
ret i32 0
}

