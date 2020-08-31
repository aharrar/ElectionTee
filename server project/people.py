

voteings = [324215458,324215459,211762282,211762281]
voted=[]
choices =[['bibi',0],['gants',0],['meretz',0],['benet',0],['white',0]]

def can_vote(id):
    for v in voteings:
        if id == v:
            return True
    return False

def vote(id):
    voteings.remove(id)
    print(voteings)
    voted.append(id)

def choosing(choice):
    if choice < len(choices):
        choices[choice][1] += 1
        return True
    return False
            

        