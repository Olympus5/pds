; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%test = alloca i32
%pipi = alloca i32,i32 19
%lol = alloca i32
%tmp1 = mul i32 3, 4
%tmp2 = add i32 %tmp1, 17
ret i32 %tmp2
}

