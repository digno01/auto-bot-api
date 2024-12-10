import paramiko
import os
import zipfile
from dotenv import load_dotenv
import time
import warnings
from cryptography.utils import CryptographyDeprecationWarning
import sys

# Suprimir o warning do TripleDES
warnings.filterwarnings("ignore", category=CryptographyDeprecationWarning)

def progress_bar(current, total, width=50):
    """
    Cria uma barra de progresso personalizada.
    """
    progress = float(current) / total
    filled = int(width * progress)
    bar = '=' * filled + '-' * (width - filled)
    percent = progress * 100
    return f'\rProgresso: [{bar}] {percent:.1f}% ({current}/{total} bytes)', percent == 100

def progress_callback(current, total):
    """
    Função de callback para mostrar o progresso do upload.
    """
    bar, done = progress_bar(current, total)
    sys.stdout.write(bar)
    sys.stdout.flush()
    if done:
        sys.stdout.write('\n')

def zip_file(local_file, zip_filename):
    """
    Compacta um único arquivo local em um arquivo zip com barra de progresso.
    """
    print(f"Compactando o arquivo {local_file} em {zip_filename}")
    file_size = os.path.getsize(local_file)

    with zipfile.ZipFile(zip_filename, 'w', zipfile.ZIP_DEFLATED) as zipf:
        # Simulando progresso durante a compactação
        progress_callback(0, file_size)
        zipf.write(local_file, os.path.basename(local_file))
        progress_callback(file_size, file_size)

    print(f"\nArquivo {local_file} compactado com sucesso em {zip_filename}.")

def load_environment_variables():
    """
    Define as variáveis de ambiente para o acesso ao servidor remoto.
    """
    global hostname, port, username, password, remote_directory, local_directory
    hostname = "87.120.116.40"
    port = 22
    username = "root"
    password = "x54tjlCeBIqE@57"
    remote_directory = "/var/java"
    local_directory = "target"

def create_ssh_client():
    """
    Cria e retorna um cliente SSH conectado ao servidor.
    """
    print("Conectando ao servidor...")
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(hostname, port, username, password)
    print("Conexão estabelecida com sucesso!")
    return client

def execute_ssh_command(ssh_client, command):
    """
    Executa um comando remoto via SSH e retorna a saída.
    """
    print(f"\nExecutando comando: {command}")
    stdin, stdout, stderr = ssh_client.exec_command(command)
    exit_status = stdout.channel.recv_exit_status()
    output = stdout.read().decode('utf-8')
    error = stderr.read().decode('utf-8')

    if exit_status == 0:
        print(f"✓ Comando executado com sucesso")
        if output:
            print(f"Saída: {output}")
    else:
        print(f"✗ Erro ao executar o comando: {error}")

    return exit_status, output, error

def transfer_file(ssh_client, local_path, remote_path):
    """
    Transfere um arquivo local para o servidor remoto usando SFTP com barra de progresso.
    """
    try:
        file_size = os.path.getsize(local_path)
        print(f"\nIniciando transferência do arquivo: {os.path.basename(local_path)}")
        print(f"Tamanho do arquivo: {file_size/1024/1024:.2f} MB")

        with ssh_client.open_sftp() as sftp:
            sftp.put(local_path, remote_path, callback=progress_callback)

        print("\n✓ Transferência completada com sucesso!")

    except Exception as e:
        print(f"\n✗ Erro durante a transferência: {str(e)}")
        raise

