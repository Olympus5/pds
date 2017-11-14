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
ret i32 0
}

