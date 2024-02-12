loop
receive y
rdata y string_som som
receive x
rdata x string_co2 co2
receive z
rdata z string_humidade humidade
receive w
rdata w string_temperatura temperatura
print string_temperatura temperatura string_som som string_co2 co2 string_humidade humidade
data envio temperatura humidade co2 som
send envio 7
delay 1000
