set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "Som: " valor 
	print p
	send p 10
	inc num
else	
	data p "Som: " valor 
	print p
	send p 10
	send p 31
	time tempo
	cprint 8  10  tempo  p
end
delay 1000
