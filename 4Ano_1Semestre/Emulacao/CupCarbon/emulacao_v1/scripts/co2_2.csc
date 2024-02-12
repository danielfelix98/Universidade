set num 0
loop
areadsensor v
rdata v a b valor
if (num<2)	
	set valor 0
	data p "CO2: " valor 
	print p
	send p 16
	send p 27
	inc num
else	
	data p "CO2: " valor 
	print p
	send p 10
	send p 27
end
delay 1000