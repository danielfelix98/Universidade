# -*- coding: utf-8 -*-
from paho.mqtt import client as mqtt_client
import time

# Broker
broker = "broker.hivemq.com"
port = 1883

file_path = r"C:\Users\luisf\OneDrive\Documentos\emulacao_v1\results\S16.txt"

# Topic
topic = "emulacao"

print("getid", flush=True)
id = input()
client_id = "cupcarbon" + id

def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!", flush=True)
        else:
            print("Failed to connect, return code ", rc, flush=True)

    # Set Connecting Client ID
    client = mqtt_client.Client(client_id)
    # client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client

def publish(client, file_path, topic):
    with open(file_path, 'r') as file:
        while True:
            # Lê uma linha do arquivo
            line = file.readline().strip()
            
            # Verifica se atingiu o final do arquivo
            if not line:
                print("print Fim do arquivo. Reiniciando a leitura.")
                file.seek(0)
                time.sleep(1)
                continue
            
            # Remove os caracteres "#" da linha
            line = line.replace("#", "")
            client.publish(topic, line)
            print("print Enviei: {}".format(line), flush=True)
            
            # Aguarda 1 segundo antes de ler a próxima linha
            time.sleep(1)

def run():
    client = connect_mqtt()
    client.loop_start()
    publish(client, file_path, topic)  # Corrigido: Adicionando argumentos adequados
    client.loop_stop()

if __name__ == '__main__':
    run()