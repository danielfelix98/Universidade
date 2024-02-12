set a 0
loop

if (a>2)
	receive y
	rdata y string_som som
	receive x
	rdata x string_co2 co2
	receive z
	rdata z string_humidade humidade
	receive w
	rdata w string_temperatura temperatura
	data envio "Area 1:" string_temperatura temperatura string_som som string_co2 co2 string_humidade humidade
	print envio	
	printfile envio	
else
	inc a
end
delay 1000


