from __future__ import unicode_literals
from Crypto import Random
from Crypto.PublicKey import RSA
import enc
#import ast




random_generator = Random.new().read
key = RSA.generate(2048, random_generator) #generate pub and priv key
public_key = key.publickey() # pub key export for exchange

def start_encryption():
    random_generator = Random.new().read
    key = RSA.generate(2048, random_generator) #generate pub and priv key
    public_key = key.publickey() # pub key export for exchange
    return public_key



def dal_encode(msg):   
      #return enc.encrypt_from(msg,'dal_public_key.pem')
      return msg

def dal_decode(msg):
     # return enc.decrypt(msg)
      return msg


#For now there is no encription between host to center.
def host_encode(msg):
    return msg
def host_decode(msg):
    return msg

#For setting the keys.
def set_server_priv(priv):
    file = open("serverPrivate.txt",'w')
    file.write(priv)
    file.close
    
def set_server_pub(pub):
    file = open("serverPublic.txt",'w')
    file.write(pub)
    file.close
    


