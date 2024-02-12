loop
receive y
rdata y string_co2 co2

if ((co2>1100) || (co2 < 600))
	mark 1
	data envio "Area 1-> Alerta CO2:" co2
	printfile envio
	time tempo
	cprint 6  28  tempo  envio
else 
	mark 0

end 



delay 1000