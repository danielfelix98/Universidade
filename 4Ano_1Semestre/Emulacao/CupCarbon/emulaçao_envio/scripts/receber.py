
# -*- coding: utf-8 -*-
import os
import threading
import tailer
import time
from paho.mqtt import client as mqtt_client

# Configurações do Load Balancer para os Brokers MQTT
broker_lb = "emulacao.pt"  # IP ou hostname do Load Balancer
port_lb = 1883
topic = "meu/topico"

# Função para conectar ao MQTT Broker via Load Balancer
def connect_mqtt() -> mqtt_client.Client:
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker via Load Balancer!")
        else:
            print("Failed to connect, return code %d\n", rc)

    client = mqtt_client.Client()
    client.on_connect = on_connect
    client.connect(broker_lb, port_lb)
    return client

# Função para publicar mensagens no broker MQTT via Load Balancer
def publish(message):
    client = connect_mqtt()  # Conectar ao broker via Load Balancer
    client.loop_start()
    result = client.publish(topic, message)
    status = result[0]
    if status == 0:
        print(f"Sent `{message}` to topic `{topic}` via Load Balancer")
    else:
        print(f"Failed to send message to topic {topic} via Load Balancer")
    client.loop_stop()
    client.disconnect()  # Desconectar do broker após publicar

def readFileAndSend(file_path):
    last_line = None
    while True:
        if os.path.exists(file_path):
            with open(file_path, 'r') as file:
                lines = file.readlines()

            for line in lines:
                line = line.replace("#", " ").strip()
                if line != last_line:  # Publicar apenas se a linha for diferente da última publicada
                    print(line)
                    publish(line)
                    time.sleep(1)
                last_line = line  # Atualizar a última linha publicada

            for line in tailer.follow(open(file_path)):
                line = line.replace("#", " ").strip()
                if line != last_line:
                    print(line)
                    publish(line)
                    time.sleep(1)
                last_line = line
        else:
            time.sleep(1)


# Função principal que inicializa as threads
def main():
    # Lista de caminhos de ficheiros
    file_paths = [
        r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S10.txt",
        r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S28.txt",
        r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S29.txt",
        r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S30.txt",
        r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S31.txt",
	r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S32.txt",
	r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S37.txt",
	r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S38.txt",
	r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S39.txt",
	r"C:\Users\a96097\Desktop\emulacao_versao_fds\emulacao_v1\results\S40.txt"
    ]

    # Criar e iniciar threads para cada ficheiro
    threads = []
    for file_path in file_paths:
        thread = threading.Thread(target=readFileAndSend, args=(file_path,))
        thread.start()
        threads.append(thread)

    # Aguardar todas as threads terminarem (opcional)
    for thread in threads:
        thread.join()

if __name__ == '__main__':
    main()

