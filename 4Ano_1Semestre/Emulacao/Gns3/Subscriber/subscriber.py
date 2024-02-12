import paho.mqtt.client as mqtt
from pymongo import MongoClient
import datetime

# Conexão com o MongoDB
mongo_client = MongoClient('10.0.1.17', 27017)  # Altere com os detalhes do seu MongoDB
db = mongo_client['EmulacaoBaseDados']
colecao_normal = db['entradas_normais']
colecao_alertas = db['alertas']

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Conectado ao broker MQTT")
        client.subscribe("meu/topico")
    else:
        print("Falha na conexão. Código de retorno:", rc)

def on_message(client, userdata, msg):
    mensagem = msg.payload.decode()
    print(f"Nova mensagem recebida no tópico {msg.topic}: {mensagem}")
    
    # Capturar o timestamp atual
    timestamp_atual = datetime.datetime.now()

    # Decidindo em qual coleção salvar
    if "-> Alerta " in mensagem:
        colecao_alertas.insert_one({"topico": msg.topic, "mensagem": mensagem, "timestamp": timestamp_atual})
    else:
        colecao_normal.insert_one({"topico": msg.topic, "mensagem": mensagem, "timestamp": timestamp_atual})

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

broker_address = "10.0.1.15"
port = 1883

client.connect(broker_address, port, 60)
client.loop_forever()
