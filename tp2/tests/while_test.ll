; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
%test = alloca i32
%res = alloca i32
br label %LOOP1
LOOP1: 
%test1 = load i32, i32* %test
%tmp1 = sub i32 %test1, 4
%tmp2 = icmp ne i32 %tmp1, 0
br i1 %tmp2, label %DO2, label %DONE3
DO2: 
%test2 = load i32, i32* %test
%tmp3 = sub i32 %test2, 1
store i32 %tmp3, i32* %res
br label %LOOP1
DONE3: 
ret i32 0
}

