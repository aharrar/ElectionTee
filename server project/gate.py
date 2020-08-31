#comuticati;on to the outside world
import socket
import encryption as enc
import main




def start(number_of_choices):
    print("started")
    serv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serv.bind(('192.168.1.16', 8080))
    serv.listen(5)
    while True:
        conn, addr = serv.accept()
        while True:
            data = conn.recv(4096)
            if not data: break
            #print(data)
            if data[0] == 0:
                for0(conn,data[1:])
            elif data[0] == 1:
                for1(conn,data[1:],number_of_choices)
            elif data[0] == 2:
                for2(conn,data[1:])
            else:
                notmatch(conn)         
        conn.close()
        print('client disconnected')
    
#from host - establish 
def for0(connection,a):
    print('for0')
    msg_from_client = a.decode('utf-8')
    print(msg_from_client)
    connection.send(b'\x00'+"resume working".encode())
    
#From dal.    
def for1(connection,a,number_of_choices):
    #temp=enc.dal_encode(b'\x00\x03\x02\x04\x02\x01\x05\x04\x05\x08')
   # print(temp)
    #print(enc.dal_decode(temp))
    print('for1')
    sent = enc.dal_decode(a)
    #print(sent)
    l = [6] #when there is protocol error.
    to_dal = bytes(l)
    if sent[0] == 0:
        #permission
        print('permission')
        b = sent[1:]
        id = 10**8*b[0]+10**7*b[1]+10**6*b[2]+10**5*b[3]+10**4*b[4]+10**3*b[5]+10**2*b[6]+10**1*b[7]+10**0*b[8]
        print(id)
        if main.ask_permission(id):
            print(True)
            l = [0,1]
        else:
            print(False)
            l = [0,0]
        to_dal =bytes(l)
    elif sent[0] == 1:
        #new envelope
        print('new envelope')
        choice = sent[1]        
        if number_of_choices > choice and main.new_vote(choice):
            l = [1,1]
        else:
            l = [1,0]
        to_dal = bytes(l)
    elif sent[1] == 2:
        #others
        print('others')
    else:
        print("PROTOCOL_ERROR")
    to_dal = enc.dal_encode(to_dal)
    connection.send(b'\x01'+to_dal)
        
    
      
 #new vote   
def for2(connection,a):
    print(22222)
    msg_from_client = a.decode('utf-8')
    print(msg_from_client)
    connection.send("bialik".encode())
    
#Not match  enithing   
def notmatch(connection):
    print("2")
    connection.send("PROTOCOL_ERROR".encode())
    