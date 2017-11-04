; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%test = alloca i32
%tmp1 = sub i32 30, 7
%tmp2 = mul i32 4, %tmp1
store i32 %tmp2, i32* %test
ret i32 0
}

