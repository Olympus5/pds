; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

; ModuleID = 'main'


define i32 @main(){
entry: 
%test = alloca i32
store i32 0, i32* %test
store i32 4, i32* %test
%test1 = load i32, i32* %test
%tmp1 = mul i32 2, 30
%tmp2 = sub i32 %test1, %tmp1
store i32 %tmp2, i32* %test
ret i32 0
}

