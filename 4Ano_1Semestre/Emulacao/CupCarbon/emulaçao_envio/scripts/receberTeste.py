# -*- coding: utf-8 -*-
import os
import threading
import tailer
import time
import uuid  # Importando a biblioteca uuid
from paho.mqtt import client as mqtt_client

# Configurações do Broker MQTT
broker = "emulacao.pt"
port = 1883
topic = "meu/topico"

# Função para conectar ao MQTT Broker
def connect_mqtt() -> mqtt_client.Client:
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)

    client_id = f"client-{uuid.uuid4()}"  # Gerando um client_id único
    client = mqtt_client.Client(client_id)  # Usando o client_id único
    client.on_connect = on_connect
    client.connect(broker, port)
    return client

# Função para publicar mensagens no broker MQTT
def publish(client, message):
    result = client.publish(topic, message)
    status = result[0]
    if status == 0:
        print(f"Sent `{message}` to topic `{topic}`")
    else:
        print(f"Failed to send message to topic {topic}")

def readFileAndSend(client, file_path):
    while True:
        if os.path.exists(file_path):
            with open(file_path, 'r') as file:
                # Ler todas as linhas existentes no arquivo
                lines = file.readlines()

            # Publicar todas as linhas lidas inicialmente
            for line in lines:
                line = line.replace("#", " ")
                print(line)
                publish(client, line.strip())  # Use strip() para remover espaços em branco extras
                time.sleep(1)

            # Começar a seguir novas linhas a partir do final do arquivo
            for line in tailer.follow(open(file_path)):
                line = line.replace("#", " ")
                print(line)
                publish(client, line.strip())
                time.sleep(1)
        else:
            # Se o arquivo não existe, aguarde até que ele seja criado
            time.sleep(1)

# Função principal que inicializa a conexão MQTT e inicia as threads
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
    # Conectar ao broker MQTT
    mqtt_client = connect_mqtt()
    mqtt_client.loop_start()

    # Criar e iniciar threads para cada ficheiro
    threads = []
    for file_path in file_paths:
        thread = threading.Thread(target=readFileAndSend, args=(mqtt_client, file_path,))
        thread.start()
        threads.append(thread)

    # Aguardar todas as threads terminarem (opcional)
    for thread in threads:
        thread.join()

if __name__ == '__main__':
    main()