def create_restart_script(ssh_client, remote_directory):
    """
    Cria o script restartjava.sh no servidor remoto com o conteúdo melhorado.
    """
    restart_script_content = """#!/bin/bash

# Configurações
PORT=8080
JAR_FILE="auth-api-0.0.1-SNAPSHOT.jar"
LOG_FILE="log.txt"
JAVA_OPTS="-Xms16g -Xmx16g"

echo "$(date): Iniciando script de restart" >> /var/java/log.txt

# Encontra o PID do processo usando a porta
PID=$(lsof -t -i:$PORT)

if [ -n "$PID" ]; then
    echo "$(date): Finalizando processo existente (PID: $PID)" >> /var/java/log.txt
    kill -9 $PID
    sleep 5
fi

# Verifica se o arquivo JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo "$(date): ERRO - Arquivo $JAR_FILE não encontrado" >> /var/java/log.txt
    exit 1
fi

# Verifica se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "$(date): ERRO - Java não está instalado" >> /var/java/log.txt
    exit 1
fi

# Inicia a aplicação
echo "$(date): Iniciando aplicação Java" >> /var/java/log.txt
nohup java $JAVA_OPTS -jar $JAR_FILE > $LOG_FILE 2>&1 &

# Verifica se o processo iniciou
sleep 10
NEW_PID=$(lsof -t -i:$PORT)
if [ -n "$NEW_PID" ]; then
    echo "$(date): Aplicação iniciada com sucesso (PID: $NEW_PID)" >> /var/java/log.txt
else
    echo "$(date): ERRO - Falha ao iniciar a aplicação" >> /var/java/log.txt
    tail -n 50 $LOG_FILE >> /var/java/log.txt
fi
"""
    restart_script_path = os.path.join(remote_directory, "restartjava.sh")

    with ssh_client.open_sftp() as sftp:
        with sftp.open(restart_script_path, 'w') as file_handle:
            file_handle.write(restart_script_content)

    execute_ssh_command(ssh_client, f"chmod +x {restart_script_path}")
    print(f"✓ Script {restart_script_path} criado e com permissão de execução.")

def deploy_application():
    load_environment_variables()

    jar_file_name = "auth-api-0.0.1-SNAPSHOT.jar"
    local_jar_path = os.path.join(local_directory, jar_file_name)
    remote_jar_path = "/var/java/auth-api.zip"
    local_zip_path = os.path.join(local_directory, "auth-api.zip")

    print("\n=== Iniciando processo de deploy ===\n")

    if not os.path.exists(local_jar_path):
        print(f"✗ Erro: O arquivo {local_jar_path} não foi encontrado.")
        return

    # Compactar o arquivo .jar
    zip_file(local_jar_path, local_zip_path)

    if not os.path.exists(local_zip_path):
        print(f"✗ Erro: O arquivo zip {local_zip_path} não foi criado.")
        return

    try:
        ssh_client = create_ssh_client()

        # Criar diretório e verificar permissões
        print("\n=== Preparando diretório remoto ===")
        execute_ssh_command(ssh_client, f"mkdir -p {remote_directory}")
        execute_ssh_command(ssh_client, f"chmod 755 {remote_directory}")

        # Transferir e extrair arquivo
        print("\n=== Transferindo e extraindo arquivo ===")
        transfer_file(ssh_client, local_zip_path, remote_jar_path)
        execute_ssh_command(ssh_client, f"cd {remote_directory} && unzip -o {remote_jar_path}")
        execute_ssh_command(ssh_client, f"rm {remote_jar_path}")

        # Criar e executar script de restart
        print("\n=== Configurando e executando restart ===")
        create_restart_script(ssh_client, remote_directory)

        print("\n⌛ Aguardando 5 segundos antes de executar o script de restart...")
        time.sleep(5)

        # Executar o script e verificar o log
        _, _, _ = execute_ssh_command(ssh_client, f"cd {remote_directory} && bash restartjava.sh")
        print("\n⌛ Aguardando 10 segundos para o serviço iniciar...")
        time.sleep(10)

        # Verificar o log de deploy
        print("\n=== Verificando logs ===")
        _, output, _ = execute_ssh_command(ssh_client, "tail -n 50 /var/java/log.txt")
        print("\nConteúdo do log de deploy:")
        print(output)

        # Verificar se o processo Java está rodando
        _, output, _ = execute_ssh_command(ssh_client, "ps aux | grep java | grep -v grep")
        if output:
            print("\n✓ Processo Java encontrado:")
            print(output)
        else:
            print("\n✗ Nenhum processo Java encontrado!")

            # Verificar o log da aplicação
            _, output, _ = execute_ssh_command(ssh_client, "tail -n 50 /var/java/log.txt")
            print("\nÚltimas linhas do log da aplicação:")
            print(output)

        print("\n=== Deploy finalizado ===")

    except Exception as e:
        print(f"\n✗ Erro durante o deploy: {str(e)}")
    finally:
        if ssh_client:
            ssh_client.close()
            print("\n✓ Conexão SSH encerrada")

if __name__ == "__main__":
    deploy_application()