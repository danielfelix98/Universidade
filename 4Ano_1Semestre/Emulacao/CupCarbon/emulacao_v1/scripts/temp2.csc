set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "Temperatura: " valor 
	print p
	send p 32
	send p 37
	inc num
else	
	data p "Temperatura: " valor 
	print p
	send p 32
	send p 37
	time tempo
	cprint 34  32  tempo  p
end
delay 1000