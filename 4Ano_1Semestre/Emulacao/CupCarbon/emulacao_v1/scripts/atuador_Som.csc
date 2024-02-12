loop
receive y
rdata y string_Som Som

if ((Som>62) || (Som < 53))
	mark 1
	data envio "Area 1-> Alerta Som:" Som
	printfile envio
	time tempo
	cprint 8  31  tempo  envio
else 
	mark 0

end 



delay 1000