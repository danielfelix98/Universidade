from flask import Flask, jsonify, render_template, request, redirect, url_for
from flask_login import LoginManager, UserMixin, login_user, login_required, logout_user, current_user
import pymongo
from bson import ObjectId

app = Flask(__name__)
app.secret_key = 'sua_chave_secreta'

# Configuração do cliente MongoDB para autenticação
mongo_client_auth = pymongo.MongoClient("mongodb://10.0.1.19:27017/")
db_auth = mongo_client_auth["AutenticacaoBaseDados"]
users_collection = db_auth["users"]

# Configuração do cliente MongoDB para dados dos sensores
mongo_client_sensors = pymongo.MongoClient("mongodb://10.0.1.17:27017/")
db_sensors = mongo_client_sensors["EmulacaoBaseDados"]
colecao_alertas = db_sensors["alertas"]
colecao_normal = db_sensors["entradas_normais"]

# Configuração do Flask-Login
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'login'

class User(UserMixin):
    def __init__(self, username):
        self.username = username

    @property
    def id(self):
        return self.username

@login_manager.user_loader
def load_user(username):
    user = users_collection.find_one({"username": username})
    if not user:
        return None
    return User(username=user['username'])

@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('home'))
    
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']

        user = users_collection.find_one({"username": username, "password": password})
        if user:
            user_obj = User(username=user['username'])
            login_user(user_obj)
            return redirect(url_for('home'))
        else:
            return 'Login falhou!'

    return render_template('login.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if current_user.is_authenticated:
        return redirect(url_for('home'))

    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']

        user_exists = users_collection.find_one({"username": username})
        if user_exists:
            return 'Usuário já existe!'

        users_collection.insert_one({"username": username, "password": password})
        return redirect(url_for('login'))

    return render_template('register.html')

@app.route('/logout', methods=['POST'])
@login_required
def logout():
    logout_user()
    return redirect(url_for('login'))

@app.route('/dados-sensores')
@login_required
def dados_sensores():
    entradas_normais = list(colecao_normal.find().sort('timestamp', pymongo.DESCENDING).limit(30))
    # Process each document and convert ObjectId to string
    entradas_processadas = [processar_entrada_normal(documento) for documento in entradas_normais]

    alertas = list(colecao_alertas.find().sort('timestamp', pymongo.DESCENDING).limit(30))
    # Process each document and convert ObjectId to string
    alertas_processadas = [processar_alerta(documento) for documento in alertas]

    # Return the processed data, which now should be serializable to JSON
    return jsonify({'entradas_normais': entradas_processadas, 'alertas': alertas_processadas})

@app.route('/')
@login_required
def home():
    # Obter e processar as entradas normais
    dados_brutos_entradas_normais = list(colecao_normal.find().sort('timestamp', pymongo.DESCENDING).limit(30))
    entradas_normais = [processar_entrada_normal(documento) for documento in dados_brutos_entradas_normais]

    # Obter e processar os alertas
    dados_brutos_alertas = list(colecao_alertas.find().sort('timestamp', pymongo.DESCENDING).limit(30))
    alertas = [processar_alerta(documento) for documento in dados_brutos_alertas]

    return render_template('home.html', alertas=alertas, entradas_normais=entradas_normais)

def processar_entrada_normal(documento):
    # Split the message by spaces and then process each segment.
    partes = documento['mensagem'].split()
    resultado = {
        'area': partes[1].strip(':'),  # Assuming 'Area' is always followed by the area number.
        'Temperatura': partes[3],
        'Som': partes[5],
        'CO2': partes[7],
        'Humidade': partes[9],
        'timestamp': documento['timestamp'].isoformat(),
        '_id': str(documento['_id'])
    }
    return resultado

def processar_alerta(documento):
    # Split the message into parts to extract alert information.
    partes = documento['mensagem'].split('->')
    alerta_info = partes[1].split(':')
    
    resultado = {
        'area': partes[0].split()[1],  # Assuming 'Area' is always followed by the area number.
        'tipo_alerta': alerta_info[0].strip(),
        'valor': alerta_info[1].strip(),
        'timestamp': documento['timestamp'].isoformat(),
        '_id': str(documento['_id'])
    }
    return resultado






if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)
