	0x002e 00046 (main.go:6)	LEAQ	type.sync.Mutex(SB), AX
	0x0035 00053 (main.go:6)	MOVQ	AX, (SP)
	0x0039 00057 (main.go:6)	PCDATA	$1, $1
	0x0039 00057 (main.go:6)	CALL	runtime.newobject(SB)
	0x003e 00062 (main.go:6)	MOVQ	8(SP), AX
	0x0043 00067 (<unknown line number>)	NOP
	0x0043 00067 (main.go:6)	MOVQ	AX, CX
	0x0046 00070 ($GOROOT/src/sync/mutex.go:74)	XORL	AX, AX
	0x0048 00072 ($GOROOT/src/sync/mutex.go:74)	MOVL	$1, DX
	0x004d 00077 ($GOROOT/src/sync/mutex.go:74)	LOCK
	0x004e 00078 ($GOROOT/src/sync/mutex.go:74)	CMPXCHGL	DX, (CX)
	0x0051 00081 ($GOROOT/src/sync/mutex.go:74)	SETEQ	AL
	0x0054 00084 ($GOROOT/src/sync/mutex.go:74)	TESTB	AL, AL
	0x0056 00086 ($GOROOT/src/sync/mutex.go:74)	JEQ	134
	0x0058 00088 (main.go:9)	LEAQ	sync.(*Mutex).Unlock·f(SB), AX
	0x005f 00095 (main.go:9)	MOVQ	AX, ""..autotmp_7+40(SP)
	0x0064 00100 (main.go:9)	MOVQ	CX, ""..autotmp_8+32(SP)
	0x0069 00105 (main.go:11)	MOVB	$0, ""..autotmp_6+23(SP)
	0x006e 00110 (main.go:11)	MOVQ	""..autotmp_8+32(SP), AX
	0x0073 00115 (main.go:11)	MOVQ	AX, (SP)
	0x0077 00119 (main.go:11)	CALL	sync.(*Mutex).Unlock(SB)
