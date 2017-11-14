; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%test = alloca i32
%tmp1 = sub i32 4, 4
%tmp2 = add i32 32, 15
%tmp3 = mul i32 %tmp1, %tmp2
%tmp4 = mul i32 %tmp3, 34
%tmp5 = udiv i32 17, %tmp4
store i32 %tmp5, i32* %test
%test1 = load i32, i32* %test
%tmp6 = add i32 3, %test1
%tmp7 = sub i32 2, %tmp6
store i32 %tmp7, i32* %test
%test2 = load i32, i32* %test
%tmp8 = sub i32 %test2, 4
%tmp9 = icmp ne i32 %tmp8, 0
br i1 %tmp9, label %IF1, label %FI2
IF1: 
store i32 0, i32* %test
br label %FI2
FI2: 
ret i32 0
}

