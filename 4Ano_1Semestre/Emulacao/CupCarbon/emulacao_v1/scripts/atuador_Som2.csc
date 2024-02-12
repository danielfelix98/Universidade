loop
receive y
rdata y string_Som Som

if ((Som>62) || (Som < 53))
	mark 1
	data envio "Area 2-> Alerta Som:" Som
	printfile envio
	time tempo
	cprint 33  38  tempo  envio
else 
	mark 0

end 



delay 1000