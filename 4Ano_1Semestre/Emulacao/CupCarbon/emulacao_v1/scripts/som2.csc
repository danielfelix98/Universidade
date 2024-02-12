set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "Som: " valor 
	print p
	send p 32
	send p 38
	inc num
else	
	data p "Som: " valor 
	print p
	send p 32
	send p 38
	time tempo
	cprint 33  32  tempo  p
end
delay 1000
