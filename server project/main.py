# -*- coding: utf-8 -*-
import gate
import people

def start():
    gate.start(5)

#new envelope
def new_vote(choice):
    a = people.choosing(choice)
    print(people.choices)
    return a

#Before the user give the "envelope", he need to get premission and sign in.   
def ask_permission(id):
    if people.can_vote(id):
        people.vote(id)
        print(len(people.voteings),len(people.voted))
        return True
    else: 
        return False

#Notification
def notificate(msg):
    print(msg)
    
def status():
    print('voteing',people.voteings)
    print('voted',people.voted)
    print('choices',people.choices)
    
start()