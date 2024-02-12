set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "CO2: " valor 
	print p
	send p 32
	inc num
else	
	data p "CO2: " valor 
	print p
	send p 32
	send p 39
	time tempo
	cprint 36  32  tempo  p
end
delay 1000
