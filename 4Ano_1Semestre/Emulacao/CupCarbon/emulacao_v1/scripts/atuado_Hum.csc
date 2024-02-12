loop
receive y
rdata y string_Hum Hum

if ((Hum>100) || (Hum < 0))
	mark 1
	data envio "Area 1-> Alerta Humidade:" Hum
	printfile envio
	time tempo
	cprint 7  29  tempo  envio
else 
	mark 0

end 



delay 1000