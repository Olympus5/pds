; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%test = alloca i32
%res = alloca i32
%test1 =  load i32, i32* %test
%tmp1 = mul i32 %test1, 4
store i32 %tmp1, i32* %res
ret i32 0
}

