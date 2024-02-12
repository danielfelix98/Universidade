set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "Humidade: " valor 
	print p
	send p 32
	send p 40
	inc num
else	
	data p "Humidade: " valor 
	print p
	send p 32
	send p 40
	time tempo
	cprint 35  32  tempo  p
end
delay 1000
