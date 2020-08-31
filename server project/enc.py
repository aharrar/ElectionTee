from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding

def encrypt(msg):
    pub_file = open("server_public_key.pem",'rb')
    public_k =serialization.load_pem_public_key( pub_file.read(), backend=default_backend())
    pub_file.close()
    return public_k.encrypt(msg,padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),algorithm=hashes.SHA256(),label=None))

def decrypt(msg):
    priv_file = open("server_private_key.pem",'rb')
    private_k = serialization.load_pem_private_key(priv_file.read(), password=None,backend=default_backend())
    priv_file.close()
    return private_k.decrypt(msg,padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),algorithm=hashes.SHA256(), label=None))

def encrypt_from(msg,file_name):#In the file there is the public key.
    pub_file = open(file_name,'rb')
    public_k =serialization.load_pem_public_key( pub_file.read(), backend=default_backend())
    pub_file.close()
    return public_k.encrypt(msg,padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),algorithm=hashes.SHA256(),label=None))
  