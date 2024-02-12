set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "Temperatura: " valor 
	print p
	send p 10
	send p 27
	inc num
else	
	data p "Temperatura: " valor 
	print p
	send p 10
	send p 30
	time tempo
	cprint 11  10  tempo  p
end
delay 1000
