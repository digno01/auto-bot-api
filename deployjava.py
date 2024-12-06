import paramiko
import os
import zipfile
from dotenv import load_dotenv

def load_environment_variables():
    """
    Define as variáveis de ambiente para o acesso ao servidor remoto.
    """
    global hostname, port, username, password, remote_directory, local_directory
    hostname = "87.120.116.40"  # Endereço IP do servidor remoto
    port = 22                   # Porta SSH (normalmente 22)
    username = "root"           # Usuário SSH
    password = "x54tjlCeBIqE@57"  # Senha SSH
    remote_directory = "/var/java"  # Diretório remoto para enviar o arquivo .jar
    local_directory = "target"    # Diretório local onde o arquivo .jar está localizado

def create_ssh_client():
    """
    Cria e retorna um cliente SSH conectado ao servidor.
    """
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())  # Adiciona automaticamente chaves de host desconhecidas
    client.connect(hostname, port, username, password)
    return client

def execute_ssh_command(ssh_client, command):
    """
    Executa um comando remoto via SSH.
    """
    stdin, stdout, stderr = ssh_client.exec_command(command)
    exit_status = stdout.channel.recv_exit_status()
    if exit_status == 0:
        print(f"Comando executado com sucesso: {command}")
    else:
        print(f"Erro ao executar o comando {command}: {stderr.read().decode('utf-8')}")

def transfer_file(ssh_client, local_path, remote_path):
    """
    Transfere um arquivo local para o servidor remoto usando SFTP.
    """
    with ssh_client.open_sftp() as sftp:
        print(f"Iniciando transferência de {local_path} para {remote_path}")
        sftp.put(local_path, remote_path, callback=progress_callback)
        print("\nTransferência completada com sucesso.")

def progress_callback(transferred, total):
    """
    Função de callback para monitorar o progresso do upload.
    `transferred` indica quantos bytes foram transferidos até o momento.
    `total` indica o tamanho total do arquivo.
    """
    print(f"\rProgresso do Upload: {transferred}/{total} bytes ({(transferred / total) * 100:.2f}%)", end="")

def zip_file(local_file, zip_filename):
    """
    Compacta um único arquivo local em um arquivo zip.
    """
    print(f"Compactando o arquivo {local_file} em {zip_filename}")
    with zipfile.ZipFile(zip_filename, 'w', zipfile.ZIP_DEFLATED) as zipf:
        zipf.write(local_file, os.path.basename(local_file))  # Adiciona o arquivo com o nome correto
    print(f"Arquivo {local_file} compactado com sucesso em {zip_filename}.")

def create_restart_script(ssh_client, remote_directory):
    """
    Cria o script restartjava.sh no servidor remoto com o conteúdo especificado.
    """
    restart_script_content = """#!/bin/bash

# Porta alvo
PORT=8080

# Encontra o PID do processo usando a porta
PID=$(lsof -t -i:$PORT)

# Verifica se o processo foi encontrado
if [ -z "$PID" ]; then
  echo "Nenhum processo encontrado na porta $PORT."
else
  # Mata o processo
  kill -9 $PID
  echo "Processo na porta $PORT (PID: $PID) foi finalizado."

  # Aguarda 1 segundo
  sleep 5

  # Executa o comando java
  echo "Iniciando o processo Java..."
  nohup java -Xms16g -Xmx16g -jar auth-api-0.0.1-SNAPSHOT.jar > log.txt 2>&1 &
  echo "Processo Java iniciado."
fi
"""
    restart_script_path = os.path.join(remote_directory, "restartjava.sh")

    # Criar o script remotamente
    with ssh_client.open_sftp() as sftp:
        with sftp.open(restart_script_path, 'w') as file_handle:
            file_handle.write(restart_script_content)

    # Dar permissão de execução ao script
    execute_ssh_command(ssh_client, f"chmod +x {restart_script_path}")
    print(f"Script {restart_script_path} criado e com permissão de execução.")

def deploy_application():
    load_environment_variables()

    # Nome do arquivo .jar que será enviado
    jar_file_name = "auth-api-0.0.1-SNAPSHOT.jar"
    local_jar_path = os.path.join(local_directory, jar_file_name)  # Caminho local do arquivo .jar
    remote_jar_path = "/var/java/auth-api.zip"  # Caminho remoto do arquivo .jar
    local_zip_path = os.path.join(local_directory, "auth-api.zip")  # Caminho local para o arquivo zip

    # Verifique se o arquivo .jar existe localmente
    if not os.path.exists(local_jar_path):
        print(f"Erro: O arquivo {local_jar_path} não foi encontrado.")
        return

    # Compactar o arquivo .jar em um arquivo .zip
    zip_file(local_jar_path, local_zip_path)

    # Verifique se o arquivo .zip foi criado
    if not os.path.exists(local_zip_path):
        print(f"Erro: O arquivo zip {local_zip_path} não foi encontrado.")
        return

    # Criando o cliente SSH e conectando-se ao servidor
    ssh_client = None
    try:
        ssh_client = create_ssh_client()

        # Verificar se o diretório remoto existe, caso contrário, criá-lo
        execute_ssh_command(ssh_client, f"mkdir -p {remote_directory}")

        # Verifique se o diretório foi criado corretamente
        execute_ssh_command(ssh_client, f"ls -ld {remote_directory}")

        # Transferir o arquivo .zip para o diretório correto no servidor
        transfer_file(ssh_client, local_zip_path, remote_jar_path)

        # Descompactar o arquivo .zip no servidor
        execute_ssh_command(ssh_client, f"unzip -o {remote_jar_path} -d {remote_directory}")

        # Opcional: Se precisar remover o arquivo .zip após a extração
        execute_ssh_command(ssh_client, f"rm {remote_jar_path}")

        # Criar o script restartjava.sh remotamente
        create_restart_script(ssh_client, remote_directory)

         # Executar o script restartjava.sh
        restart_script_path = f"{remote_directory}/restartjava.sh"  # Caminho corrigido
        execute_ssh_command(ssh_client, f"bash {restart_script_path}")  # Executando o script

    finally:
        if ssh_client:
            ssh_client.close()

if __name__ == "__main__":
    deploy_application()
