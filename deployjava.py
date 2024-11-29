import paramiko
import os
import zipfile
from dotenv import load_dotenv

def load_environment_variables():
    """
    Defina as variáveis de ambiente para o acesso ao servidor remoto.
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

        # Opcional: Se precisar executar algum comando após o envio do arquivo .jar, como reiniciar um serviço
        # execute_ssh_command(ssh_client, "systemctl restart meu-servico")

    finally:
        if ssh_client:
            ssh_client.close()

if __name__ == "__main__":
    deploy_application()
