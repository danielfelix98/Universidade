loop
receive y
rdata y string_Temp Temp

if ((Temp>40) || (Temp < 5))
	mark 1
	data envio "Area 1-> Alerta Temperatura:" Temp
	printfile envio
	time tempo
	cprint 11  30  tempo  envio
else 
	mark 0

end 



delay 1000